package com.holagames.xcds.ac;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.modle.Notice;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.http.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityGameNoticePage extends BaseActivity{
	private ImageView context_bg_yellow;
	private ImageView context_img;
	private View close_rl;
	
	private RotateAnimation contextYellowAnimation;
	
	private TranslateAnimation closeRlAnimation;
	
	private static String ActivityName = "com.longyuan.sdk.ac.ActivityGameNoticePage";
	
	private Notice notice_configure;
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(ResUtil.getLayoutId(this, "ilong_game_notice_page"));
		initView();
		initData();
	}
	private void initView() {
		context_bg_yellow = (ImageView) findViewById(ResUtil.getId(this, "ilong_notice_page_context_bg_yellow"));
		context_img = (ImageView) findViewById(ResUtil.getId(this, "ilong_notice_page_iv"));
		close_rl = findViewById(ResUtil.getId(this, "ilong_notice_close_rl"));
	}

	private void initData() {
		//图片加载配置
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
						.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
						.showImageOnLoading(ResUtil.getDrawableId(ActivityGameNoticePage.this, "ilong_notice_default_bg"))
						.showImageOnFail(ResUtil.getDrawableId(ActivityGameNoticePage.this, "ilong_notice_default_bg"))
						.build();
		notice_configure = IlongSDK.getInstance().packInfoModel.getActive();
		contextYellowAnimation = new RotateAnimation(0, -8,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		contextYellowAnimation.setFillAfter(true);
		contextYellowAnimation.setDuration(1000);
		
		closeRlAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		closeRlAnimation.setFillAfter(true);
		closeRlAnimation.setDuration(1000);
		
		findViewById(ResUtil.getId(this, "ilong_notice_close_bt")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".game_notice_page_close_Btn");
				finish();
				
			}
		});
		
		ImageLoader.getInstance().displayImage(notice_configure.getImgUrl(), context_img, options);
		
		context_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = notice_configure.getUrl();
				if(url == null || url.equals("")) return;
				url = url+"?from=sdk"+"&access_token="+IlongSDK.getInstance().mToken+ Constant.AppendToWebUri();
				Log.e("Notice", url);
				Intent intent = new Intent(ActivityGameNoticePage.this, ActivityWeb.class);
				intent.putExtra("url", Uri.parse(url).toString());
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public String getActivityName() {
		
		return ActivityName;
	}
		@Override
	protected void onResume() {
		close_rl.startAnimation(closeRlAnimation);
		context_bg_yellow.startAnimation(contextYellowAnimation);
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		IlongSDK.getInstance().packInfoModel.setActive(null);
		super.onDestroy();
	}
}
