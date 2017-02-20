package com.holagame.global;

public class Constant {
	/**是否是调试模式*/
	public static boolean isDebug = false;
	
	/**属于SDK 还是属于 数据*/
	
//	public static final String KEY_CHANNEL_DATA = "GAME";
//	public static final String KEY_CHANNEL_SDK = "SDK";
	
//	public static final boolean MODEL_DATA = true;
	
	/**获取服务器配置*/
	public static final String URL_CONFIG = "/dataserver/server/getServerConfig.do";
	
	/**添加接口*/
	public static String URL_ADD_BASE64 = "/dataserver/server/encryptAdd.do";
	
	/**服务器地址*/
	public static final String host = "http://dataserver.ilongyuan.com.cn";
	/**测试服地址*/
//	public static final String host = "http://test.dataserver.ilongyuan.com.cn";
	/**内网地址*/
//	public static final String host = "http://192.168.0.143:8080";
	
	/**上传的最大数据条数*/
	public static final int Queue_Max_Siz = 50;
	/**数据上传的时间间隔*/
	public final static int Submit_Time_Span = 150000;
	
	
	public static final boolean MODEL_DATA = true;
	
}
