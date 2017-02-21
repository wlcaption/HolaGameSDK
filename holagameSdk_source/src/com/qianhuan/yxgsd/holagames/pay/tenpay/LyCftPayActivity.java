package com.qianhuan.yxgsd.holagames.pay.tenpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.holagame.constant.ActivityLifeTypeConstant;
import com.holagame.tool.Gamer;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.pay.LyUrlConstant;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;

public class LyCftPayActivity extends BaseFragmentActivity {

	private WebView webView;
	// 支付金额
	private String amount;
	// 订单号
	private String out_trade_no;
	// 方法名称
	private String pay_method = "ten";
	private String payUrl;
	private String notify_uri;
	private View backBtn;
	private AlertDialog exitDialog;
	private TextView tipsTextView;
	
	private String ActivityName = "com.longyuan.sdk.pay.LyCftPayActivity";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(ResUtil.getLayoutId(this, "ly_cft_layout"));
		getIntentData();
		initView();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId,ActivityName, 
				ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONCREATE);
	}

	private void getIntentData() {
		Intent i = getIntent();
		amount = i.getStringExtra("amount");
		out_trade_no = i.getStringExtra("out_trade_no");
		notify_uri = i.getStringExtra("notify_uri");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		backBtn = findViewById(ResUtil.getId(this, "ly_pay_head_back_btn"));
		tipsTextView = (TextView) findViewById(ResUtil.getId(this, "ly_cft_tips"));
		webView = (WebView) findViewById(ResUtil.getId(this, "ly_cft_webview"));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				
				if (url.contains(notify_uri)) {
					ToastUtils.show(LyCftPayActivity.this, "支付成功");
					if (exitDialog != null && exitDialog.isShowing()) {
						exitDialog.dismiss();
					}
					setResult(RESULT_OK);
					LyCftPayActivity.this.finish();
				}
				//如果是测试环境，检测正式地址
				if(! com.qianhuan.yxgsd.holagames.tools.http.Constant.isRelease){
					String urlNotify = "http://account.ilongyuan.cn/Oauth/Pay/return_notify";
					if(url.contains(urlNotify)){
						ToastUtils.show(LyCftPayActivity.this, "支付成功");
						if (exitDialog != null && exitDialog.isShowing()) {
							exitDialog.dismiss();
						}
						setResult(RESULT_OK);
						LyCftPayActivity.this.finish();
					}
				}
				
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				tipsTextView.setVisibility(View.GONE);
			}
		});
		webView.requestFocus();
		payUrl = LyUrlConstant.BASE_URL + LyUrlConstant.CFT_CONFIRM +"?out_trade_no=" + out_trade_no + "&channel=" + pay_method;
		webView.loadUrl(payUrl);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".ly_pay_head_back_btn");
				showExitDialog();
			}
		});
	}

	private void showExitDialog() {
		if (exitDialog == null) {
			Builder builder = new Builder(this);
			builder.setTitle("提示").setMessage("操作尚未完成，确定退出财付通支付？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					exitDialog.dismiss();
					setResult(RESULT_CANCELED);
					LyCftPayActivity.this.finish();
				}
			});
			exitDialog = builder.create();
		}
		exitDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId,ActivityName, 
				ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONPAUSE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId,ActivityName,  
				ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONRESUME);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId,ActivityName,  
				ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONDESTROY);
	}
	
}
