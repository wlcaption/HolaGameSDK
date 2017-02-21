package com.qianhuan.yxgsd.holagames.ac;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.i.IToken2UserInfo;
import com.qianhuan.yxgsd.holagames.modle.UserInfo;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;

public class ActivityUser extends BaseActivity {
	private TextView longyuan_id;
	private TextView level_text;
	private TextView longyuanbi_text;
	
	private Dialog dialogBind;
	/**应用程序包名 信息*/

//	private String isShowShare ="isShowShare" ;
	/**用户mUserInfo的id*/
	private String id = "";
	
	private String ActivityName = "com.longyuan.sdk.ac.ActivityUser";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_user_info"));
		initView();
		if (null != IlongSDK.mToken && !"".equals(IlongSDK.mToken)) {
			IlongSDK.getInstance().updateUserInfo(this, IlongSDK.mToken, new IToken2UserInfo() {

				@Override
				public void onSuccess(UserInfo userInfo) {
					setUserInfo(userInfo);
				}
				@Override
				public void onFailed(){
					
				}
			});
		}
	}
	


	private void setUserInfo(UserInfo userInfo) {
		    String temp;
		    if(IlongSDK.ISLONG){
		    	temp = getResources().getString(ResUtil.getStringId(this, "ilong_userinfo_id"));
		    }else{
		    	temp = getResources().getString(ResUtil.getStringId(this, "hr_userinfo_id"));
		    }
			longyuan_id.setText(temp + userInfo.getUid());
			level_text.setText("LV." + userInfo.getLevel());
			longyuanbi_text.setText("￥" + userInfo.getMoney());
			id = userInfo.getId();
	}

	private void initView() {
		longyuan_id = (TextView) findViewById(ResUtil.getId(this, "longyuan_id"));
		level_text = (TextView) findViewById(ResUtil.getId(this, "level_text"));
		longyuanbi_text = (TextView) findViewById(ResUtil.getId(this, "longyuanbi_text"));
		findViewById(ResUtil.getId(this, "close_button")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".close_button");
				onBackPressed();
			}
		});
		//跳转个人中心
		findViewById(ResUtil.getId(this, "user_center_layout")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".user_center_layout");
				v.setEnabled(false);
				startBind(new BindListener() {
					
					@Override
					public void startNormal() {					
						Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//						intent.putExtra(isShowShare, true);
						String url = Constant.USER_CENTER + "?access_token=" + IlongSDK.mToken + "&from=sdk"
								+Constant.AppendToWebUri()+"&sdk_version="+DeviceUtil.SDK_VERSION;
						intent.putExtra("url", Uri.parse(url).toString());
						intent.putExtra("title", "个人中心");
						intent.putExtra("id", id);
						startActivity(intent);
					}
				});
				
				v.setEnabled(true);
				finish();
			}
		});
		//跳转礼包
		findViewById(ResUtil.getId(this, "gift_layout")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".gift_layout" );
				v.setEnabled(false);
				startBind(new BindListener() {
					
					@Override
					public void startNormal() {
						Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//						intent.putExtra(isShowShare, false);
						intent.putExtra("url", Constant.getGiftUri(IlongSDK.mToken).toString());
						intent.putExtra("title", "礼包");
						startActivity(intent);
					}
				});
				v.setEnabled(true);
				finish();
			}
		});
		//帮助
		findViewById(ResUtil.getId(this, "help_layout")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".help_layout");
				v.setEnabled(false);
				if (null == IlongSDK.mToken || IlongSDK.mToken.isEmpty()) {
					ToastUtils.show(ActivityUser.this, "请重新登陆");
					return;
				}
				Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//				intent.putExtra(isShowShare, false);
				intent.putExtra("url", Constant.getHelpUri(IlongSDK.mToken).toString());
				intent.putExtra("title", "帮助");
				startActivity(intent);
				
				v.setEnabled(true);
				finish();
			}
		});
		//签到sign_layout
		findViewById(ResUtil.getId(this, "sign_layout")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".sign_layout");
				v.setEnabled(false);
				startBind(new BindListener() {
					
					@Override
					public void startNormal() {
						Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//						intent.putExtra(isShowShare, false);
						intent.putExtra("url", Constant.getUSERNEWS(IlongSDK.mToken).toString());
						intent.putExtra("title", "消息");
						startActivity(intent);
					}
				});
				v.setEnabled(true);
				finish();
			}
		});
		//活动active_layout
		findViewById(ResUtil.getId(this, "active_layout")).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,IlongSDK.AccountId+".active_layout");
				v.setEnabled(false);
				startBind(new BindListener() {
					
					@Override
					public void startNormal() {
						Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//						intent.putExtra(isShowShare, false);
						intent.putExtra("url", Constant.getActiveUri().toString());
						intent.putExtra("title", "活动");
						startActivity(intent);
					}
				});
				v.setEnabled(true);
				finish();
				
			}
		});
		
		//论坛
		findViewById(ResUtil.getId(this, "forum_layout")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".forum_layout");
				v.setEnabled(false);
				startBind(new BindListener() {
					
					@Override
					public void startNormal() {
						Intent intent = new Intent(ActivityUser.this, ActivityWeb.class);
//						intent.putExtra(isShowShare, false);
						intent.putExtra("url", Constant.getUserBBS(IlongSDK.mToken).toString());
						intent.putExtra("title", "论坛");
						startActivity(intent);
						
					}
				});
				v.setEnabled(true);
				finish();
			}
		});
		
		//切换账号
		findViewById(ResUtil.getId(this, "ilong_text_switch_account")).setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_text_switch_account");
				v.setEnabled(false);
				IlongSDK.getInstance().setBackEable(true);
				Intent intent = new Intent(ActivityUser.this, SdkLoginActivity.class);
