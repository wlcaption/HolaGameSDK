package com.qianhuan.yxgsd.holagamesdk.tools.http;

import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public abstract class SdkJsonReqHandler extends JsonHttpResponseHandler {
	public static final String TAG = "ImJsonHttpResponseHandler";
	protected Object reqObject;

	public SdkJsonReqHandler(Object reqObject) {
		this.reqObject = reqObject;
	}

	@Override
	public void onFailure(int status, Header[] arg1, String arg2, Throwable arg3) {
		
		NetException spException = new NetException(arg3);
		if (arg3 instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (arg3 instanceof IOException) {
			spException = new NetException("网络异常");
		}
		ReqNo(reqObject, spException);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
		NetException spException = new NetException(throwable);
		if (throwable instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (throwable instanceof IOException) {
			spException = new NetException("网络异常");
		}
		ReqNo(reqObject, spException);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		NetException spException = new NetException(throwable);
		if (throwable instanceof JSONException) {
			spException = new NetException("数据异常");
		}
		if (throwable instanceof IOException) {
			spException = new NetException("网络异常");
		}
		ReqNo(reqObject, spException);
	}

	@Override
	public void onSuccess(int status, Header[] arg1, String text) {
		if (status != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			ReqYes(reqObject, text);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		String jsonStr = response.toString();
		if (statusCode != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			ReqYes(reqObject, jsonStr);
		}
	}

	@Override
	public void onSuccess(int status, Header[] headers, JSONObject response) {
		String jsonStr = response.toString();
		if (status != 200) {
			ReqNo(reqObject, new NetException(new Throwable("服务端返回非200错误")));
		} else {
			ReqYes(reqObject, jsonStr);
		}
	}

	public abstract void ReqYes(Object reqObject, String content);

	public abstract void ReqNo(Object reqObject, NetException slException);
}
