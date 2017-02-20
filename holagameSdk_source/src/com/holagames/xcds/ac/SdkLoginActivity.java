package com.holagames.xcds.ac;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.dialog.BindPhoneNumber;
import com.holagames.xcds.dialog.LoginShowUserInfoPopwindow;
import com.holagames.xcds.dialog.LyProgressDialog;
import com.holagames.xcds.dialog.WelcomeToast;
import com.holagames.xcds.dialog.LoginShowUserInfoPopwindow.OnItemOnclick;
import com.holagames.xcds.modle.LoginCodeModel;
import com.holagames.xcds.modle.NoticeModel;
import com.holagames.xcds.modle.PackInfoModel;
import com.holagames.xcds.modle.RespModel;
import com.holagames.xcds.modle.ResponseData;
import com.holagames.xcds.tools.IlongCode;
import com.holagames.xcds.tools.Json;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.TimerDown;
import com.holagames.xcds.tools.ToastUtils;
import com.holagames.xcds.tools.UpdateUtil;
import com.holagames.xcds.tools.http.Constant;
import com.holagames.xcds.tools.http.HttpUtil;
import com.holagames.xcds.tools.http.NetException;
import com.holagames.xcds.tools.http.ReqTask;
import com.holagames.xcds.tools.http.ReqTask.Delegate;
import com.holagames.xcds.tools.http.SdkJsonReqHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class SdkLoginActivity extends BaseActivity {
	private LayoutInflater layoutInflater = null;
	private LyProgressDialog progressDialog;
	
	public static final String TAG = "SdkLogin";

	/**父视图,装载了登录view，注册view。但是注册view中手机注册高度比登录的高，
	 * 所以了为了测量，当注册返回的时候我们将改view去掉*/
	private ViewGroup loginDialogContainer;
	private ViewGroup full_contianer;

	/**关闭按钮*/
	private Button regist_exit;
	private View normalLoginView;
	private EditText usernameEditText;
	private EditText passwordEditText;
	/**显示更多用户信息按钮*/
	private ImageButton more_userinfo_bt;
	/**用户信息弹窗*/
	private LoginShowUserInfoPopwindow showHistoryUserInfo;
//    /**是否是切换账号*/
//	private boolean isInterrupt = false;
	/**登录时加载框*/
	private View autoLoginView;
	/**登陆时 时间倒数显示tv*/
//	private TextView autoLoginTimeDown_tv;
	/**登录时图片旋转*/
	private ImageView autoLoginTimeDown_iv;
	private Handler handler = new Handler();
	/**是否取消登录*/
//	private boolean isCancelNetwork=false;
	
	private String ActivityName = "com.longyuan.sdk.ac.SdkLoginActivity";
	
	private static final String GUESTNAME="guest";
	private static final String GUESTCONTRNT="guestcontent";
	private LoginShowUserInfoPopwindow userinfoPopwindow;
	
	private long maxDelyTime = 3*1000;
	
	private Long startTime = 0l;
	
	private SdkJsonReqHandler mJsonHandler;
	/**
	 * 自动登录的逻辑
	 */
	public void doAutoLogin(HashMap<String, String> map) {
		loginDialogContainer.setVisibility(View.GONE);
		full_contianer.setVisibility(View.GONE);
		String userName = map.get(Constant.KEY_DATA_USERNAME);
		String userType = map.get(Constant.KEY_DATA_TYPE);
		setUserName(userName,userType);
		getUpdate(true, map);
	}
	
	/**点击切换账号*/
	private void onClickSwitchAccount(){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(mJsonHandler != null){
					mJsonHandler.isInterrupted = true;
					mJsonHandler = null;
				}else{
					Logd.e("onClickSwitchAccount", "mJsonHandler is null");
				}
				autoLoginView.setVisibility(View.GONE);
				loginDialogContainer.setVisibility(View.VISIBLE);
				full_contianer.setVisibility(View.VISIBLE);
				dissmissProgressDialog();
			}
		});
		
	}

	private void initView() {
		loginDialogContainer = (FrameLayout) findViewById(ResUtil.getId(this, "ilong_dialog_container"));
		full_contianer = (ViewGroup) findViewById(ResUtil.getId(this, "full_contianer"));
		//登录时候的加载框
		autoLoginView = findViewById(ResUtil.getId(this, "ilong_auto_login_view"));
//		autoLoginTimeDown_tv = (TextView) autoLoginView.findViewById(ResUtil.getId(this, "ilong_auto_login_time_down"));
		autoLoginTimeDown_iv = (ImageView) autoLoginView.findViewById(ResUtil.getId(this, "ilong_auto_login_loading_iv"));
		//登录框
		normalLoginView = layoutInflater.inflate(ResUtil.getLayoutId(this, "ilong_layout_login_normal"), loginDialogContainer, false);
		loginDialogContainer.addView(normalLoginView);
		HashMap<String, String> map = DeviceUtil.readUserFromFiles(this);
		initNormalLoginView(map);
		//是否是切换账号类型  如果是切换账号类型，则直接显示登录界面
		boolean isSwitch = getIntent().getBooleanExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, false);
		if(isSwitch){
			showNormalView(false, "");
		}else{			
			//如果有用户信息，则是正式用户，自动登录
			if (map != null && map.size() > 0) {
				if(DeviceUtil.isLogout(this)){
					showNormalView(false, "");
				}else{
					showAutoView();
					doAutoLogin(map);
				}
			} else{
				//用户名不存在，直接以游客登录
				//用户名存在，游客用游客方式，正式账号显示登录界面
				
//				String userType = DeviceUtil.getData(this, DeviceUtil.KEY_UTYPE);
//				if(userType == null || userType.equals("")){
//					//没有任何数据
//					showAutoView();
//					getUserFromNet();
//				}else if(userType.equals(Constant.TYPE_USER_NOT_REGISTER)){
//					//用户类型为游客类型
//					showAutoView();
//					doAutoLoginNotRegister();
//				}
//				else{
//					showNormalView();
//				}	
				if(!IlongSDK.getInstance().getItIsTourist()) {
					showNormalView(false, "");
				}else{
					showAutoView();
					getUserFromNet();
				}
			}
		}
		
		regist_exit  = (Button) normalLoginView.findViewById(ResUtil.getId(this, "ilong_close"));
		regist_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".loginActivity.ilong_close");
				finish();
			}
		});
		
		/**切换登录按钮*/
		View interrupuBtn = autoLoginView.findViewById(ResUtil.getId(this, "ilong_auto_login_btn"));
		interrupuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					onClickSwitchAccount();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void showAutoView(){
		autoLoginView.setVisibility(View.VISIBLE);
		loginDialogContainer.setVisibility(View.GONE);
		full_contianer.setVisibility(View.GONE);
		//设置倒计时,展示登录加载时即倒计时开始。
		setTimeDown();
	}
	
	/**是否是游客，是否显示登录失败*/
	private void showNormalView(boolean isGUest, String msg){
//		if(msg != null && msg.length() > 0)
//			DeviceUtil.showToast(SdkLoginActivity.this, msg);
		if(isGUest){
			IlongSDK.getInstance().callbackLogin.onFailed("登录失败");
			finish();
			return;
		}
		autoLoginView.setVisibility(View.GONE);
		loginDialogContainer.setVisibility(View.VISIBLE);
		full_contianer.setVisibility(View.VISIBLE);
		
	}
	
	

	private void initNormalLoginView(HashMap<String, String> map) {
		// 用户名框
		usernameEditText = (EditText) normalLoginView.findViewById(ResUtil.getId(this, "ilong_username_edittext"));
		// 密码框
		passwordEditText = (EditText) normalLoginView.findViewById(ResUtil.getId(this, "ilong_password_edittext"));
		
		String userName = map.get(Constant.KEY_DATA_USERNAME);
		if(userName == null || map.get(Constant.KEY_DATA_USERNAME).equals("")){
			userName = DeviceUtil.getData(this, DeviceUtil.KEY_UID);
		}
		String userType = map.get(Constant.KEY_DATA_TYPE);
		//如果不是游客，则填充用户名、密码
		if(userType!=null&&!userType.equals(Constant.TYPE_USER_NOT_REGISTER)){
			usernameEditText.setText(userName);
			if(!DeviceUtil.isLogout(this))
				passwordEditText.setText(DeviceUtil.getData(this, DeviceUtil.KEY_UPWD));
		}
		//忘记密码
		normalLoginView.findViewById(ResUtil.getId(this, "forget_password")).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".forget_password"); 
				v.setEnabled(false);
				Intent intent = new Intent(SdkLoginActivity.this, ActivityWeb.class);
				intent.putExtra("url", Constant.getFrogetPasswordUri().toString());
				intent.putExtra("title", "个人中心");
				startActivity(intent);
				v.setEnabled(true);
			}
		});
		// 一键注册按钮
		View fast_reg_text = normalLoginView.findViewById(ResUtil.getId(this, "fast_reg_text"));
		fast_reg_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(showHistoryUserInfo!=null&&showHistoryUserInfo.isShowing()){
					showHistoryUserInfo.dismiss();
				}
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".fast_reg_text");
				addOneKeyRegistView();
			}
		});
		// 登陆
		final Button goIntoGameBtn = (Button) normalLoginView.findViewById(ResUtil.getId(this, "ilong_go_into_game_btn"));
		goIntoGameBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_go_into_game_btn");
				// 点击登陆按钮
				final String username = usernameEditText.getText().toString();
				final String password = passwordEditText.getText().toString();
				// 判定账号是否为空
				if (TextUtils.isEmpty(username)) {
					Toast.makeText(SdkLoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(SdkLoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
					return;
				}
				showProgressDialog();
				//执行请求包信息
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
				map.put(Constant.KEY_DATA_USERNAME, username);
				map.put(Constant.KEY_DATA_PWD, password);
				Log.d(TAG, "登录信息1map:" + map.toString());
				String userinfo = "";
				try {
					userinfo = makeUserInfo(Constant.TYPE_USER_NORMAL, username, password); //加密操作
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(SdkLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
					return;
				}
				map.put(Constant.KEY_DATA_CONTENT, userinfo);
				Log.d(TAG, "登录信息2map:" + map.toString());
				getUpdate(false, map);
			}
		});
		
		final int more_userinfo_ico_id_down;
		final int more_userinfo_ico_id_up;
		if(IlongSDK.ISLONG){
			more_userinfo_ico_id_down = ResUtil.getDrawableId(SdkLoginActivity.this, "ilong_login_da");
			more_userinfo_ico_id_up = ResUtil.getDrawableId(SdkLoginActivity.this, "ilong_login_da_close");
		}else{
			more_userinfo_ico_id_up = ResUtil.getDrawableId(SdkLoginActivity.this, "hr_login_da_close");
			more_userinfo_ico_id_down = ResUtil.getDrawableId(SdkLoginActivity.this, "hr_login_da");
		}
		//加载更多按钮
		more_userinfo_bt = (ImageButton) normalLoginView.findViewById(ResUtil.getId(this, "ilong_more_userinfo"));
		more_userinfo_bt.setSelected(false);
		//初始化下拉框，并且处理点击事件
		showHistoryUserInfo = new LoginShowUserInfoPopwindow(SdkLoginActivity.this,new OnItemOnclick() {
		
			@Override
			public void onclick(Map<String, String> userInfo, int position) {
				showHistoryUserInfo.dismiss();
				if(userInfo.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NOT_REGISTER)){
					showAutoView();
					getUserFromNet();
					dissmissProgressDialog();
					return;
				}
				String userName = userInfo.get(Constant.KEY_DATA_USERNAME);
				usernameEditText.setText(userName);
				//设置密码
				String pwd = DeviceUtil.getPwdFromUser(SdkLoginActivity.this, userName);
				passwordEditText.setText(pwd);
				Logd.e(TAG, userInfo.toString() + "\n" + userName + ", " + pwd);
			}
		});
		
		showHistoryUserInfo.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			more_userinfo_bt.setImageResource(more_userinfo_ico_id_down);
			}
		});
		more_userinfo_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RelativeLayout parent = (RelativeLayout)(more_userinfo_bt.getParent());
				//设置下拉列表参数
				showHistoryUserInfo.setHeight(parent.getHeight()*3);
				showHistoryUserInfo.setChildViewHight(parent.getHeight());
				showHistoryUserInfo.setChildViewWidth(parent.getWidth()-parent.getPaddingLeft()-parent.getPaddingRight());
				showHistoryUserInfo.setWidth(parent.getWidth()-parent.getPaddingLeft()-parent.getPaddingRight());
				if(showHistoryUserInfo.isShowing()){
					showHistoryUserInfo.dismiss();
				}else{
					showHistoryUserInfo.showAtLocation(parent, Gravity.CENTER, 0, parent.getHeight());
				}
				//切换展示更多按钮的图标
				more_userinfo_bt.setImageResource(showHistoryUserInfo.isShowing()?more_userinfo_ico_id_up:more_userinfo_ico_id_down);
			}
		});
	}
	
	private void setUserName(String userName, boolean isNotRegister){
		if(autoLoginView == null || userName == null){
			return;
		}
		TextView usernameTextView = (TextView) autoLoginView.findViewById(ResUtil.getId(this, "ilong_auto_login_username_textview"));
		if(isNotRegister){
			usernameTextView.setText("游客账号");
		}else {
			usernameTextView.setText(userName);
		}
		
	}
	
	private void setUserName(String userName,String userType){
		if(autoLoginView == null || userName == null){
			return;
		}
		TextView usernameTextView = (TextView) autoLoginView.findViewById(ResUtil.getId(this, "ilong_auto_login_username_textview"));
//		String userType = DeviceUtil.getData(this, DeviceUtil.KEY_UTYPE);
		if(userType!=null&&!userType.equals(Constant.TYPE_USER_NORMAL)){
			usernameTextView.setText("游客账号");
		}else {
			usernameTextView.setText(userName);
		}
		
	}
	
	/**游客获取*/
	public void getUserFromNet(){
		showProgressDialog();
		final String url = Constant.httpHost + Constant.USER_QUICK_REG;
		final Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("pid", DeviceUtil.getUniqueCode(this));
		params.put("version", "201512");
		if(IlongSDK.getInstance().getDebugMode()){
			DeviceUtil.appendToDebug("getUserFromNet params: " + params.toString());
		}
		
		SdkJsonReqHandler mHandler = new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, String content) {
				try {
					JSONObject json = new JSONObject(content);
					int errno = json.getInt("errno");
					if(errno == 200){
						String userName = json.getJSONObject("data").getString("username");
						setUserName(userName, true);
						String pid = DeviceUtil.getUniqueCode(SdkLoginActivity.this);
						String userinfo = makeUserInfo(Constant.TYPE_USER_NOT_REGISTER, userName, pid);
						//开始登录
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NOT_REGISTER);
				        map.put(Constant.KEY_DATA_USERNAME, userName);
				        map.put(Constant.KEY_DATA_CONTENT, userinfo);
				        //登录时写入游客用户信息
				        DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
				        if(IlongSDK.getInstance().getDebugMode()){
							DeviceUtil.appendToDebug("getUpdate  map: " + map.toString());
						}
						getUpdate(true, map);
					}else{
						dissmissProgressDialog();
						showNormalView(true, "登录失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					showNormalView(true, "登录失败");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				showNormalView(true, "登录失败");
			}
		};
		mJsonHandler = mHandler;
		
		HttpUtil.newIntance().httpGet(this, url, params, mHandler);
	}
	
	
	
	public String makeUserInfo(String type, String userName, String pwd) throws Exception{
		JSONObject json = new JSONObject();
		json.put(Constant.KEY_LOGIN_USERNAME, userName); //将用户名放到KEY_LOGIN_USERNAME里面去
		if(type.equals(Constant.TYPE_USER_NORMAL)){
			json.put(Constant.KEY_LOGIN_PWD, pwd);
		}else if(type.equals(Constant.TYPE_USER_NOT_REGISTER)){
			json.put(Constant.KEY_LOGIN_PID, pwd);
		}
		return DeviceUtil.getencodeData(json.toString());
	}
	
	

	public void getUpdate(final boolean autoLogin, final HashMap<String, String> map) {
		//执行请求包信息
		//String url = Constant.httpHost + Constant.USER_PACK_INFO;
		String url = "http://139.129.21.196/hola_sdk_server/cli_init.php";
		Map<String, Object> params = new HashMap<String, Object>(0);
//		params.put("version", UpdateUtil.getVersion(IlongSDK.getInstance().getActivity()));
//		params.put("client_id", IlongSDK.getInstance().getAppId());
//		params.put("pack_key", IlongSDK.getInstance().getSid());
//		//os",标识传给服务器的是 安卓平台还是 苹果IOS平台的，安卓端填写固定值 "android"。如果是苹果用户，填写"ios";
//		params.put("os", "android");
//		
		Logd.e(TAG, "登录信息params:" + params.toString());

		
		SdkJsonReqHandler mHandler = new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, String content) {
				try {
					Logd.e(TAG, "content:" + content);
					RespModel respModel = Json.StringToObj(content, RespModel.class);
					if (null != respModel && respModel.getErrno() == 200) {
						PackInfoModel packInfoModel = Json.StringToObj(respModel.getData(), PackInfoModel.class); //服务器返回信息
						//论坛地址
						IlongSDK.URL_BBS = packInfoModel.getBbs(); //论坛
						
						if (packInfoModel.getKf() == 1) {
							IlongSDK.getInstance().setHasChat(true);
						}
						//包信息
						IlongSDK.getInstance().packInfoModel = packInfoModel;
//						判断强制更新不？ uptadta>0有更新，force>0 必须更新
						int update = packInfoModel.getUpdate();
						String uri = packInfoModel.getUri();
						if (update > 0 && uri != null && uri.startsWith("http:")) {
							hideLoginView();
							IlongSDK.showUpdateCancle(SdkLoginActivity.this, packInfoModel, new UpdateListener() {
								
								@Override
								public void doLogin() {
									Logd.e(TAG, "登录的信息map:" + map.toString());
									//执行登陆
									login(autoLogin, map);
								}
							});
						} 
						else {
							//执行登陆
							login(autoLogin, map);
						}
					} else {
						dissmissProgressDialog();
						onClickSwitchAccount();
					}
				} catch (Exception e) {
					e.printStackTrace();
					showNormalView(true, "登录失败");
				}
			}
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				showNormalView(!map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL), "登录失败，网络异常");
			}
		};
		mJsonHandler = mHandler;
		 
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPost(SdkLoginActivity.this, url, params, mHandler);
	}
	
	public static interface UpdateListener{
		public void doLogin();
	}
	
	/**
	 * 手机注册
	 * @param phone
	 * @param password
	 * @param verify_code
	 */
	public void register1(final String phone, final String password, final String verify_code){
		showProgressDialog();
		String url = null;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("mobile", phone);
		params.put("password", password);
		params.put("code", verify_code);
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				Log.d(TAG, "content:" + content);
				onkeyRegistFinishedBtn.setEnabled(true);
				if(respModel.getErrno() == 200){
					Gamer.sdkCenter.Register(phone, "default", Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE, phone);
					usernameEditText.setText(phone);
					passwordEditText.setText(password);
					ToastUtils.show(SdkLoginActivity.this, "注册成功");
					//注册成功清除注册信息
					ilong_reg_usernme.setText("");
					ilong_reg_pwd.setText("");
					v_edittext.setText("");
					//请求登录的参数
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
					map.put(Constant.KEY_DATA_USERNAME, phone);
					map.put(Constant.KEY_DATA_PWD, password);
					Log.d(TAG, map.toString());
					String userInfo = "";
					try {
						userInfo = makeUserInfo(Constant.KEY_DATA_TYPE, phone, password);
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtils.show(SdkLoginActivity.this, "登录失败");
					}
				}
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				
			}
		});
	}
	
	/**
	 * 手机号注册
	 * @param phone
	 * @param password
	 * @param verify_code
	 */
	public void register(final String phone, final String password, final String verify_code) {
		showProgressDialog();
		String url = Constant.httpHost + Constant.USER_REGISTER_MOBILE;
		//String url = "http://139.129.21.196/hola_sdk_server/register.php"
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("mobile", phone);
		params.put("password", password);
		params.put("code", verify_code);
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				onkeyRegistFinishedBtn.setEnabled(true);
				if (respModel.getErrno() == 200) {
					Gamer.sdkCenter.Register(phone, "default",Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE, phone);
					usernameEditText.setText(phone);
					passwordEditText.setText(password);
					ToastUtils.show(SdkLoginActivity.this, "注册成功");
					//注册成功，就清除掉注册信息
					ilong_reg_usernme.setText("");
					ilong_reg_pwd.setText("");
					v_edittext.setText("");
//					//执行请求包信息,请求登录的信息
//					HashMap<String, String> map = new HashMap<String, String>();
//					map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
//					map.put(Constant.KEY_DATA_USERNAME, phone);
//					map.put(Constant.KEY_DATA_PWD, password);
//					Log.d(TAG, map.toString());
//					String userinfo = "";
//					try {
//						userinfo = makeUserInfo(Constant.TYPE_USER_NORMAL, phone, password);
//					} catch (Exception e) {
//						e.printStackTrace();
//						Toast.makeText(SdkLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
//						return;
//					}
//					map.put(Constant.KEY_DATA_CONTENT, userinfo);
//					DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
//					//完成注册即登录
//					showAutoView();
//					doAutoLogin(map);
					//回退到登录页面，如果登录失败将显示这个页面，不然将会停留在注册页面
					backToNormalLoginView();
				} else if(respModel.getErrno() == Constant.ERRNO_SMSCODE_ERROR){
					ToastUtils.show(SdkLoginActivity.this, "验证码错误，请重新输入验证码");
				} else {
					Constant.paseError(respModel.getErrno());
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				System.out.println(reqObject.toString() + ", " + slException.toString());
				dissmissProgressDialog();
				onkeyRegistFinishedBtn.setEnabled(true);
				ToastUtils.show(SdkLoginActivity.this, "注册失败," + slException.getMessage());
			}
		});
		onkeyRegistFinishedBtn.setEnabled(false);
	}

	/**
	 * 发送验证码
	 * @param phone
	 */
	public void sendSms(final String phone) {
		if(!isMobileNum(phone))
		{
			ToastUtils.show(SdkLoginActivity.this, "请输入正确的号码");
			return;
		}
		showProgressDialog();
		get_verif_button.setEnabled(false);
		String url = Constant.httpHost + Constant.USER_REG_SMS;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("is_demo", 0);
		params.put("mobile", phone);
		Logd.d(TAG, "手机验证码params:" + params.toString());
		
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				Logd.d(TAG, "手机号码发送之后服务器返回的data content:" + content);
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					new TimerDown(get_verif_button, 60 * 1000, 1000).start();
					Toast.makeText(SdkLoginActivity.this, "短信验证码已发送，请注意查收", Toast.LENGTH_LONG).show();
				} else if(respModel.getErrno() ==  Constant.ERRNO_MOBILE_EXISTS){
					ToastUtils.show(SdkLoginActivity.this, "该手机号已绑定");
					get_verif_button.setEnabled(true);
				}else if(respModel.getErrno() ==  Constant.API_ERR_SMS){
					ToastUtils.show(SdkLoginActivity.this, Constant.paseError(respModel.getErrno()));
					get_verif_button.setEnabled(true);
				}
				else{
					get_verif_button.setEnabled(true);
					ToastUtils.show(SdkLoginActivity.this, "请不要频繁发送验证码");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				ToastUtils.show(SdkLoginActivity.this, "发送失败," + slException.getMessage());
				get_verif_button.setEnabled(true);
			}
		});
	}

	
