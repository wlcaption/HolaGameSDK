package com.holagames.xcds.tools;

import com.holagames.xcds.BuildConfig;
import com.holagames.xcds.IlongSDK;

import android.util.Log;

/**
 * 日志
 * @author niexiaoqiang
 */
public class LogUtils {
	public static final String TAG = "LONGYUAN";

	public static void debug(Object msg) {
		if(IlongSDK.getInstance().getDebugMode())
			Log.d(TAG, null == msg ? "" : msg.toString());
	}

	public static void info(Object msg) {
		if(IlongSDK.getInstance().getDebugMode())
			Log.i(TAG, null == msg ? "" : msg.toString());
	}

	public static void error(Object msg) {
		if(IlongSDK.getInstance().getDebugMode())
			Log.e(TAG, null == msg ? "" : msg.toString());
		if (BuildConfig.DEBUG && msg instanceof Throwable) {
			((Throwable) msg).printStackTrace();
		}
	}

	public static void warn(Object msg) {
		if(IlongSDK.getInstance().getDebugMode())
			Log.w(TAG, null == msg ? "" : msg.toString());
	}
}
