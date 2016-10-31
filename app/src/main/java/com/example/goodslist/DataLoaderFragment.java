package com.example.goodslist;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.JsonToken;

import com.example.goodslist.model.Product;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DataLoaderFragment extends Fragment {

	private static final String DATA_URL = "https://test4.lesara.de/restapi/v1/trendproducts/?app_token=this_is_an_app_token&" +
			"user_token=63a12a8116814a9574842515378c93c64846fc3d0858def78388be37e127cd17&store_id=1";
	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int READ_STREAM_TIMEOUT = 8000;
	private static final String DATA_URL_PAGE = "page";

	private DataReceivedListener mDataReceiverCallback;
	private DataLoaderTask mLoaderTask;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mDataReceiverCallback = (DataReceivedListener)context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mLoaderTask = new DataLoaderTask();
		mLoaderTask.execute();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mDataReceiverCallback = null;
	}

	public interface DataReceivedListener {

		void onDataReceived(List<Product> products);
	}

	private class DataLoaderTask extends AsyncTask<Void, Void, List<Product>> {

		@Override
		protected List<Product> doInBackground(Void... params) {
			try {
				return downloadUrl();
			} catch (IOException error) {
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

		private List<Product> downloadUrl() throws IOException {
			InputStream is = null;
			try {
				URL url = new URL(DATA_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(READ_STREAM_TIMEOUT);
				conn.setConnectTimeout(CONNECTION_TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				int response = conn.getResponseCode();
				if (response == HttpsURLConnection.HTTP_OK) {
					is = new BufferedInputStream(conn.getInputStream());
					return parseResponse(is);
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
			return new ArrayList<>();
		}

		private List<Product> parseResponse(InputStream stream) throws IOException {
			JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
			try {
				List<Product> messages = new ArrayList<>();
				if (reader.nextName().equals("trend_products")) {
					reader.beginObject();
					if (reader.nextName().equals("products") && reader.peek() != JsonToken.NULL) {
						reader.beginArray();
						while (reader.hasNext()) {
							messages.add(readProduct(reader));
						}
						reader.endArray();
					}
				}
				return messages;
			} finally {
				reader.close();
			}
		}

		private Product readProduct(JsonReader reader) throws IOException {
			String title = null;
			double price = 0;
			String image = null;
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("name")) {
					title = reader.nextString();
				} else if (name.equals("price")) {
					price = reader.nextDouble();
				} else if (name.equals("thumbnail_path")) {
					image = reader.nextString();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			return new Product(title, image, price);
		}

	}
}
