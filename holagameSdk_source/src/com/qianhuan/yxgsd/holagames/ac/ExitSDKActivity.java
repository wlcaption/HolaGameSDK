package com.qianhuan.yxgsd.holagames.ac;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.holagame.tool.Gamer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.modle.ExitBanner;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;

public class ExitSDKActivity extends BaseActivity implements OnClickListener {
	/**用于跳转页面后，是否显示 分享按钮的key*/
	private String isShowShare ="isShowShare" ;
	
//	退出的正中央的内容
	private ImageView exit_context;
//	是否是正式用户
	private boolean isUserNoraml = false;
//	正式用户和游客共享按钮，升级或者是随便逛逛
	private Button continue_btn;
	
	private String ActivityName = "com.longyuan.sdk.ac.ExitSDKActivity";
	private ExitBanner exit_banner = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_exit_sdk"));
		if(IlongSDK.getInstance().packInfoModel!=null){
			exit_banner = IlongSDK.getInstance().packInfoModel.getExit_banner();
		}
		initView();
	}

	@SuppressLint("NewApi")
	private void initView() {
		//图片加载配置
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnFail(ResUtil.getDrawableId(this, "ilong_exit_context_bg"))//设置加载失败时候显示的图片
				.build();
		findViewById(ResUtil.getId(this, "exit_close")).setOnClickListener(this);
		findViewById(ResUtil.getId(this, "ilong_exit_sdk_out_btn")).setOnClickListener(this);
		continue_btn = (Button) findViewById(ResUtil.getId(this, "ilong_exit_sdk_continue_btn"));
		continue_btn.setOnClickListener(this);
		exit_context = (ImageView) findViewById(ResUtil.getId(this, "ilong_exit_context"));
	    if(IlongSDK.mUserInfo==null||IlongSDK.TYPE_USER.equals(Constant.TYPE_USER_NORMAL)){
	    	isUserNoraml = true;
	    }
		if(isUserNoraml){
			if(exit_banner != null){
				//加载图片地址
				String url = exit_banner.getImage();
				ImageLoader.getInstance().displayImage(url, exit_context, options);
			}else{
				exit_context.setImageResource(ResUtil.getDrawableId(this, "ilong_exit_context_bg"));
			}
			exit_context.setOnClickListener(this);
			continue_btn.setText("随便逛逛");
		}else{
			exit_context.setImageResource(ResUtil.getDrawableId(this, IlongSDK.ISLONG?"ilong_exit_visitor_bg":"hr_exit_visitor_bg"));
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		 if (id == ResUtil.getId(this, "ilong_exit_sdk_out_btn")) {
			Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_exit_sdk_out_btn");
			IlongSDK.getInstance().callbackExit.onExit();
			Gamer.sdkCenter.ExitEvent(IlongSDK.AccountId);
			IlongSDK.mToken = "";
			IlongSDK.mUserInfo = null;
			IlongSDK.getInstance().hideFloatView();
		} else if (id == ResUtil.getId(this, "ilong_exit_context")){
			if(!isUserNoraml || exit_banner ==null) return;
			//跳转地址
			String url = exit_banner.getImage();
			if(url.equals("")) return;
			Intent intent = new Intent();
		    intent.setAction("android.intent.action.VIEW");
		    Uri content_url = Uri.parse(url);
		    intent.setData(content_url);
		    //打开浏览器
		    v.getContext().startActivity(intent);
		}else if(id == ResUtil.getId(this, "ilong_exit_sdk_continue_btn") && isUserNoraml){
			Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_exit_sdk_continue_btn");
			Intent intent = new Intent(ExitSDKActivity.this, ActivityWeb.class);
			intent.putExtra("url", Constant.getUserBBS(IlongSDK.mToken).toString());
			intent.putExtra("title", "论坛");
			intent.putExtra(isShowShare, false);
			startActivity(intent);
		}else if(id == ResUtil.getId(this, "ilong_exit_sdk_continue_btn") && !isUserNoraml){
			Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_exit_sdk_continue_btn");
			Toast.makeText(this, "游客用户", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ExitSDKActivity.this, ActivityUpdateAccount.class);
			startActivity(intent);
		}
		 Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".exit_close");
		finish();
	}	

	@Override
	public String getActivityName() {

		return ActivityName;
	}
	
	
	
	
	
	

}
