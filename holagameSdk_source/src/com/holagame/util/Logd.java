package com.holagame.util;

import com.holagames.xcds.IlongSDK;

import android.util.Log;

public class Logd {
//	public static final String TAG = "ilongsdk";
	
	public static boolean isNull(String tag, String msg){
		if(tag == null){
			Log.e(tag, "tag is null");
			return true;
		}
		if(msg == null){
			Log.e(tag, "msg is null");
			return true;
		}
		return false;
	}
	
	public static void i(String tag, String msg){
		if(isNull(tag, msg)){
			return;
		}
		if(IlongSDK.getInstance().getDebugMode())
			Log.i(tag, msg);
	}
	
	public static void d(String tag, String msg){
		if(isNull(tag, msg)){
			return;
		}
		if(IlongSDK.getInstance().getDebugMode())
			Log.d(tag, msg);
	}
	
	public static void e(String tag, String msg){
		if(isNull(tag, msg)){
			return;
		}
		if(IlongSDK.getInstance().getDebugMode())
			Log.e(tag, msg);
	}
}
