package com.holagames.xcds;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.holagames.xcds.ac.ActivityGameNoticePage;
import com.holagames.xcds.ac.ActivityUser;
import com.holagames.xcds.ac.ExitSDKActivity;
import com.holagames.xcds.ac.LoginHelper;
import com.holagames.xcds.ac.SdkLoginActivity;
import com.holagames.xcds.ac.SdkLoginActivity.UpdateListener;
import com.holagames.xcds.dialog.UserCardDialog;
import com.holagames.xcds.dialog.UserCardDialog.VerifyUserID;
import com.holagames.xcds.enums.LocationType;
import com.holagames.xcds.gamecenter.FloatViewPopwindow;
import com.holagames.xcds.gamecenter.FloatViewService;
import com.holagames.xcds.i.ILongExitCallback;
import com.holagames.xcds.i.ILongInitCallback;
import com.holagames.xcds.i.ILongPayCallback;
import com.holagames.xcds.i.IToken2UserInfo;
import com.holagames.xcds.i.IlongGame;
import com.holagames.xcds.i.IlongLoginCallBack;
import com.holagames.xcds.modle.FloatLocation;
import com.holagames.xcds.modle.GamerInfo;
import com.holagames.xcds.modle.Notice;
import com.holagames.xcds.modle.PackInfoModel;
import com.holagames.xcds.modle.RespModel;
import com.holagames.xcds.modle.UserInfo;
import com.holagames.xcds.pay.LyPayActivity;
import com.holagames.xcds.tools.IlongCode;
import com.holagames.xcds.tools.Json;
import com.holagames.xcds.tools.LogUtils;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.SDKMark;
import com.holagames.xcds.tools.ToastTipString;
import com.holagames.xcds.tools.http.Constant;
import com.holagames.xcds.tools.http.HttpUtil;
import com.holagames.xcds.tools.http.NetException;
import com.holagames.xcds.tools.http.SdkJsonReqHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * SDK
 * @author niexiaoqiang
 */
public class IlongSDK implements IlongGame {
	public static boolean ISLONG = true;
	private boolean isTourist = true;
	private static String appId = "";
	private static String lySid = "0";
	private Activity mActivity = null;
    
	public static UserInfo mUserInfo = null;
	public static String mToken = null;
	private boolean hasChat = false;
	
	public ILongInitCallback callbackInit = null;
	public IlongLoginCallBack callbackLogin = null;
	public ILongPayCallback callbackPay = null;
	public ILongExitCallback callbackExit = null;
	
	public static DisplayMetrics screenInfo = new DisplayMetrics();
	//论坛地址
	public static String URL_BBS = "http://bbs.ilongyuan.com.cn";
	private boolean isDebug = false;
	private boolean isBackEable = true;
	
	private boolean isInited = false;
	
	public static final String TAG = "IlongSDK";
	
	public static String debugInfo = "";
	/**用户类型*/
	public static String TYPE_USER = Constant.TYPE_USER_NORMAL;
	
	/**accountId 游戏用户id.默认值设为 unKnown */
	public static String AccountId = "unknown";
	
	/**是否显示登录界面*/
	private boolean isShowLoginView = true;
	/**包信息*/
	public PackInfoModel packInfoModel=null;
	/**悬浮窗位置*/
	public static FloatLocation floatViewLocation=null;
	public void setUserToken(Context context, final String token, final IToken2UserInfo iToken2UserInfo) {
		if(token == null || token.length() == 0 || iToken2UserInfo == null){
			Log.e(TAG, "setUserToken 参数错误");
			showToast("setUserToken 参数错误");
			return;
		}
		mToken = token;
		updateUserInfo(context, token, iToken2UserInfo);
	}

