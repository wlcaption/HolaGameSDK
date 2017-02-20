package com.holagame.tool;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.util.Log;

import com.holagame.global.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 网络访问工具类
 * 
 * @author 邹龙
 * 
 */
public class NetWork extends AsyncHttpResponseHandler{
	/** 上传数据 */
	private String value;
	/** 服务器请求地址 */
	private String url;
	/** 上传工具类 */
	private static NetWork netWork;
	
	
	
	/**上下文*/
	private static Context context;
	/**回调接口*/
	private AsyncHttpResponseHandler hanlerinterface;
	public  static final String TAG="NetWork";
	
	public NetWork() {
	}
	
	/**
	 * 获取网络实例化工具
	 * @param value 上传的值
	 * @param url 上传的地址
	 * @param hanlerinterface 回调方法，该类是android-async-http框架的类，详情可以参考该类
	 * @return 实例化的网络上传工具
	 */
	public static NetWork getInstance(String value,String url,AsyncHttpResponseHandler hanlerinterface){
	    netWork=new NetWork();
		netWork.setValue(value);
		netWork.setUrl(url);
		if(hanlerinterface != null){
			netWork.setHanlerinterface(hanlerinterface);
		}else{
			netWork.setHanlerinterface(netWork);
		}
		return netWork;
	}
	
	/**
	 * 开始上传数据
	 */
	public void start(){
		Logd.d(TAG, "上传数据是："+value+"\n"+"上传url："+ Constant.host + url);
		try {
				AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
				asyncHttpClient.setTimeout(30000);
				asyncHttpClient.post(context, Constant.host + url,new StringEntity(value, "UTF-8"), "application/x-www-form-urlencoded", hanlerinterface);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
				

		}
	 
	
	/**
	 * 设置上传数据
	 * @param value 上传数据
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 设置接口
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 设置回调
	 * @param hanlerinterface 回调接口继承AsyncHttpResponseHandler
	 */
	public void setHanlerinterface(AsyncHttpResponseHandler hanlerinterface) {
		this.hanlerinterface = hanlerinterface;
	}
	
	/**
	 * 设置上下文
	 * 
	 * @param context，传入整个应用的 applicationContext
	 */
	public static void setContext(Context context) {
		NetWork.context = context.getApplicationContext();
	}


	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		Log.e(TAG, "onFaild");
		Logd.d(TAG, "数据收集失败");
	}


	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		Log.e(TAG, "onSuccess");
		Logd.d(TAG, "返回值："+new String(arg2));
	}
 }