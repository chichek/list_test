package com.example.goodslist;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.goodslist.adapter.ProductsAdapter;
import com.example.goodslist.model.Product;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoaderFragment.DataReceivedListener {

	public static final String TAG_DATA_LOADER_FRAGMENT = "data_loader_fragment_tag";
	private ProductsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		mAdapter = new ProductsAdapter(this);
		loadData();
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.act_main_recyclerview);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		recyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onDataReceived(List<Product> products) {
		if (mAdapter != null) {
			mAdapter.addProducts(products);
		}
	}

	private void loadData() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		DataLoaderFragment fragment = (DataLoaderFragment) fragmentManager.findFragmentByTag(TAG_DATA_LOADER_FRAGMENT);
		if (fragment == null) {
			fragment = new DataLoaderFragment();
			fragmentManager.beginTransaction().add(fragment, TAG_DATA_LOADER_FRAGMENT).commit();
		}
	}
}
