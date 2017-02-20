package com.holagames.xcds.tools.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.holagame.util.DeviceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class HttpUtil extends AsyncHttpClient {
	public static final String TAG = "HttpUtil";
	private static HttpUtil intance;
	private static HttpUtil httpsInstance;

	public RequestHandle httpPost(Context context, String url, Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return intance.post(context, url, getParams(url, map), responseHandler);
	}

	public RequestHandle httpGet(Context context, String url, Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return intance.post(context, url, getParams(url, map), responseHandler);
	}

	public RequestHandle httpsPost(Context context, String url, Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return httpsInstance.post(context, url, getParams(url, map), responseHandler);
	}

	public RequestHandle httpsGet(Context context, String url, Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return httpsInstance.post(context, url, getParams(url, map), responseHandler);
	}
    
	@SuppressWarnings("deprecation")
	public RequestHandle httpsPostJSON(Context context,String url,Map<String, Object> map,ResponseHandlerInterface responseHandler){
		RequestHandle requestHandle = null;
		try {
			requestHandle=httpsInstance.post(context, url,new StringEntity(getValue(map), "UTF-8"),  "Content-Type", responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(TAG, "字符转换失败，可能是StringEntity过时");
		}
		return requestHandle;
	}

	private HttpUtil() {
		super();
	}

	private HttpUtil(boolean b, int port, int httpsport) {
		super(b, port, httpsport);
	}

	public static HttpUtil newIntance() {
		if (null == intance) {
			intance = new HttpUtil();
			intance.setTimeout(30000);
		}
		return intance;
	}

	public static HttpUtil newHttpsIntance(Context context) {
		if (null == httpsInstance) {
			httpsInstance = new HttpUtil(true, 80, 443);
			PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
			myCookieStore.clear();
			httpsInstance.setCookieStore(myCookieStore);
			httpsInstance.setTimeout(30000);
		}
		return httpsInstance;
	}

	public RequestParams getParams(String url, Map<String, Object> map) {
		Log.d(TAG, "getParams" + ", " + url + ", " + map.toString());
		StringBuilder logSb = new StringBuilder(url);
		RequestParams params = new RequestParams();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof File) {
				try {
					params.put(entry.getKey(), (File) value);
				} catch (Exception e) {
				}
				continue;
			} else {
				logSb.append("/" + entry.getKey() + "/" + value);
				params.put(entry.getKey(), value);
			}
		}
		com.holagames.xcds.tools.LogUtils.debug(logSb.toString());
		return params;
	}
	/**
	 * 将一个键值对转化成一个JSON对象
	 * @param map 请求键值对
	 * @return JSON对象
	 */
	private String getValue(Map<String, Object> map) {
		JSONObject jsonObject=new JSONObject();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
				jsonObject.put(entry.getKey(), value);
			}
		return DeviceUtil.encodeData(jsonObject.toJSONString());
	}
}
