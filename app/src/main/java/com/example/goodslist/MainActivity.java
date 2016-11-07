package com.example.goodslist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.goodslist.adapter.ProductsAdapter;
import com.example.goodslist.data.GoodsDownloadService;
import com.example.goodslist.data.GoodsReceiver;
import com.example.goodslist.model.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoodsReceiver.DataReceivedListener {

	public static final String KEY_DATA_RECEIVER = "key_data_receiver";
	public static final String KEY_DOWNLOAD_URL = "key_download_url";
	public static final String KEY_RECEIVED_ITEMS = "key_received_items";
	public static final String KEY_RECEIVED_PAGE = "key_received_page";
	public static final String KEY_RECEIVED_PAGES_COUNT = "key_received_pages_count";

	private static final String KEY_LAYOUT_STATE = "key_layout_state";
	private static final String KEY_STATE_ARRAY_LIST = "key_state_array_list";
	private static final String KEY_STATE_CURRENT_PAGE = "key_state_current_page";
	private static final String KEY_STATE_PAGES_COUNT = "key_state_pages_count";

	private static final String DATA_URL = "https://test4.lesara.de/restapi/v1/trendproducts/?app_token=this_is_an_app_token&" +
			"user_token=63a12a8116814a9574842515378c93c64846fc3d0858def78388be37e127cd17&store_id=1&page_override=";

	private ProductsAdapter mAdapter;
	private ProgressBar mProgressBar;
	private GridLayoutManager mLayoutManager;
	private GoodsReceiver mReceiver;
	private int mCurrentPage;
	private int mPagesCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		mProgressBar = (ProgressBar) findViewById(R.id.act_main_progress);
		mAdapter = new ProductsAdapter(this);
		mLayoutManager = new GridLayoutManager(this, 2);
		//TODO use this handler instead of interface
		mReceiver = new GoodsReceiver(new Handler(), this);
		if (savedInstanceState != null) {
			mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_STATE));
			mAdapter.addProducts(savedInstanceState.<Product>getParcelableArrayList(KEY_STATE_ARRAY_LIST));
			mCurrentPage = savedInstanceState.getInt(KEY_STATE_CURRENT_PAGE);
			mPagesCount = savedInstanceState.getInt(KEY_STATE_PAGES_COUNT);
		}
		loadData();
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.act_main_recyclerview);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onDataReceived(Bundle data) {
		stopProgress();
		if (mAdapter != null) {
			mAdapter.addProducts(data.<Product>getParcelableArrayList(KEY_RECEIVED_ITEMS));
			mCurrentPage = data.getInt(KEY_RECEIVED_PAGE);
			mPagesCount = data.getInt(KEY_RECEIVED_PAGES_COUNT);
			if (mCurrentPage < mPagesCount) {
				loadData();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_LAYOUT_STATE, mLayoutManager.onSaveInstanceState());
		outState.putParcelableArrayList(KEY_STATE_ARRAY_LIST, (ArrayList<? extends Parcelable>) mAdapter.getProducts());
		outState.putInt(KEY_STATE_CURRENT_PAGE, mCurrentPage);
		outState.putInt(KEY_STATE_PAGES_COUNT, mPagesCount);
	}

	private void loadData() {
		startProgress();
		//TODO check connection
		Intent request = new Intent(this, GoodsDownloadService.class);
		request.putExtra(KEY_DATA_RECEIVER, mReceiver);
		request.putExtra(KEY_DOWNLOAD_URL, DATA_URL + (mCurrentPage + 1));
		startService(request);
	}

	private void startProgress() {
		mProgressBar.setVisibility(mAdapter != null && mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
	}

	private void stopProgress() {
		if (mProgressBar.getVisibility() != View.GONE) {
			mProgressBar.setVisibility(View.GONE);
		}
	}
}
