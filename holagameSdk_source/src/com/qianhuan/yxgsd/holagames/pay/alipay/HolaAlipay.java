package com.qianhuan.yxgsd.holagames.pay.alipay;

import java.util.Map;

import com.alipay.sdk.app.PayTask;
import com.holagame.util.Logd;
import com.qianhuan.yxgsd.holagames.pay.LyPayActivity.LyPayResult;
import com.qianhuan.yxgsd.holagames.pay.alipay.utils.PayResult;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/***
 * 支付宝支付，对外公开的接口，传入参数是payinfo
 * @author 小魏 
 * @Email: wlcaption@gmail.com
 * 2017-2-21 下午2:58:01
 */
public class HolaAlipay {
	
	private static final int SDK_PAY_FLAG   = 1;
	private static final int SDK_CHECK_FLAG = 2;
	
	private Activity mContext;
	private LyPayResult lyPayResult;
	private HolaAlipay holaAlipay;
	
	
	public HolaAlipay(Activity mContext, LyPayResult lyPayResult){
		this.mContext = mContext;
		this.lyPayResult = lyPayResult;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);
				String resultStaus = payResult.getResultStatus();
				if(TextUtils.equals(resultStaus, "9000")){
					lyPayResult.lyPayYes(0);
				}else{
					if(TextUtils.equals(resultStaus, "8000")){
						ToastUtils.show(mContext, "支付结果确认中");
					}else if(TextUtils.equals(resultStaus, "6001")){
						lyPayResult.lyPayNo(0, "支付取消");
					}else{
						lyPayResult.lyPayNo(0, "支付失败");
					}
				}
				break;
				
			case SDK_CHECK_FLAG:{
				ToastUtils.show(mContext, "检查结果为:" + msg.obj);
				break;
			}

			default:
				break;
			}
		};
	};
	
	public void payV2(final String pay_info){
		
		Runnable payRunnable = new Runnable() {
			
			@Override
			public void run() {
				PayTask aliPayTask = new PayTask(mContext);
				Map<String, String> result = aliPayTask.payV2(pay_info, true);
				Logd.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
				
			}
		};
		
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	public void getSDKVersion(){
		PayTask payTask = new PayTask(mContext);
		String version = payTask.getVersion();
		ToastUtils.show(mContext, version);
	}

}