	public void updateUserInfo(Context context, final String token, final IToken2UserInfo iToken2UserInfo) {
		String url = Constant.httpHost + Constant.USER_DETAIL;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("access_token", token);
//		Log.d("gst", "updateUserInfo()中的url-->"+url+"token:"+token);
		HttpUtil.newHttpsIntance(context).httpPost(context, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (null != respModel && respModel.getErrno() == IlongCode.S2C_SUCCESS_CODE) {
					mUserInfo = Json.StringToObj(respModel.getData(), UserInfo.class);
					DeviceUtil.saveData(mActivity, mUserInfo.getId()+"haspay_pwd", mUserInfo.getPay_password());
					AccountId = mUserInfo.getUid();
					checkUserCarid(iToken2UserInfo);//检查是否要去登录实名
					Gamer.sdkCenter.Login(AccountId);
					//提示绑定手机号码,其他逻辑在bindphonenumber中去实现,这个功能待定
					//BindPhoneNumber.getInit(mActivity,ResUtil.getStyleId(mActivity, "bind_phone_dialog")).show();
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				iToken2UserInfo.onFailed();
			}
		});
	}
	
	public void checkUserCarid(final IToken2UserInfo iToken2UserInfo) {
		//登录实名，必须要先实名然后才能回调给CP登录完成
		final Notice notice = IlongSDK.getInstance().packInfoModel.getActive();//公告信息
		VerifyUserID mVerify = new VerifyUserID(){//实名认证页面回调

			@Override
			public void onSuccess() {
				if (notice!=null && !notice.getImgUrl().isEmpty() && !notice.getUrl().isEmpty()) {
					Intent it = new Intent(mActivity,ActivityGameNoticePage.class);
					mActivity.startActivity(it);
					iToken2UserInfo.onSuccess(mUserInfo);
				}
			}
			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
			}};
			//校验verify是否实名（0.需要实名  非0.不需要实名），verify_config显示位置（0.不显示 1.登录显示 2.支付显示）
		if(mUserInfo.getVerify().equals("0")&&packInfoModel.getVerify_config().equals("1")){//登录实名
			UserCardDialog userCardDialog = new UserCardDialog(mActivity);
			userCardDialog.setVerify_inf(mVerify);
			userCardDialog.setCancelable(false);
			userCardDialog.show();
		}else if(notice!=null && !notice.getImgUrl().isEmpty() && !notice.getUrl().isEmpty()){//展示公告
			Intent it = new Intent(mActivity,ActivityGameNoticePage.class);
			mActivity.startActivity(it);
			iToken2UserInfo.onSuccess(mUserInfo);
		}else{//回调用户信息
			iToken2UserInfo.onSuccess(mUserInfo);
		}
	}
	public boolean isHasChat() {
		return hasChat;
	}

	public void setHasChat(boolean hasChat) {
		this.hasChat = hasChat;
	}

	public static void initAppId(Activity a) {
		try {
			ApplicationInfo info = a.getPackageManager().getApplicationInfo(a.getPackageName(), PackageManager.GET_META_DATA);
			appId = info.metaData.getString("LONGYUAN_APPID");
		} catch (Throwable e) {
			showToast(ToastTipString.NotSetAppId);
			LogUtils.error(e);
		}
	}

	public void setSid(String sid) {
		lySid = sid;
	}

	public String initSid(Activity a) {
		lySid = SDKMark.getMark(a);
		if(lySid == null || lySid.length() == 0){
			lySid = "0";
			Logd.d("LYSDK", "sid is default");
		}
		return lySid;
	}
	
	public IlongSDK setDebugModel(boolean flag){
		isDebug = flag;
		return getInstance();
	}
	
	public boolean getDebugMode(){
		return isDebug;
	}
	
	public IlongSDK setBackEable(boolean flag){
		isBackEable = flag;
		return getInstance();
	}
	
	public boolean getBackEable(){
		return isBackEable;
	}
	
	

	public boolean isShowLoginView() {
		return isShowLoginView;
	}

	public IlongSDK setShowLoginView(boolean isShowLoginView) {
		this.isShowLoginView = isShowLoginView;
		return getInstance();
	}

	public String getSid() {
		if (lySid.length() <= 0) {
			LogUtils.warn("SID为空,请先初始化!!!");
		}
		return lySid;
	}
	
	private static void initScreen(final Context context){
		WindowManager mWindowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(screenInfo);
	}

	@Override
	public void init(Activity a, ILongInitCallback callbackInit, IlongLoginCallBack callbackLogin, 
			ILongPayCallback callbackPay, ILongExitCallback callbackExit) {
		if (a == null || callbackInit == null || callbackLogin == null || callbackPay == null
				|| callbackExit == null) {
			Log.e(TAG, "初始化参数错误，有空指针");
			callbackInit.onFailed();
			return;
		}
		this.mActivity = a;
		this.mActivity.setTheme(getThem());//设置当前主acitivity主题
		this.callbackInit = callbackInit;
		this.callbackLogin = callbackLogin;
		this.callbackPay = callbackPay;
		this.callbackExit = callbackExit;
		initAppId(mActivity);
		initSid(mActivity);
		initScreen(mActivity);
		initImageLoader();
		//初始化debug信息
		//初始化数据收集，包括异常收集
		Gamer.init(mActivity,"ly", false);
		//登录完成后设置区服
		//Gamer.setGameArea("区服");
		DeviceUtil.initDebug();
		if(isDebug){
			showToast("当前是debug模式，正式发布请将debug设置为false或不调用setDebugModel");
		}
		if (appId.length() > 0 && lySid.length() > 0){
			isInited = true;
			callbackInit.onSuccess();
		}
		else {
			callbackInit.onFailed();
		}
		Logd.e(TAG, "init call .....");
		startMonitorScreen();
	}

	/**
	 * 屏幕监控
	 */
	private void startMonitorScreen() {
		mActivity.startService(new Intent(mActivity, FloatViewService.class));
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity.getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
	}

	@Override
	public void login() {	
		if(! isInited){
			Logd.e(TAG, "请先初始化");
			return;
		}
		
		HashMap<String, String> map = DeviceUtil.readUserFromFiles(mActivity);
		boolean isGuest = (map == null || map.size() == 0);
		
		if(isShowLoginView || DeviceUtil.isLogout(mActivity)){
			//游客不受注销限制
			if(isGuest && !isShowLoginView){
				LoginHelper.getInstance().init(mActivity, true);
				LoginHelper.getInstance().doLogin();
				return;
			}
			Intent loginIntent = new Intent(mActivity, SdkLoginActivity.class);
			mActivity.startActivity(loginIntent);
		}else{
			LoginHelper.getInstance().init(mActivity, true);
			mActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					LoginHelper.getInstance().doLogin();
				}
			});
			
		}
	}

	@Override
	public void logout() {
		DeviceUtil.setLogout(mActivity, true);
		hideFloatView();
		mToken = null;
		//数据收集
		Gamer.sdkCenter.logout(AccountId);
		callbackLogin.onLogout();

	}
	
	public void onPause() {
		Gamer.onPause();
	}

	public void onResume() {
		Gamer.onResume();
	}

	@Override
	public void showFloatView() {
		FloatViewPopwindow.getInstance(mActivity);
	}

	@Override
	public void hideFloatView() {
		FloatViewPopwindow.getInstance(mActivity).destroy();
	}

	@Override
	public void exitSDK() {
		Intent exitIntent = new Intent(mActivity, ExitSDKActivity.class);
		mActivity.startActivity(exitIntent);
	}
	
	private boolean verifyParam(Bundle b){
		try {
			if (mToken == null || mToken.equals("")) {
				Toast.makeText(mActivity, "请先登录", Toast.LENGTH_LONG).show();
				callbackPay.onFailed();
				callbackLogin.onLogout();
				return false;
			}
			String amount = b.getString("amount");
			String orderId = b.getString("app_order_id");
			String uid = b.getString("app_uid");
			String notify_uri = b.getString("notify_uri");
			String product_name = b.getString("product_name");
			String product_id = b.getString("product_id");
			String userName = b.getString("app_username");
			
			if(amount == null || amount.length() < 4){
				Logd.e(TAG, "amount 错误，请保留两位小数");
			}
			if(orderId == null || orderId.length() == 0){
				Logd.e(TAG, "订单号不能为空");
			}
			if(notify_uri == null || ! notify_uri.startsWith("http")){
				Logd.e(TAG, "回调地址错误");
			}
			if(product_name == null || product_name.length() < 1){
				Logd.e(TAG, "product_name 不能为空");
			}
			if(product_id == null || product_id.length() < 1){
				Logd.e(TAG, "product_id 不能为空");
			}
			if(userName == null || userName.length() == 0){
				Log.e(TAG, "app_username 不能为空");
			}
			
			if(uid == null || uid.length() == 0){
				Logd.e(TAG, "uid 不能为空");
			}
			
			b.putString("access_token", mToken);//用户登录成功之后用authcode 获取的accessToken 值
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

	@Override
	public void pay(final Bundle bundle) {
		if(!verifyParam(bundle)){ //验证订单和商品信息是否合法
			showToast("支付参数错误");
			callbackPay.onFailed();
			return;
		}
		if(mUserInfo == null){
			showToast("登录失效，请重新登录");
			callbackPay.onFailed();
			callbackLogin.onLogout();
			Logd.e(TAG, "userinfo is null");
			return ;
		}
		if(mUserInfo.getVerify().equals("0")&&packInfoModel.getVerify_config().equals("2")){
			UserCardDialog userCardDialog = new UserCardDialog(mActivity);
			userCardDialog.setVerify_inf(new VerifyUserID() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Intent payIntent = new Intent(mActivity, LyPayActivity.class);
					bundle.putString("uid", mUserInfo.getId());//用户登录成功之后获取到的uid
					payIntent.putExtras(bundle);
					mActivity.startActivity(payIntent);
				}
				
				@Override
				public void onFailed() {
					// TODO Auto-generated method stub
					callbackPay.onFailed();
				}
			});
			userCardDialog.setCancelable(true);
			userCardDialog.setCanceledOnTouchOutside(false);
			userCardDialog.show();
			return;
		}
		Intent payIntent = new Intent(mActivity, LyPayActivity.class);
		bundle.putString("uid", mUserInfo.getId());//用户登录成功之后获取到的uid
		payIntent.putExtras(bundle);
		mActivity.startActivity(payIntent);
	}
	
	public static void showToast(final String msg){
		if(getInstance().mActivity == null){
			Log.e(TAG, "activity is null, 请初始化！");
			return;
		}
		getInstance().mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getInstance().mActivity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	public String getAppId() {
		return appId;
	}

	private static IlongSDK ilongSDK;

	private IlongSDK() {
	}

	public static IlongSDK getInstance() {
		if (ilongSDK == null) {
			ilongSDK = new IlongSDK();
		}
		return ilongSDK;
	}

	public static Activity getActivity() {
		return getInstance().mActivity;
	}

//	public static void showUpdate(final Activity activity, final String uri) {
//		Dialog dialog = new Dialog(activity, ResUtil.getStyleId(activity, "Dialog_update"));
//		View view = LayoutInflater.from(activity).inflate(ResUtil.getLayoutId(activity, "ilong_activity_update_sdk"), null);
//		dialog.setContentView(view);
//		Button btnConfirm = (Button) view.findViewById(ResUtil.getId(activity, "btn_confirm"));
//		dialog.setCancelable(false);
//		dialog.setCanceledOnTouchOutside(false);
//		btnConfirm.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				activity.startActivity(it);
//				activity.finish();
//			}
//		});
//		dialog.show();
//	}
	
	//是否 展示强制更新  Dialog
	public static Dialog dialogUpdate;
		public static void showUpdateCancle(final Activity activity, final PackInfoModel pInfo, final UpdateListener mListener) {

			dialogUpdate = new Dialog(activity, ResUtil.getStyleId(activity,"ilongyuanAppUpdataCanCancle"));
			View appUpdataPage = activity.getLayoutInflater().inflate(
					ResUtil.getLayoutId(activity,"ilong_app_updata_can_cancle_dialog"), null);
			Button CancleBtn = (Button) appUpdataPage.findViewById(ResUtil.getId(activity, "appUpdataCancle"));

			final Button OKUpdataBtn = (Button) appUpdataPage.findViewById(ResUtil.getId(activity, "appUpdataOk"));
			
			View view = appUpdataPage.findViewById(ResUtil.getId(activity, "CancleBtnfatherReleLayout"));
			TextView content = (TextView)appUpdataPage.findViewById(ResUtil.getId(activity, "ilong_update_content"));
			content.setText(pInfo.getUpdate_msg());
			if (pInfo.getForce() == 1) {
				CancleBtn.setVisibility(View.GONE);
				view.setVisibility(View.GONE);
			}

			CancleBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mListener.doLogin();
					hideDialog();
					Gamer.sdkCenter.ButtonClick(AccountId,DeviceUtil.getRunningActivityName(activity)+".ShowUpdateCancleCancle");
				}
			});

			OKUpdataBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					hideDialog();
					Gamer.sdkCenter.ButtonClick(AccountId,DeviceUtil.getRunningActivityName(activity)+".ShowUpdateCancleUpdateOK");
					Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(pInfo.getUri()));
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(it);
					activity.finish();

				}
			});

			dialogUpdate.setCancelable(false);
			dialogUpdate.setCanceledOnTouchOutside(false);
			dialogUpdate.setContentView(appUpdataPage);
			dialogUpdate.show();

		}
	public static void hideDialog(){
		if(dialogUpdate != null && dialogUpdate.isShowing()){
			dialogUpdate.cancel();
		}
	}
	
	@Override
	public void showUserCenter(){
		if(mActivity == null){
			Logd.e(TAG, "mactivity is null");
			return;
		}
		if(mUserInfo == null || mToken == null || mToken.length() == 0){
			DeviceUtil.showToast(mActivity, "请先登录");
			return;
		}
		Intent intent = new Intent(mActivity, ActivityUser.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(intent);
	}
	
	/**
	 * 清除缓存
	 */
	public void deleteCache(){
		DeviceUtil.deleteFolderFile(DeviceUtil.getBasePath(mActivity), true);
		DeviceUtil.clearApplicationData(mActivity);
	}
	/**
	 * 转化成浩然游配置
	 */
	public void changeThem(){
		IlongSDK.ISLONG = !IlongSDK.ISLONG;
		mActivity.setTheme(getThem());
		Constant.initUrl();
	}
	/**
	 * 获取当前主题ID
	 * @return 主题ID
	 */
	private int getThem() {
		int them = ResUtil.getStyleId(mActivity, ISLONG?"Ilong_Theme":"HR_Theme");
		return them;
	}

	/**
	 * 设置悬浮窗位置
	 * @param typeX X轴的位置，布尔类型，分为开始中间和末尾
	 * @param typeY Y轴的位置，布尔类型，分为开始中间和末尾
	 */
	public void setFloatLocation(LocationType typeX){
		LogUtils.debug("初始化悬浮窗位置");
		if(IlongSDK.getInstance() == null){
			LogUtils.debug("请先初始化SDK");
			return;
		}
		floatViewLocation = new FloatLocation();
		floatViewLocation.setX(IlongSDK.screenInfo.widthPixels,typeX);
		
	}
	
	/**
	 * 设置悬浮窗位置
	 * @param floatX X轴位置百分比，大于0小于1的float类型
	 * @param floatY Y轴位置百分比，大于0小于1的float类型
	 */
	public void setFloatLocation(Float floatX,Float floatY){
		LogUtils.debug("初始化悬浮窗位置");
		if(IlongSDK.getInstance() == null){
			LogUtils.debug("请先初始化SDK");
			return;
		}
		if(floatX==null || floatY==null || floatX>1 || floatX<0 || floatY>1 ||floatY<0){
			LogUtils.debug("悬浮窗比例参数错误");
			return;
		}
		floatViewLocation = new FloatLocation();
		floatViewLocation.setX(IlongSDK.screenInfo.widthPixels,floatX);
		floatViewLocation.setY(IlongSDK.screenInfo.heightPixels,floatY);
	}
	
	/**
	 * 是否启用游客模式,默认情况下启用
	 * @param istourist 
	 */
	public void setIsTourist(boolean istourist){
		this.isTourist = istourist;
	}
	
	public boolean getItIsTourist(){
		return isTourist;
	}
	
	
	/**
	 * 储存游戏信息
	 * @param gamerInfo 游戏信息对象，格式：jsonObject</br>
	 * 游戏区服    String gameService;  </br>
	 * 用户的游戏ID  String userId</br>
	 * 角色名称  String roleName</br>
	 * @return 是否存储成功</br>
	 */
	public boolean setUserInfo(String gamerInfo){
//		手机型号   String phoneModel</br>
//		渠道           String channel</br>
		if(gamerInfo == null){
			return false;
		} 
		//增加厂商信息  增加渠道信息
		try {
			JSONObject json = new JSONObject(gamerInfo);
			if(! json.has("channel")){
				json.put("channel", "longyuan_android");
			}
			if(! json.has("phoneModel")){
				String brand = Build.MODEL;
				json.put("phoneModel", brand);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DeviceUtil.saveData(mActivity, Constant.GAME_INFO, gamerInfo);
		return true;
	}
	
	/**
	 * 屏幕发生变化的情况
	 */
	public void onConfigurationChanged(){
		if(mUserInfo!=null){
			FloatViewPopwindow.getInstance(mActivity).onConfigurationChanged();
		}
	}
}