package com.qianhuan.yxgsd.holagames.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.qianhuan.yxgsd.holagames.pay.LyPayActivity.LyPayResult;
import com.qianhuan.yxgsd.holagames.pay.alipay.utils.PayResult;

public class LyAlipay {
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private LyPayResult lyPayResult;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						//					Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
						lyPayResult.lyPayYes(0);
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
							lyPayResult.lyPayNo(0, "等待支付结果确认");
						} else if(TextUtils.equals(resultStatus, "6001")){
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							//						Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
							lyPayResult.lyPayNo(0, "支付取消");
						}else{
							lyPayResult.lyPayNo(0, "支付失败");
						}
					}
					break;
				}
				case SDK_CHECK_FLAG: {
					Toast.makeText(mContext, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
			}
		};
	};

	private Activity mContext;

	public LyAlipay(Activity mContext, LyPayResult lyPayResult) {
		this.mContext = mContext;
		this.lyPayResult = lyPayResult;
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 */
	public void pay(final String pay_info) {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(pay_info,true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 */
//	public void check(View v) {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(mContext);
//				// 调用查询接口，获取查询结果
//				//boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//
//	}

	/**
	 * get the sdk version. 获取SDK版本号
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(mContext);
		String version = payTask.getVersion();
		Toast.makeText(mContext, version, Toast.LENGTH_SHORT).show();
	}

}
