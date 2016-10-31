package com.example.goodslist;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.goodslist.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DataLoaderFragment extends Fragment {

	private static final String DATA_URL = "https://test4.lesara.de/restapi/v1/trendproducts/?app_token=this_is_an_app_token&" +
			"user_token=63a12a8116814a9574842515378c93c64846fc3d0858def78388be37e127cd17&store_id=1&page_override=";
	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int READ_STREAM_TIMEOUT = 8000;

	private DataReceivedListener mDataReceiverCallback;
	private DataLoaderTask mLoaderTask;
	private int mCurrentPage;
	private int mPagesCount;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mDataReceiverCallback = (DataReceivedListener) context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mLoaderTask = new DataLoaderTask();
		mLoaderTask.execute();
	}

	public void loadMoreData() {
		if (mCurrentPage < mPagesCount) {
			mLoaderTask = new DataLoaderTask();
			mLoaderTask.execute();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mDataReceiverCallback = null;
	}

	public interface DataReceivedListener {

		void onStartDataLoading();

		void onDataReceived(List<Product> products);
	}

	private class DataLoaderTask extends AsyncTask<Void, Void, List<Product>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mDataReceiverCallback != null) {
				mDataReceiverCallback.onStartDataLoading();
			}
		}

		@Override
		protected List<Product> doInBackground(Void... params) {
			try {
				return downloadUrl();
			} catch (Exception error) {
				return new ArrayList<>();
			}
		}

		@Override
		protected void onPostExecute(List<Product> products) {
			super.onPostExecute(products);
			if (mDataReceiverCallback != null) {
				mDataReceiverCallback.onDataReceived(products);
			}
		}

		private List<Product> downloadUrl() throws IOException, JSONException {
			InputStream is = null;
			try {
				URL url = new URL(DATA_URL + (mCurrentPage + 1));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(READ_STREAM_TIMEOUT);
				conn.setConnectTimeout(CONNECTION_TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				int response = conn.getResponseCode();
				if (response == HttpsURLConnection.HTTP_OK) {
					is = conn.getInputStream();
					return parseResponse(is);
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
			return new ArrayList<>();
		}

		private List<Product> parseResponse(InputStream stream) throws IOException, JSONException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String inputStr;
			while ((inputStr = reader.readLine()) != null) {
				sb.append(inputStr);
			}
			JSONObject object = new JSONObject(sb.toString());
			List<Product> products = new ArrayList<>();
			object = object.getJSONObject("trend_products");
			JSONArray array = object.getJSONArray("products");
			JSONObject productObject;
			for (int i = 0; i < array.length(); i++) {
				productObject = array.getJSONObject(i);
				products.add(new Product(productObject.optString("name"), productObject.optString("thumbnail_path"), productObject.optDouble("price")));
			}
			mPagesCount = object.optInt("number_pages");
			mCurrentPage = object.optInt("current_page");
			return products;
		}
	}
}
