package com.qianhuan.yxgsd.holagamesdk.tools.http;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * 网络请求工具类
 * 
 * @author niexiaoqiang
 */
public class HttpUtil extends AsyncHttpClient {
	public static final String TAG = "HttpUtil";
	private static HttpUtil intance;
	private static HttpUtil httpsInstance;

	public RequestHandle httpPost(Context context, String url,
			Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return intance.post(context, url, getParams(url, map), responseHandler);
	}

	public RequestHandle httpGet(Context context, String url,
			Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return intance.post(context, url, getParams(url, map), responseHandler);
	}

	public RequestHandle httpsPost(Context context, String url,
			Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return httpsInstance.post(context, url, getParams(url, map),
				responseHandler);
	}

	public RequestHandle httpsGet(Context context, String url,
			Map<String, Object> map, ResponseHandlerInterface responseHandler) {
		return httpsInstance.post(context, url, getParams(url, map),
				responseHandler);
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
			PersistentCookieStore myCookieStore = new PersistentCookieStore(
					context);
			myCookieStore.clear();
			httpsInstance.setCookieStore(myCookieStore);
			httpsInstance.setTimeout(30000);
		}
		return httpsInstance;
	}

	public RequestParams getParams(String url, Map<String, Object> map) {
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
		return params;
	}
}
