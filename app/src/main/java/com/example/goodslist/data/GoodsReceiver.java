package com.example.goodslist.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class GoodsReceiver extends ResultReceiver {

	//TODO rewrite using handler
	private DataReceivedListener mDataReceiverCallback;

	public GoodsReceiver(Handler handler, DataReceivedListener callback) {
		super(handler);
		mDataReceiverCallback = callback;
	}

	public interface DataReceivedListener {

		void onDataReceived(Bundle data);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);
		switch (resultCode) {
			case GoodsDownloadService.RESULT_OK:
				if (mDataReceiverCallback != null) {
					mDataReceiverCallback.onDataReceived(resultData);
				}
				break;
		}
	}
}
