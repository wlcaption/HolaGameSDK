package com.qianhuan.yxgsd.holagames.pay.weixinpay;

import org.json.JSONException;
import org.json.JSONObject;

import com.holagame.tool.Logd;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class WXPay {
	
	public static final int NO_OR_LOW_WX    = 1; //网络错误
	public static final int ERROR_PAY_PARAM = 2; //参数错误
	public static final int ERROR_PAy       = 3; //支付错误
	
	private static WXPay mWxPay;
	private IWXAPI mIwxapi;
	private String mPay_info;
	private WXPayResultCallBack mCallBack;
	
	public interface WXPayResultCallBack{
		void onSuccess();
		void onError(int error_code);
		void onCancel();
	}
	
	public WXPay(Context context, String wx_appid){
		mIwxapi = WXAPIFactory.createWXAPI(context, null);
		mIwxapi.registerApp(wx_appid);
	}
	
	public static void init(Context context, String wx_appid){
		if(mWxPay == null){
			mWxPay = new WXPay(context, wx_appid);
		}
	}
	
	public static WXPay getInstance(){
		return mWxPay;
	}
	
	public IWXAPI getWXApi(){
		return mIwxapi;
	}
	
	public void doPay(String pay_info, WXPayResultCallBack callBack){
		this.mPay_info = pay_info;
		this.mCallBack = callBack;
		
		if(!(check())){
			if(mCallBack != null){
				mCallBack.onError(NO_OR_LOW_WX);
			}
			return;
		}
		
		JSONObject mJsonObject = null;
		try {
			Log.e("ppp", mPay_info);
			mJsonObject = new JSONObject(mPay_info);
		} catch (JSONException e) {
			e.printStackTrace();
			if(mCallBack != null){
				mCallBack.onError(ERROR_PAY_PARAM);
			}
			return;
		}
		
		if(TextUtils.isEmpty(mJsonObject.optString("appid")) || TextUtils.isEmpty(mJsonObject.optString("partnerid"))|| 
				TextUtils.isEmpty(mJsonObject.optString("prepayid")) || TextUtils.isEmpty(mJsonObject.optString("package")) ||
                TextUtils.isEmpty(mJsonObject.optString("noncestr")) || TextUtils.isEmpty(mJsonObject.optString("timestamp")) ||
                TextUtils.isEmpty(mJsonObject.optString("sign"))){
			Log.e("mjson","-----");
			if(mCallBack != null){
				mCallBack.onError(ERROR_PAY_PARAM);
			}
			return;
		}
		
		PayReq req = new PayReq();
		req.appId     = mJsonObject.optString("appid");
		req.partnerId = mJsonObject.optString("partnerid");
		req.prepayId  = mJsonObject.optString("prepayid");
		req.nonceStr  = mJsonObject.optString("noncestr");
		req.timeStamp = mJsonObject.optString("timestamp");
		req.sign      = mJsonObject.optString("sign");
		req.packageValue = mJsonObject.optString("package");
		Log.e("req", mJsonObject.optString("package"));
		mIwxapi.sendReq(req);
	}
	
	public void onResp(int error_code){
		if(mCallBack == null){
			return;
		}
		
		if(error_code == 0){
			mCallBack.onSuccess();
		}else if(error_code == 1){
			mCallBack.onError(ERROR_PAy);
		}else if(error_code == -2){
			mCallBack.onCancel();
		}
		
		mCallBack = null;
	}

	private boolean check() {
		
		return mIwxapi.isWXAppInstalled() && mIwxapi.getWXAppSupportAPI()>=Build.PAY_SUPPORTED_SDK_INT;
	}

}
