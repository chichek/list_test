package com.example.goodslist.data;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.example.goodslist.MainActivity;
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

public class GoodsDownloadService extends IntentService {

	public static final int RESULT_RUNNING = 0;
	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;

	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int READ_STREAM_TIMEOUT = 8000;

	private List<Product> mProducts;
	private int mPageReceived;
	private int mPagesCount;

	public GoodsDownloadService() {
		super(GoodsDownloadService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final ResultReceiver goodsReceiver = intent.getParcelableExtra(MainActivity.KEY_DATA_RECEIVER);
		String urlAddress = intent.getStringExtra(MainActivity.KEY_DOWNLOAD_URL);
		Bundle result = new Bundle();
		int resultCode = RESULT_RUNNING;
		try {
			downloadUrl(urlAddress);
			result.putParcelableArrayList(MainActivity.KEY_RECEIVED_ITEMS, (ArrayList<Product>) mProducts);
			result.putInt(MainActivity.KEY_RECEIVED_PAGE, mPageReceived);
			result.putInt(MainActivity.KEY_RECEIVED_PAGES_COUNT, mPagesCount);
			resultCode = RESULT_OK;
		} catch (Exception e) {
			result.putString(MainActivity.KEY_RECEIVED_ITEMS, e.getMessage());
			resultCode = RESULT_ERROR;
		}
		goodsReceiver.send(resultCode, result);
	}

	private void downloadUrl(String urlAddress) throws IOException, JSONException {
		InputStream is = null;
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(READ_STREAM_TIMEOUT);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			int response = conn.getResponseCode();
			if (response == HttpsURLConnection.HTTP_OK) {
				is = conn.getInputStream();
				parseResponse(is);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private void parseResponse(InputStream stream) throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();
		String inputStr;
		while ((inputStr = reader.readLine()) != null) {
			sb.append(inputStr);
		}
		JSONObject object = new JSONObject(sb.toString());
		mProducts = new ArrayList<>();
		object = object.getJSONObject("trend_products");
		JSONArray array = object.getJSONArray("products");
		JSONObject productObject;
		for (int i = 0; i < array.length(); i++) {
			productObject = array.getJSONObject(i);
			mProducts.add(new Product(productObject.optString("name"), productObject.optString("thumbnail_path"), productObject.optDouble("price")));
		}
		mPagesCount = object.optInt("number_pages");
		mPageReceived = object.optInt("current_page");
	}
}