// 	{"data":{"uid":"2451750","id":"0317d64187c519b85e7c365784bbd74c","phone":"","level":"0",
//	"isPhoneBind":"0","name":"rar2016","score":"0","money":"0"},"errinfo":"","errno":200}
	/**
	 * 
	 * @param autoLogin 是否延时
	 * @param map 登录map
	 */
	public void login(final boolean autoLogin, final HashMap<String, String> map) {
			//String url = Constant.httpHost + Constant.USER_QUICK_LOGIN;
			String url = "http://139.129.21.196/hola_sdk_server/login.php"; //用户登录
			final Map<String, Object> params = new HashMap<String, Object>(0);
//			params.put("client_id", IlongSDK.getInstance().getAppId());
			params.put("cellphone", map.get(Constant.KEY_DATA_USERNAME));
			params.put("password", map.get(Constant.KEY_DATA_PWD));
//			params.put("pack_key", IlongSDK.getInstance().getSid());
//		
//			//android SDK 填写默认值，android。
//			params.put("os", "android");
//			params.put("os_version", DeviceUtil.getVersionCode(SdkLoginActivity.this));
//			params.put("manufacturer", DeviceUtil.getPhoneManufacturer());
//			params.put("brand", DeviceUtil.getPhoneBrand());
////			IMEI 采用小写 imei
//			params.put("imei", DeviceUtil.getIMEI(SdkLoginActivity.this));
//			//SDK版本号
//			params.put("sdk_version_code", DeviceUtil.SDK_VERSION);
			Logd.e(TAG, "login params" + params.toString());
			
			if(IlongSDK.getInstance().getDebugMode()){
				DeviceUtil.getUniqueCode(this);
				DeviceUtil.appendToDebug("login params: " + map.toString() + "\n\n  " + params.toString());
			}
		    SdkJsonReqHandler mHandler = new SdkJsonReqHandler(params) {
			@Override
			public void ReqYes(Object reqObject, final String content) {
				if(IlongSDK.mToken != null && !IlongSDK.mToken.isEmpty()){
					IlongSDK.getInstance().hideFloatView();
				}
				//计算网络请求的时间
				long delay = System.currentTimeMillis() - startTime;
				//计算等待时间
				delay = maxDelyTime - delay;
				//如果请求时间超过最长时间，则直接响应，否则等待设置等待时间
				if(delay < 0) delay = 0;
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
						if(isInterrupted) return;
						dissmissProgressDialog();
						RespModel respModel = Json.StringToObj(content, RespModel.class);
						Logd.e(TAG, "content1:" + content); //登录服务器返回信息 包含200代码
						if (null != respModel) {
							if (respModel.getErrno() == IlongCode.S2C_SUCCESS_CODE) { //服务器返回200
								LoginCodeModel codeModel = Json.StringToObj(respModel.getData(), LoginCodeModel.class);
//								这是测试数据
//								Gamer.sdkCenter.Login(,"一区");
								String type = map.get(Constant.KEY_DATA_TYPE);
								String name = map.get(Constant.KEY_DATA_USERNAME);
//								Gamer.sdkCenter.Login(name);
//								Gamer.sdkCenter.setAccountType(name, "default");
								if(type.equals(Constant.TYPE_USER_NOT_REGISTER)){
									name = "游客" + name;
								}
								DeviceUtil.setLogout(SdkLoginActivity.this, false);
								new WelcomeToast(SdkLoginActivity.this, name).show(); //用户名加欢迎回来的界面
								
								//记住用户类型
								if(map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL)){
									IlongSDK.TYPE_USER = Constant.TYPE_USER_NORMAL;
									//save
									DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
								}else{
									IlongSDK.TYPE_USER = Constant.TYPE_USER_NOT_REGISTER;
//									IlongSDK.mUserInfo=new UserInfo("游客","", "", "0", "0",Json.StringToObj(respModel.getData(),ResponseData.class).getOpenId(),"0","");
									//游客也保存，读取的时候注意，如果是游客，则不用
//									DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
									DeviceUtil.saveData(SdkLoginActivity.this, IlongSDK.getInstance().getAppId(), JSON.toJSONString(map));
								}
								//如果有密码，则保存密码
								if(map.containsKey(Constant.KEY_DATA_PWD)){
									DeviceUtil.saveData(SdkLoginActivity.this, DeviceUtil.KEY_UPWD, map.get(Constant.KEY_DATA_PWD));
								}
								sendRecord(map);
								//是否是切换账号类型  如果是切换账号类型，回调切换账号
								boolean isSwitch = getIntent().getBooleanExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, false);
								String auth_code = Json.StringToObj(respModel.getData(),ResponseData.class).getCode();//服务器返回的数据中获取的code
								
								Logd.e(TAG, "auth_code:" + auth_code);
								
								if(isSwitch && auth_code != null && !auth_code.isEmpty()){
									IlongSDK.getInstance().callbackLogin.onSwitchAccount(auth_code); //切换账号的auth_code
								}else if(auth_code != null && !auth_code.isEmpty()){
									IlongSDK.getInstance().callbackLogin.onSuccess(auth_code);//登录验证的auth_code
								}else{
									IlongSDK.getInstance().callbackLogin.onFailed("auth_code转换为空，登录失败");
								}
								finish();
							} else {
								DeviceUtil.setLogout(SdkLoginActivity.this, true);
								Constant.paseError(respModel.getErrno());
								showNormalView(!map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL), "登录失败");
							}
						} else {
							showNormalView(!map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL), "用户名或密码错误");
						}
						} catch (Exception e) {
							e.printStackTrace();
							ToastUtils.show(SdkLoginActivity.this, "登录失败");
							showNormalView(!map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL), "登录失败");
						}
					}
				}, delay);
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				if(isInterrupted){
					return;
				}
				ToastUtils.show(SdkLoginActivity.this, "登录失败," + slException.getMessage());
				dissmissProgressDialog();
				showNormalView(!map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL), "登录失败");
			}
		};
		mJsonHandler = mHandler;
		 
		HttpUtil.newIntance().httpPost(SdkLoginActivity.this, url, params, mHandler);
	}
	
	/**
	 * 设置倒计时
	 */
	private void setTimeDown() {
//	    if (autoLoginTimeDown_tv == null) return;
		//计时器，记录登录开始的时间
		startTime = System.currentTimeMillis();
		//设置倒计时动画
		setTimeDownAnimation();
		CountDownTimer timer = new CountDownTimer(maxDelyTime+1000, 1000) {  
	        @Override  
	        public void onTick(long millisUntilFinished) {  
//	        	autoLoginTimeDown_tv.setText(millisUntilFinished/1000+"");
	        }  
	        @Override  
	        public void onFinish() {  
//	        	autoLoginTimeDown_tv.setText("");
	        }  
	    };  
	    timer.start();
	}
    /**
     * 设置倒计时动画
     */
	private void setTimeDownAnimation(){
		if(autoLoginTimeDown_iv == null) return;
		Animation animation = AnimationUtils.loadAnimation(this, ResUtil.getAnimationID(this, "loading"));
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1000);
		autoLoginTimeDown_iv.startAnimation(animation);
	}

	public static boolean verifyRegparamPhone(Context context,String passwprd,String phone,String vcode){
		if (null == phone || phone.isEmpty() || phone.length() != 11 || !phone.startsWith("1")) {
			ToastUtils.show(context, "请输入11位手机号");
			return false;
		}
		
		if (null == passwprd || passwprd.isEmpty()) {
			ToastUtils.show(context, "请输入密码");
			return false;
		}
		if(passwprd.length() < 6){
			ToastUtils.show(context, "密码长度不能小于6位");
			return false;
		}
		if(passwprd.length() > 16){
			ToastUtils.show(context, "密码长度不能大于16位");
			return false;
		}
		if (null == vcode || vcode.isEmpty() || vcode.length() < 4 || vcode.length() > 8) {
			ToastUtils.show(context, "请输入正确的验证码");
			return false;
		}
		
		return true;
	}
	public static  boolean verifyRegParamUserName(Context context, String userName, String pwd){
		if(userName.length() < 6 || userName.length() > 16){
			Toast.makeText(context, "用户名请输入6-15位字母或数字", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(! Pattern.compile("^[A-Za-z]+$").matcher(userName.substring(0, 1)).matches()){
			Toast.makeText(context, "请输入以字母开头的用户名", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(! Pattern.compile("^[0-9a-zA-Z_]+$").matcher(userName).matches()){
			Toast.makeText(context, "用户名只能包含字母数字和下划线!", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(pwd.length() < 6 || pwd.length() > 16){
			Toast.makeText(context, "密码请输入6-16位字符", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 用户注册接口
	 * 步骤: 1、输入用户和密码判断用户名和密码的合法性，将用户名和密码以map的方式发送到服务器，服务器返回200的代码，表示注册成功。
	 * @param userName
	 * @param pwd
	 */
	private void regWithUname(final String userName, final String pwd){
		//String url = Constant.httpHost + Constant.USER_REGISTER_USERNAME;
		String url = "http://139.129.21.196/hola_sdk_server/register.php";
		if(! verifyRegParamUserName(this, userName, pwd)){
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>(0);
//		params.put("client_id", IlongSDK.getInstance().getAppId());
//		params.put("pack_key", IlongSDK.getInstance().getSid());
//		params.put("username", userName);
//		params.put("password", pwd);
		params.put("cellphone", userName);
		params.put("password", pwd);
		Log.d(TAG, "params:" + params.toString());
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPost(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				Log.d(TAG, "content:" + content);
				Gamer.sdkCenter.Register(userName, "default",Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE);
				usernameEditText.setText(userName);
				passwordEditText.setText(pwd);
				ToastUtils.show(SdkLoginActivity.this, "注册成功");
				//注册成功就清空消息
				ilong_reg_usernme.setText("");
				ilong_reg_pwd.setText("");
				v_edittext.setText("");
				backToNormalLoginView();
//				//第一次注册后，使用用户名登录
				BindPhoneNumber.isFirstRegist = true;
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				onkeyRegistFinishedBtn.setEnabled(true);
				if (respModel.getErrno() == 200) {
					Gamer.sdkCenter.Register(userName, "default",Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE);
					usernameEditText.setText(userName);
					passwordEditText.setText(pwd);
					ToastUtils.show(SdkLoginActivity.this, "注册成功");
					//注册成功就清空消息
					ilong_reg_usernme.setText("");
					ilong_reg_pwd.setText("");
					v_edittext.setText("");
////					//执行请求包信息
////					HashMap<String, String> map = new HashMap<String, String>();
////					map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
////					map.put(Constant.KEY_DATA_USERNAME, userName);
////					map.put(Constant.KEY_DATA_PWD, pwd);
////					Log.d(TAG, "map:" + map);
////					String userinfo = "";
////					try {
////						userinfo = makeUserInfo(Constant.TYPE_USER_NORMAL, userName, pwd);
////					} catch (Exception e) {
////						e.printStackTrace();
////						Toast.makeText(SdkLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
////						return;
////					}
////					map.put(Constant.KEY_DATA_CONTENT, userinfo);
////					DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
					//showAutoView();
					//doAutoLogin(map);
					//返回登录界面
					backToNormalLoginView();
					//第一次注册后，使用用户名登录
					BindPhoneNumber.isFirstRegist = true;
				} else {
					Constant.paseError(respModel.getErrno());
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				System.out.println(reqObject.toString() + ", " + slException.toString());
				dissmissProgressDialog();
				onkeyRegistFinishedBtn.setEnabled(true);
				ToastUtils.show(SdkLoginActivity.this, "注册失败," + slException.getMessage());
			}
		});
		onkeyRegistFinishedBtn.setEnabled(false);
	}
	
	/**以用户名注册*/
	private void regWithUname1(final String userName, final String pwd){
		String url = Constant.httpHost + Constant.USER_REGISTER_USERNAME;
		if(! verifyRegParamUserName(this, userName, pwd)){
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("username", userName);
		params.put("password", pwd);
		Log.d(TAG, "params:" + params.toString());
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				Log.d(TAG, "content:" + content);
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				onkeyRegistFinishedBtn.setEnabled(true);
				if (respModel.getErrno() == 200) {
					Gamer.sdkCenter.Register(userName, "default",Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE, Gamer.sdkCenter.DEFAULT_VAULE);
					usernameEditText.setText(userName);
					passwordEditText.setText(pwd);
					ToastUtils.show(SdkLoginActivity.this, "注册成功");
					//注册成功就清空消息
					ilong_reg_usernme.setText("");
					ilong_reg_pwd.setText("");
					v_edittext.setText("");
					//执行请求包信息
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
					map.put(Constant.KEY_DATA_USERNAME, userName);
					map.put(Constant.KEY_DATA_PWD, pwd);
					Log.d(TAG, "map:" + map);
					String userinfo = "";
					try {
						userinfo = makeUserInfo(Constant.TYPE_USER_NORMAL, userName, pwd);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(SdkLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
						return;
					}
					map.put(Constant.KEY_DATA_CONTENT, userinfo);
					DeviceUtil.writeUserToFile(map, SdkLoginActivity.this);
					showAutoView();
					doAutoLogin(map);
					//返回登录界面
					backToNormalLoginView();
					//第一次注册后，使用用户名登录
					BindPhoneNumber.isFirstRegist = true;
				} else {
					Constant.paseError(respModel.getErrno());
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				System.out.println(reqObject.toString() + ", " + slException.toString());
				dissmissProgressDialog();
				onkeyRegistFinishedBtn.setEnabled(true);
				ToastUtils.show(SdkLoginActivity.this, "注册失败," + slException.getMessage());
			}
		});
		onkeyRegistFinishedBtn.setEnabled(false);
	}
	
	
	public void hideLoginView() {
		if (autoLoginView != null)
			autoLoginView.setVisibility(View.GONE);
		if (loginDialogContainer != null) {
			loginDialogContainer.setVisibility(View.GONE);
			full_contianer.setVisibility(View.GONE);
		}
		dissmissProgressDialog();
	}

	private void backToNormalLoginView() {
		loginDialogContainer.removeView(oneKeyRegistView);
		//执行动画
		ObjectAnimator inLogin = ObjectAnimator.ofFloat(normalLoginView, "translationX", 0f);
		inLogin.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				usernameEditText.setEnabled(true);
				passwordEditText.setEnabled(true);
				ilong_reg_usernme.setEnabled(false);
				ilong_reg_pwd.setEnabled(false);
				v_edittext.setEnabled(false);
				
				
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		inLogin.setDuration(800);
		inLogin.setInterpolator(new OvershootInterpolator());
		ObjectAnimator outReg = ObjectAnimator.ofFloat(oneKeyRegistView, "translationX", (float) loginDialogContainer.getWidth());
		outReg.setDuration(800);
		outReg.setInterpolator(new OvershootInterpolator());
		inLogin.start();
		outReg.start();
	}

	//以下是注册页面的初始化和处理
	private View oneKeyRegistView;
	/**返回登录*/
	private View ilong_back_login_btn;
	private Button onkeyRegistFinishedBtn;
	/**获取验证码按钮*/
	private Button get_verif_button;
	/**注册方式手机或者是用户名称*/
	private EditText ilong_reg_usernme;
	/**(手机)密码和(用户名)确认密码*/
	private EditText ilong_reg_pwd;
	/**(手机)验证码和(用户明)密码*/
	private EditText v_edittext;
	
	/**顶部的蓝色条 以及 两个按钮*/
	private Button changeRegistMethod;
	/**用户名 icon*/
	private ImageView name_icon;
	/**是否是手机登录*/
	private boolean isPhone = true;
	/**当前页面的标题*/
	private TextView regist_tilte;
	/**眼睛*/
	private ImageView close_eye_bt;

	private String[] hints = new String[]{
			"请输入手机号",
			"请输入密码",
			"输入验证码",
			"请输入用户名",
			"请输入密码",
			"请确认密码"
	};
	/**
	 * 初始化手机注册
	 * @param v 点击按钮
	 */
	public void initPhoneRegist(View v){
		setVerifyCodeAnimation(-1);
		//获取验证码列
		oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_regist_code_rl")).setVisibility(View.VISIBLE);
		ilong_reg_usernme.setText("");
		ilong_reg_pwd.setText("");
		v_edittext.setText("");	
		((Button)v).setText("用户名注册");
		regist_tilte.setText("手机号注册");
		ilong_reg_usernme.setInputType(InputType.TYPE_CLASS_PHONE);
		v_edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		ilong_reg_usernme.setHint(hints[0]);
		InputFilter[] filters = {new InputFilter.LengthFilter(11)};
		ilong_reg_usernme.setFilters(filters);
		ilong_reg_pwd.setHint(hints[1]);
		v_edittext.setHint(hints[2]);
		get_verif_button.setVisibility(View.VISIBLE);
		name_icon.setImageResource(ResUtil.getDrawableId(SdkLoginActivity.this, IlongSDK.ISLONG?"ilong_icon_phone":"hr_icon_phone"));
	}
	/**
	 * 初始化用户注册
	 * @param v 点击按钮
	 */
	public void initUsernameRegist(View v){
		setVerifyCodeAnimation(1);
		((Button)v).setText("手机号注册");
		regist_tilte.setText("用户名注册");
		ilong_reg_usernme.setText("");
		ilong_reg_pwd.setText("");
		v_edittext.setText("");	
		ilong_reg_usernme.setInputType(InputType.TYPE_CLASS_TEXT);
		v_edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		ilong_reg_usernme.setHint(hints[3]);
		InputFilter[] filters = {new InputFilter.LengthFilter(15)};
		ilong_reg_usernme.setFilters(filters);
		ilong_reg_pwd.setHint(hints[4]);
		v_edittext.setHint(hints[5]);
		get_verif_button.setVisibility(View.INVISIBLE);
		name_icon.setImageResource(ResUtil.getDrawableId(SdkLoginActivity.this, IlongSDK.ISLONG?"ilong_icon_user":"hr_icon_user"));
	}
	
	/**
	 * 设置动画
	 * @param i 关闭或者是打开
	 * @param tview 上面的视图
	 * @param bview 下面的视图
	 */
	private void setVerifyCodeAnimation(final int i) {
		View tview = oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_reg_name_rl"));
		View bview = oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_reg_pwd_rl"));
		TranslateAnimation tanimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF,(i>0)?0:0.25f*i, Animation.RELATIVE_TO_SELF, (i>0)?0.25f*i:0);
		TranslateAnimation banimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF,(i>0)?0:-0.25f*i, Animation.RELATIVE_TO_SELF, (i>0)?-0.25f*i:0);
		banimation.setDuration(100);
		tanimation.setDuration(100);
		tview.startAnimation(tanimation);
		bview.startAnimation(banimation);
		banimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if(i>0){
					oneKeyRegistView.findViewById(ResUtil.getId(SdkLoginActivity.this, "ilong_regist_code_rl")).setVisibility(View.GONE);
				}else{
					oneKeyRegistView.findViewById(ResUtil.getId(SdkLoginActivity.this, "ilong_regist_code_rl")).setVisibility(View.VISIBLE);
				}
			}
		});
		//获取验证码列
				
	}

	private void OneKeyRegistView(View root){
		//初始化界面的时候是手机注册
		
		changeRegistMethod = (Button) root.findViewById(ResUtil.getId(this, "ilong_regist_username"));
		name_icon = (ImageView)(root.findViewById(ResUtil.getId(this, "name_icon")));
		changeRegistMethod.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPhone = !isPhone;
				if(isPhone){
					initPhoneRegist(v);
				}else{
					initUsernameRegist(v);
				}
				
			}
		});
	}

	private void addOneKeyRegistView() {
		if (oneKeyRegistView == null) {
			oneKeyRegistView = layoutInflater.inflate(ResUtil.getLayoutId(this, "ilong_layout_onekey_regist"), loginDialogContainer, false);
			ilong_reg_usernme = (EditText) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_reg_usernme"));
			ilong_reg_pwd = (EditText) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_reg_pwd"));
			v_edittext = (EditText) oneKeyRegistView.findViewById(ResUtil.getId(this, "v_edittext"));
			regist_tilte = (TextView) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_regist_title"));
			regist_exit  = (Button) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_close"));
			regist_exit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			ilong_back_login_btn =  oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_regist_back_login"));
			ilong_back_login_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_regist_back_login");
					backToNormalLoginView();
				}
			});
			//立即注册
			onkeyRegistFinishedBtn = (Button) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_onkey_regist_finished"));
			onkeyRegistFinishedBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_onkey_regist_finished");
					String phone = ilong_reg_usernme.getText().toString().trim();
					String passwprd = ilong_reg_pwd.getText().toString().trim();
					String vcode = v_edittext.getText().toString().trim();
					if(isPhone){
						if(verifyRegparamPhone(SdkLoginActivity.this, passwprd, phone, vcode)){ //判断密码、手机号、验证码是否合格
							register(phone, passwprd, vcode);//带着手机号、密码、验证码去注册
						}
					}else{
						//以用户名注册
						regWithUname(phone, passwprd);
					}
				}
			});
			/**
			 * 获取验证码
			 */
			get_verif_button = (Button) oneKeyRegistView.findViewById(ResUtil.getId(this, "get_verif_button"));
			get_verif_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".get_verif_button");
					//获取验证码
					String phone = ilong_reg_usernme.getText().toString();
					if (null == phone || phone.isEmpty()) {
						ToastUtils.show(SdkLoginActivity.this, "请输入手机号");
						return;
					}
					sendSms(phone);
				}
			});
			//眼睛按钮
			close_eye_bt = (ImageView) oneKeyRegistView.findViewById(ResUtil.getId(this, "ilong_close_eye_bt"));
			close_eye_bt.setSelected(false);
			close_eye_bt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".register.ilong_close");
					if(!close_eye_bt.isSelected()){
						ilong_reg_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						close_eye_bt.setImageResource(ResUtil.getDrawableId(SdkLoginActivity.this, IlongSDK.ISLONG?"ilong_eye":"hr_eye"));
					}else{
						close_eye_bt.setImageResource(ResUtil.getDrawableId(SdkLoginActivity.this, "ilong_close_eye"));
						ilong_reg_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					}
					ilong_reg_pwd.setSelection(ilong_reg_pwd.getText().toString().length());
					close_eye_bt.setSelected(!close_eye_bt.isSelected());
				}
			});
			
            //用户协议			
			oneKeyRegistView.findViewById(ResUtil.getId(this, "regist_user_agreement")).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SdkLoginActivity.this, ActivityWeb.class);
					intent.putExtra("url", Constant.getUserAgreement());
					intent.putExtra("title", "用户协议");
					startActivity(intent);
				}
			});
		}
		OneKeyRegistView(oneKeyRegistView);
		ViewHelper.setTranslationX(oneKeyRegistView, loginDialogContainer.getWidth());
		loginDialogContainer.addView(oneKeyRegistView);
		//执行动画
		ObjectAnimator outLogin = ObjectAnimator.ofFloat(normalLoginView, "translationX", (float) -loginDialogContainer.getWidth());
		outLogin.setDuration(800);
		outLogin.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				usernameEditText.setEnabled(false);
				passwordEditText.setEnabled(false);
				v_edittext.setEnabled(true);
				ilong_reg_pwd.setEnabled(true);
				ilong_reg_usernme.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});

		outLogin.setInterpolator(new OvershootInterpolator());
		ObjectAnimator inReg = ObjectAnimator.ofFloat(oneKeyRegistView, "translationX", (float) 0);
		inReg.setDuration(800);
		inReg.setInterpolator(new OvershootInterpolator());
		outLogin.start();
		inReg.start();
	}
	@Override
	public void onResume(){
		super.onResume();
		dissmissProgressDialog();
	}

	/**
	 * 显示加载框
	 */
	private void showProgressDialog() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (progressDialog == null) {
						progressDialog = new LyProgressDialog(SdkLoginActivity.this);
					}
					if(progressDialog.isShowing()){
						return;
					}
					progressDialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * 隐藏加载框
	 */
	private void dissmissProgressDialog() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		dissmissProgressDialog();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		dissmissProgressDialog();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		dissmissProgressDialog();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		layoutInflater = LayoutInflater.from(this);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_sdk"));
		setBackPress();
		initView();
		//向服务器获取用户对应设备码的用户信息
		getRecord();
	}
	
	public void setBackPress(){
		if(IlongSDK.getInstance().getBackEable()){
			//设置在4秒钟之后，才能取消登录界面
			IlongSDK.getInstance().setBackEable(false);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					IlongSDK.getInstance().setBackEable(true);
				}
			}, 1000);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	if(IlongSDK.getInstance().getBackEable()){
        		IlongSDK.getInstance().callbackLogin.onCancel();
        		return super.onKeyDown(keyCode, event);
        	}else{
        		return true;
        	}
        }
        return super.onKeyDown(keyCode, event);
    }
	
	public interface LoginSilentListener{
		void onLoginSuccess();
		void onLoginFailed();
	}

	public boolean isMobileNum(String mobiles) {
		if(mobiles.length()!=11||!mobiles.startsWith("1")||!mobiles.matches("[0-9]*"))
			return false;
        return true;
    }
	
	@Override
	public String getActivityName() {
		return ActivityName;
	}
    /**将用户信息发到服务器保存*/
	private void sendRecord(HashMap<String, String> map) {
		String url = Constant.httpHost +Constant.SEND_RECORD;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("mac", DeviceUtil.getIMEI(SdkLoginActivity.this));
		params.put("datetime", System.currentTimeMillis());
		if(map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL)){
			params.put("type", 0);
		}else{
			params.put("type", 1);
		}
		params.put("pid", DeviceUtil.getUniqueCode(SdkLoginActivity.this));
		params.put("username", map.get(Constant.KEY_DATA_USERNAME));
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(this, url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "上传账号信息和设备成功打印："+content);
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "上传账号信息和设备成失败打印：");
			}
		});
	}
	
	public void getRecord(){
		String url = Constant.httpHost +Constant.GET_RECORD;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("mac", DeviceUtil.getIMEI(SdkLoginActivity.this));
		HttpUtil.newHttpsIntance(SdkLoginActivity.this).httpsPostJSON(SdkLoginActivity.this, url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "获取账号信息和设备成功打印："+content);
				showHistoryUserInfo.setServiceUserInfo(content);
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				Logd.d(TAG, "获取账号信息和设备失败打印："+slException.toString());
			}
		});
	}
}