//				intent.putExtra(isShowShare, false);
				intent.putExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, true);
				startActivity(intent);
				v.setEnabled(true);
				finish();
			}
		});	
		
		//升级账号
		findViewById(ResUtil.getId(this, "ilong_text_update_account")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".IlongSDK.AccountId");
				v.setEnabled(false);
				Intent intent = new Intent(ActivityUser.this, ActivityUpdateAccount.class);
				startActivity(intent);
				v.setEnabled(true);
				finish();
			}
		});		
		
		if(IlongSDK.TYPE_USER.equals(Constant.TYPE_USER_NORMAL)){
			findViewById(ResUtil.getId(this, "ilong_text_switch_account")).setVisibility(View.VISIBLE);
			findViewById(ResUtil.getId(this, "ilong_text_update_account")).setVisibility(View.GONE);
		}else{
			findViewById(ResUtil.getId(this, "ilong_text_switch_account")).setVisibility(View.GONE);
			findViewById(ResUtil.getId(this, "ilong_text_update_account")).setVisibility(View.VISIBLE);
		}
	}
	
	
	/**显示绑定手机界面*/
	public void showBindPhone(){
		Activity activity = IlongSDK.getActivity();
		dialogBind = new Dialog(activity, ResUtil.getStyleId(activity,"ilongyuanAppUpdataCanCancle"));
		View view = activity.getLayoutInflater().inflate(
				ResUtil.getLayoutId(activity,"ilong_dialog_bindphone"), null);
		View CancleBtn = view.findViewById(ResUtil.getId(activity, "ilong_bind_cancel"));
		View OKUpdataBtn = view.findViewById(ResUtil.getId(activity, "ilong_bind_go"));
		View cancel = view.findViewById(ResUtil.getId(activity, "ilong_bind_close"));
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dialogBind != null && dialogBind.isShowing()){
					dialogBind.cancel();
					IlongSDK.getInstance().callbackPay.onSuccess();
		
				}
			}
		});
		CancleBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if(dialogBind != null && dialogBind.isShowing()){
						dialogBind.cancel();
						IlongSDK.getInstance().callbackPay.onSuccess();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		OKUpdataBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(dialogBind != null && dialogBind.isShowing()){
					dialogBind.cancel();
				}
				Intent it = new Intent(IlongSDK.getActivity(), ActivityBindPhone.class);
				IlongSDK.getActivity().startActivity(it);
			}
		});

		dialogBind.setCancelable(false);
		dialogBind.setCanceledOnTouchOutside(false);
		dialogBind.setContentView(view);
		dialogBind.show();
	}
	
	static interface BindListener{
		public void startNormal();
	}
	
	public void startBind(BindListener mListener){
		//如果是游客，需要绑定手机
		
		if(IlongSDK.TYPE_USER.equals(Constant.TYPE_USER_NORMAL)){
			mListener.startNormal();
		}else{
			Intent intent = new Intent(this, ActivityUpdateAccount.class);
			startActivity(intent);
			
		}
		
	}



	@Override
	public String getActivityName() {
		
		return ActivityName;
	}
}









