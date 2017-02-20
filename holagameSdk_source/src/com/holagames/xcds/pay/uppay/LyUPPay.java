package com.holagames.xcds.pay.uppay;

import android.app.Activity;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

/**
 * 银联支付封装类
 * @author ly
 */
public class LyUPPay {
	/**
	 * 银联测试环境
	 */
	public static final String UP_TEST_MODE = "01";
	/**
	 * 银联生产环境
	 */
	public static final String UP_PRODUCT_MODE = "00";
	/**
	 * 当前银联环境
	 */
	private String mMode = UP_PRODUCT_MODE;

	private Activity mContext;

	public LyUPPay(Activity mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param tn 商品交易流水号
	 * @param up_mode 银联环境
	 */
	public void pay(String tn) {
		//尝试启动支付控件
//		int result = UPPayAssistEx.startPay(mContext, null, null, tn, mMode);
		UPPayAssistEx.startPayByJAR(mContext, PayActivity.class, null, null, tn, mMode);
		//		int result = UPPayAssistEx.startPayFromBrowser(mContext, tn, mMode);
		// 安装Asset中提供的UPPayPlugin.apk
//		if (UPPayAssistEx.PLUGIN_NOT_FOUND == result) {
//			UPPayAssistEx.installUPPayPlugin(mContext);
//		}
	}

}
