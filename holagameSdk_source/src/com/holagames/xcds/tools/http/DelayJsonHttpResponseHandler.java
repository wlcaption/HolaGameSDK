package com.holagames.xcds.tools.http;

import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.holagames.xcds.tools.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
/**
 * 网络请求回调
 * 在原来的基础添加延迟回调的功能
 */
public abstract class DelayJsonHttpResponseHandler extends JsonHttpResponseHandler {
	public static final String TAG = "SdkJsonReqHandler";
	protected Object reqObject;
	private long startTime = 0;
	private long delayTime = 0;

	public DelayJsonHttpResponseHandler(Object reqObject,long startTime,long delayTime) {
		this.reqObject = reqObject;
		this.startTime = startTime;
		this.delayTime = delayTime;
	}
	
	public boolean isInterrupted = false;

	@Override
	public void onFailure(int status, Header[] arg1, String arg2, Throwable arg3) {
		if(arg3 != null) arg3.printStackTrace();
		NetException spException = new NetException(arg3);
		LogUtils.debug(spException.getMessage());
		if (arg3 instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (arg3 instanceof IOException) {
			spException = new NetException("网络异常");
		}
		try {
			Log.e("SdkJsonReqHandler", "status: " + status + "\n header[] : " + arg1.toString() + 
					"\n arg2: " + arg2.toString() + "\nthrowable: " + arg3.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ReqNo(reqObject, spException);
	}
	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
		if(throwable != null) throwable.printStackTrace();
		NetException spException = new NetException(throwable);
		LogUtils.debug(spException.getMessage());
		if (throwable instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (throwable instanceof IOException) {
			spException = new NetException("网络异常");
		}
		try {
			Log.e("SdkJsonReqHandler", "status: " + statusCode + "\n header[] : " + headers.toString() + 
					"\n JSONArray: " + errorResponse.toString() + "\nthrowable: " + throwable.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ReqNo(reqObject, spException);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		if(throwable != null) throwable.printStackTrace();
		NetException spException = new NetException(throwable);
		LogUtils.debug(spException.getMessage());
		if (throwable instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (throwable instanceof IOException) {
			spException = new NetException("网络异常");
		}
		try {
			Log.e("SdkJsonReqHandler", "status: " + statusCode + "\n header[] : " + headers.toString() + 
					"\n JSONArray: " + errorResponse.toString() + "\nthrowable: " + throwable.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ReqNo(reqObject, spException);
	}

	@Override
	public void onSuccess(int status, Header[] arg1, String text) {
		LogUtils.debug(text);
		if (status != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			if(isInterrupted) {
				Log.e(TAG, "isInterrupted: " + isInterrupted);
				return;
			}
			setDelay(text);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		String jsonStr = response.toString();
		LogUtils.debug(jsonStr);
		if (statusCode != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			if(isInterrupted) {
				Log.e(TAG, "isInterrupted: " + isInterrupted);
				return;
			}
			setDelay(jsonStr);
		}
	}

	@Override
	public void onSuccess(int status, Header[] headers, JSONObject response) {
		String jsonStr = response.toString();
		LogUtils.debug(jsonStr);
		if (status != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			if(isInterrupted) {
				Log.e(TAG, "isInterrupted: " + isInterrupted);
				return;
			}
			setDelay(jsonStr);
		}
	}


	private void setDelay(final String jsonStr) {
		long netAccesTime = System.currentTimeMillis() - startTime;
		if(startTime != 0 && delayTime != 0 && netAccesTime<delayTime){
			new Handler().postDelayed(new Runnable() {
				public void run() {
					ReqYes(reqObject, jsonStr);
				}
			}, (delayTime-netAccesTime));
		}else{
			ReqYes(reqObject, jsonStr);
		}
	}
	
	public abstract void ReqYes(Object reqObject, String content);

	public abstract void ReqNo(Object reqObject, NetException slException);
}
