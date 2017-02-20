package com.holagame.constant;
/**
 * 
 * Activity 生命周期方法常量定义*/
public class ActivityLifeTypeConstant {
	/**页面创建*/
	public static final String TYPE_ACTIVITY_LIFE_ONCREATE = "创建";
	/**页面可以交互*/
	public static final String TYPE_ACTIVITY_LIFE_ONRESUME = "可交互";
	/**页面被部分遮挡*/
	public static final String TYPE_ACTIVITY_LIFE_ONPAUSE = "失去焦点";
	/**页面销毁*/
	public static final String TYPE_ACTIVITY_LIFE_ONDESTROY  = "销毁";
}
