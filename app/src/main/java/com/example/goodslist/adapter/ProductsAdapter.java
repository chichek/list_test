package com.example.goodslist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goodslist.HttpHelper;
import com.example.goodslist.MainActivity;
import com.example.goodslist.R;
import com.example.goodslist.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{

	private List<Product> mProducts;
	private Context mContext;

	public ProductsAdapter(Context context) {
		mContext = context;
		mProducts = new ArrayList<>();
	}

	public void addProducts(List<Product> moreProducts) {
		int initialSize = mProducts.size();
		mProducts.addAll(moreProducts);
		notifyItemRangeInserted(initialSize, moreProducts.size());
	}

	public List<Product> getProducts() {
		return mProducts;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.product_item_layout, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Product product = mProducts.get(position);
		holder.mProductNameTextView.setText(product.getProductName());
		holder.mProductPriceTextView.setText(String.format(Locale.ENGLISH, "%.2f", product.getProductPrice()));
		HttpHelper.getInstance().getPicassoDownloader(mContext).load(product.getProductImage())
				.placeholder(android.R.drawable.gallery_thumb)
				.into(holder.mProductImageView);
	}

	@Override
	public int getItemCount() {
		return mProducts.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ImageView mProductImageView;
		public TextView mProductNameTextView;
		public TextView mProductPriceTextView;
		public ViewHolder(View v) {
			super(v);
			mProductImageView = (ImageView) v.findViewById(R.id.product_item_layout_image);
			mProductNameTextView = (TextView) v.findViewById(R.id.product_item_layout_name);
			mProductPriceTextView = (TextView) v.findViewById(R.id.product_item_layout_price);
		}
	}

}
