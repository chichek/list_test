package com.example.goodslist;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.goodslist.adapter.ProductsAdapter;
import com.example.goodslist.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoaderFragment.DataReceivedListener {

	private static final String TAG_DATA_LOADER_FRAGMENT = "data_loader_fragment_tag";
	private static final String KEY_LAYOUT_STATE = "key_layout_state";
	private static final String KEY_DATA_ARRAY_LIST = "key_data_array_list";
	private ProductsAdapter mAdapter;
	private ProgressBar mProgressBar;
	private GridLayoutManager mLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		mProgressBar = (ProgressBar) findViewById(R.id.act_main_progress);
		mAdapter = new ProductsAdapter(this);
		loadData(false);
		mLayoutManager = new GridLayoutManager(this, 2);
		if (savedInstanceState != null) {
			mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_STATE));
			mAdapter.addProducts(savedInstanceState.<Product>getParcelableArrayList(KEY_DATA_ARRAY_LIST));
		}
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.act_main_recyclerview);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onStartDataLoading() {
		if (mAdapter != null && mAdapter.getItemCount() == 0) {
			startProgress();
		}
	}

	@Override
	public void onDataReceived(List<Product> products) {
		stopProgress();
		if (mAdapter != null) {
			mAdapter.addProducts(products);
			loadData(true);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_LAYOUT_STATE, mLayoutManager.onSaveInstanceState());
		outState.putParcelableArrayList(KEY_DATA_ARRAY_LIST, (ArrayList<? extends Parcelable>) mAdapter.getProducts());
	}

	private void loadData(boolean hasDataAlready) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		DataLoaderFragment fragment = (DataLoaderFragment) fragmentManager.findFragmentByTag(TAG_DATA_LOADER_FRAGMENT);
		if (fragment == null) {
			fragment = new DataLoaderFragment();
			fragmentManager.beginTransaction().add(fragment, TAG_DATA_LOADER_FRAGMENT).commit();
		}
		if (hasDataAlready) {
			fragment.loadMoreData();
		}
	}

	private void startProgress() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void stopProgress() {
		if (mProgressBar.getVisibility() != View.GONE) {
			mProgressBar.setVisibility(View.GONE);
		}
	}
}
