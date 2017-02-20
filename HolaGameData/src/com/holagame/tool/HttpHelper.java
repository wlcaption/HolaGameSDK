package com.holagame.tool;



import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


/**网络工具类*/
@SuppressLint("NewApi") 
public class HttpHelper {
	public static final String Tag = "HttpHelper";
	
	public static interface MHttp{
		public abstract void onSuccess(String result);
		public abstract void onFailed();
	}
	
	
	public static void doGet(final String url, final MHttp mHttp){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					AndroidHttpClient c = AndroidHttpClient.newInstance("");
					HttpResponse rep = c.execute(new HttpGet(url));
					if(rep.getStatusLine().getStatusCode() == 200){
						String result = EntityUtils.toString(rep.getEntity(), "utf-8");
						mHttp.onSuccess(result);
					}else{
						mHttp.onFailed();
					}
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
					mHttp.onFailed();
				}
			}
		}).start();
	}
	
	public static void doPost(final String url, final MHttp mHttp, final List<NameValuePair> params){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					  HttpPost httpRequest = new HttpPost(url);
//					  List<NameValuePair> params = new ArrayList<NameValuePair>();
//					  NameValuePair pair1 = new BasicNameValuePair("par", "HttpClient_android_Post"));
//					  params.add(pair1);
					   // 取得默认的HttpClient
					   HttpClient httpclient = new DefaultHttpClient();
					   // 取得HttpResponse
					   HttpResponse httpResponse = httpclient.execute(httpRequest);
					   // HttpStatus.SC_OK表示连接成功
					    if (httpResponse.getStatusLine().getStatusCode() == 200) {			    
					     // 取得返回的字符串
					    	String strResult = EntityUtils.toString(httpResponse.getEntity());
					    	mHttp.onSuccess(strResult);
					    }else{
					    	mHttp.onFailed();
					    }
					    
				} catch (Exception e) {
					e.printStackTrace();
					mHttp.onFailed();
				}
			}
		}).start();
	}
	
	
	public static void doPost(Context context, String url, String data, MHttpHandler mHandler){
		try {
			AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
			asyncHttpClient.setTimeout(30000);
			asyncHttpClient.post(context, url,new StringEntity(data, "UTF-8"), "application/json", mHandler);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static abstract class MHttpHandler extends AsyncHttpResponseHandler{
		
		public abstract void onSuccess(String msg);
		public abstract void onFailed();
		
		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				if(arg2 == null) {
					arg2 = new byte[0];
				}
				String data = new String(arg2);
				Log.d(Tag, data);
				onSuccess(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			try {
				String data = "数据收集上传失败：" + new String(arg2) + "/n" + arg3.toString();
				if(arg3!=null && arg2!=null){
					Logd.d(Tag, data);
				}
				onFailed();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
	
	}
	
	
	
}
