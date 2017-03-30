package com.example.goodslist.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.ResultReceiver;

import com.example.goodslist.HttpHelper;
import com.example.goodslist.MainActivity;
import com.example.goodslist.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class GoodsDownloadService extends Service {

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;

	private ServiceHandler mServiceHandler;
	private IBinder mBinder;

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread(GoodsDownloadService.class.getName(), Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mServiceHandler = new ServiceHandler(thread.getLooper());
		mBinder = new DownloadBinder();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void downloadGoods(GoodsReceiver receiver, String url) {
		Message msg = mServiceHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putParcelable(MainActivity.KEY_DATA_RECEIVER, receiver);
		data.putString(MainActivity.KEY_DOWNLOAD_URL, url);
		msg.setData(data);
		mServiceHandler.sendMessage(msg);
	}

	public class DownloadBinder extends Binder {
		public GoodsDownloadService getService () {
			return GoodsDownloadService.this;
		}
	}

	private final class ServiceHandler extends Handler {

		private List<Product> mProducts;
		private int mPageReceived;
		private int mPagesCount;

		ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			if (!data.isEmpty()) {
				final ResultReceiver goodsReceiver = data.getParcelable(MainActivity.KEY_DATA_RECEIVER);
				String urlAddress = data.getString(MainActivity.KEY_DOWNLOAD_URL);
				Bundle result = new Bundle();
				int resultCode;
				try {
					Request request = new Request.Builder().url(urlAddress).build();
					Response response = HttpHelper.getInstance().getOkHttpClient().newCall(request).execute();
					parseResponse(response.body().string());
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
		}

		private void parseResponse(String jsonResponse) throws IOException, JSONException {
			JSONObject object = new JSONObject(jsonResponse);
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
}
