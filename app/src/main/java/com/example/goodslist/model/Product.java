package com.example.goodslist.model;

public class Product {

	private static final String BASE_IMAGE_URL = "https://daol3a7s7tps6.cloudfront.net/";

	private String mProductName;
	private String mProductImage;
	private double mProductPrice;

	public Product(String name, String image, double price) {
		this.mProductName = name;
		this.mProductImage = BASE_IMAGE_URL + image;
		this.mProductPrice = price;
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
}
