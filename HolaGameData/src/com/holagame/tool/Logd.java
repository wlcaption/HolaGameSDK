package com.holagame.tool;

import com.holagame.global.Constant;

import android.util.Log;

/**
 * log打印
 * @author 邹龙
 *
 */
public class Logd {
	
	/**需要处理的错误，必须得到纠正*/
	public static final String TAG_ERROR = "DATA_ERROR";
	
	public static void e(String tag, String message){
		try {
			if(Constant.isDebug)
				Log.e(tag, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void d(String tag, String message){
		try {
			if(Constant.isDebug)
				Log.d(tag, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
