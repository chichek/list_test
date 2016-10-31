package com.example.goodslist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

	private static final String BASE_IMAGE_URL = "https://daol3a7s7tps6.cloudfront.net/";

	private String mProductName;
	private String mProductImage;
	private double mProductPrice;

	public Product(String name, String image, double price) {
		this.mProductName = name;
		this.mProductImage = BASE_IMAGE_URL + image;
		this.mProductPrice = price;
	}

	private Product(Parcel in) {
		String[] strings = new String[2];
		in.readStringArray(strings);
		mProductName = strings[0];
		mProductImage = strings[1];
		mProductPrice = in.readDouble();
	}

	public String getProductName() {
		return mProductName;
	}

	public String getProductImage() {
		return mProductImage;
	}

	public double getProductPrice() {
		return mProductPrice;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {mProductName, mProductImage});
		dest.writeDouble(mProductPrice);
	}

	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

}
