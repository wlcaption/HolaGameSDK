package com.holagame.tool;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.holagame.em.DataType;
import com.holagame.global.Constant;
import com.holagame.model.PostDateModel;

/**
 * 上传数据母体 模型(外围基础数据json 格式)
 * 
 */
public class DataEvent {
	/** 上传模型 */
	public static PostDateModel postDateModel;
	/** 单例 */
	public static DataEvent signalProduction;
	/** 关卡失败 */
	public static final String levelsFail = "levelsFail";
	
	/** 上传错误消息 */
	public static final String ReportError = "error";
	
	/**数据上传json中data 字段POSTMODEL_DATA="data" */
	public static final String POSTMODEL_DATA="data";
	
	/** 上下文 */
	private static Context context;
	
	public static String channelId="ly";

	public static final String META_CHANNEL = "ChannelID";
	public static final String META_APPID = "LONGYUAN_APPID";
	public static final int platform = 1;
	
	public static final String LY_APPID = "fSkZ4gVBMKhqRh4Bkg7jFDhIp6nQNXwj";
	public static String GAME_APPID = "";
	public DataEvent(){
	}
	/**
	 * 实例化上传PosyDateModel模型
	 * 这个方法，将PosyDateModel公有数据段全部赋值，而数据段data = null ;
	 * @return 一条上传数据，内部data 字段对应的 值为 null
	 */
	public static DataEvent init(Context context, String channel) {
		if (signalProduction == null) {
			signalProduction = new DataEvent();
			signalProduction.context = context.getApplicationContext();
			DeviceUtil.DevId = DeviceUtil.getIMEI(context);
			GAME_APPID = getMetaData(META_APPID);
			String appId = LY_APPID;
			
			String channelId = channel;
			String packageId = SDKMark.getMark(context);
			String accountId = DeviceUtil.DevId;
			String apkVersion= getAppVersion(context);
			String sdkVersion=DeviceUtil.SDK_VERSION;
			String ip = DeviceUtil.getPhoneIp();
			String deviceId = 	DeviceUtil.DevId;
			String manufacturer = DeviceUtil.getPhoneManufacturer();
			String brand = DeviceUtil.getPhoneBrand();
			String network = DeviceUtil.getPhoneNetWork(context);
			String resolution = DeviceUtil.getSIMResolution(context);
			
			postDateModel=new PostDateModel(appId, 
					platform, 
					channelId, 
					packageId, 
					apkVersion, 
					sdkVersion, 
					Gamer.getTime(),
					ip,
					deviceId,
					manufacturer,
					brand,network,
					resolution,null);
		}
		return signalProduction;
	}
	
	
	
	/**
	 * 得带要上传的 JSON格式的String
	 * @param sinalcomplex 是一个 ArrayList<String> 里面包含了数据采集的各种事件         
	 * @return 将postDataModel 数据模型 转成String 字符串
	 */
	public static String getSignal(ArrayList<String> sinalcomplex,DataType type) {
		if(type == DataType.LONGYUAN){
			postDateModel.setAppId(LY_APPID);
		}else if(type == DataType.GAME){
			postDateModel.setAppId(GAME_APPID);
		}
		//重新刷新大模型的时间
		postDateModel.setTs(Gamer.getTime());
		postDateModel.setData(sinalcomplex);
		return JSON.toJSONString(postDateModel);
	
     }
	
	/**
	 * 
	 * 将具体的事件对象，转成 json字符串格式，用于上传
	 */
	public static String getSignal(Object object) {
		postDateModel.setTs(Gamer.getTime());
		postDateModel.setData(signalProduction.getdata(object));
		return JSON.toJSONString(postDateModel);
	}
	
	/**
	 * 信号流字符串生成器
	 * 
	 * @param object 上传类，可以是一个单独的信号模型，比如登录loginmodel，传入这个参数将只上传一个，如果是arraylist<loginmodel>将一次上传 多条数据,实现的是单接口多数据上传
	 * @param head 向服务器请求的方法
	 * @return 信号流模型JSON字符串
	 */
	private ArrayList<String> getdata(Object object) {
		ArrayList<String> temp = new ArrayList<String>();
		if (object instanceof ArrayList) {
			ArrayList<String> arrayList = (ArrayList<String>) object;
			temp.addAll(arrayList);
		} else {
			temp.add(JSON.toJSONString(object));
		}
		return temp;
	}
		
	/**
	 * 获取版本信息
	 * 
	 * @return 应用的版本
	 */
	public static String getAppVersion(Context context) {
		try {
			String pkName = context.getPackageName();
			String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
			return versionName;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 获取application中的应用空间中的数据
	 * 
	 * @param type 定义的名称
	 * @return 返回定义在application中的值
	 */
	public static String getMetaData(String type) {
		String data = "";
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			data = info.metaData.getString(type);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return data;
	}
}
