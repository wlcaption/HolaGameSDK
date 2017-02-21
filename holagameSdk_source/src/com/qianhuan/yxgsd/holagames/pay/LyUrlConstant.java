package com.qianhuan.yxgsd.holagames.pay;

import com.qianhuan.yxgsd.holagames.tools.http.Constant;

/**
 * @author ly
 */
public class LyUrlConstant {
	//支付正式服务器地址
	public static final String BASE_URL = Constant.httpHost + "/";
	/** 获取订单 */
	public static final String PAY_ORDER = "V201601/Order/game";
	/** 交易记录 */
	public static final String PAY_RECORD = "Oauth/User/orders";
	public static final String RETURN_NOTIFY = "Oauth/Pay/return_notify";
//	新的财付通接口
	public static final String CFT_CONFIRM = "V201601/Public/webpay";
    /**旧接口将不用了*/
	public static final String LONGYUAN_PAY = "V201601/Order/ilypay";
	//ALI异步通知
	public static final String ALI_ASYNC_NOTIFY = "V201601/Public/notify/channel/alipayquick";

	public static final String CHECK_PASSWORLD = "V201601/order/checkPayPassword";
}
