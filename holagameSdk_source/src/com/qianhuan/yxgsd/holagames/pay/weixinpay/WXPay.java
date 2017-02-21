package com.qianhuan.yxgsd.holagames.pay.weixinpay;

import android.content.Context;

import com.holagame.util.Logd;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPay {
	public static final String TAG = "WXPay";
	
	private IWXAPI api;
	private static WXPay mWxPay;
	private Context mContext;
	
	private WXPay(Context context){
		this.mContext = context;
		this.api = WXAPIFactory.createWXAPI(context.getApplicationContext(),null);
	}
	
	public static WXPay getInstance(Context context){
		if(mWxPay == null){
			mWxPay = new WXPay(context);
		}
		return mWxPay;
	}
	
	public void wxpay(String content){
		Logd.e(TAG, content);
		sendPayReq(content);
	}

	private void sendPayReq(String result) {
		WXModel model = WXModel.jsonData(result);
		PayReq request = new PayReq();
		request.appId = model.getAppId();
		request.partnerId = model.getPartnerId();
		request.prepayId = model.getPrepayId();
		request.packageValue = model.getPackageValue();
		request.nonceStr = model.getNonceStr();
		request.timeStamp = model.getTimeStamp();
		request.sign = model.getSign();
		Logd.e(TAG, "model:" + model.toString());
		boolean b = this.api.sendReq(request);
	}
}
