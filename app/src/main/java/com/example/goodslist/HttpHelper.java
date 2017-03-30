package com.example.goodslist;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

public class HttpHelper {

	private static HttpHelper mHelper;
	private OkHttpClient mOkHttpClient;
	private Picasso mPicassoDownloader;

	private HttpHelper() {
		mOkHttpClient = new OkHttpClient();
	}

	public static synchronized HttpHelper getInstance() {
		if (mHelper == null) {
			mHelper = new HttpHelper();
		}
		return mHelper;
	}

	public OkHttpClient getOkHttpClient() {
		return mOkHttpClient;
	}

	public Picasso getPicassoDownloader(Context context) {
		if (mPicassoDownloader == null) {
			mPicassoDownloader = new Picasso.Builder(context).downloader(new OkHttp3Downloader(mOkHttpClient)).build();
		}
		return mPicassoDownloader;
	}
}
