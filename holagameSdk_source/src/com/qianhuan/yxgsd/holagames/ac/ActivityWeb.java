package com.qianhuan.yxgsd.holagames.ac;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.dialog.IlongBasicDialog;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;

public class ActivityWeb extends BaseActivity implements WebCall{
	private String TAG = this.getClass().getSimpleName();
	private ImageView loadingIv;
	private Animation loadingAnim;
	private TextView titletext ;
	private View backView ;
//	private View shareView ;
	private View title;
	
	private WebView web;
	private final String CompanyUrl = "http://www.ilongyuan.com.cn/";
	
	/**传递过来的mUserInfo的id*/
	private String id  = "";
	/**微信APPID*/
	public static  String ILONGYUAN_WX_APPID = ""; 
	/**IWAPI 是第三方app和微信通信的openapi接口*/
//	private IWXAPI api;
	/**微信支付 朋友圈的最低版本*/
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	private String ActivityName = "com.longyuan.sdk.ac.ActivityWeb";
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(web == null){
			return super.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
			web.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_web"));
//		InitWeiXinShare(ActivityWeb.this);
		InitWebTitle();
		loadingIv=(ImageView) findViewById(ResUtil.getId(this, "IlongActivityWeb_Loading"));
		loadingAnim = AnimationUtils.loadAnimation(this, ResUtil.getAnimationID(this, "loading"));  
	    loadingAnim.setInterpolator(new LinearInterpolator());
		try{
			String url = getIntent().getStringExtra("url");
			id = getIntent().getStringExtra("id");
			if(TextUtils.isEmpty(id)){
				Logd.d("SDK", "ActivityWeb中未取到用户支付密码标识");
			}
			initWebView(url);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

//	/**初始化 微信APPID,以及注册应用到微信*/
//	private void InitWeiXinShare(Activity a){
//		try {
//			ApplicationInfo info = a.getPackageManager().getApplicationInfo(a.getPackageName(), PackageManager.GET_META_DATA);
//			ILONGYUAN_WX_APPID = info.metaData.getString("ILONGYUAN_WX_APPID");
//			if(!TextUtils.isEmpty(ILONGYUAN_WX_APPID)){
//				api = WXAPIFactory.createWXAPI(a, ILONGYUAN_WX_APPID, true);
//				api.registerApp(ILONGYUAN_WX_APPID);
//			}else{
//				Logd.e("SDK", "SDK检索到微信APPID是空");
//			}
//		} catch (Throwable e) {
//			Logd.e("SDK", "SDK检索到微信APPID发生异常");
//			LogUtils.error(e);
//		}
//	}
//	/**初始化 微信APPID,以及注册应用到微信*/
//	private void InitWeiXinShare(Activity a){
//		try {
//			ApplicationInfo info = a.getPackageManager().getApplicationInfo(a.getPackageName(), PackageManager.GET_META_DATA);
//			ILONGYUAN_WX_APPID = info.metaData.getString("ILONGYUAN_WX_APPID");
//			if(!TextUtils.isEmpty(ILONGYUAN_WX_APPID)){
//				api = WXAPIFactory.createWXAPI(a, ILONGYUAN_WX_APPID, true);
//				api.registerApp(ILONGYUAN_WX_APPID);
//			}else{
//				Logd.e("SDK", "SDK检索到微信APPID是空");
//			}
//		} catch (Throwable e) {
//			Logd.e("SDK", "SDK检索到微信APPID发生异常");
//			LogUtils.error(e);
//		}
//	}
	

	/**微信中的方法*/
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**悬浮窗中 个人中心，礼包等等web页面 title的初始化*/
	private void  InitWebTitle(){
		titletext = (TextView)findViewById(ResUtil.getId(this, "IlongActivity_web_title"));
		backView = (View) findViewById(ResUtil.getId(this, "IlongActivity_web_back"));
		title = findViewById(ResUtil.getId(this, "IlongActivity_web_title_ll"));
		String text = getIntent().getStringExtra("title");
		titletext.setText(text);
		if(text!=null&&text.equals("论坛")){
			title.setVisibility(View.VISIBLE);
		}
		backView.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if(web.canGoBack()){
					web.goBack();// 返回前一个页面
				}else{
					finish();
				}
			}
			
		});
		
		findViewById(ResUtil.getId(this, "web_close_btn")).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".IlongActivity_web_back");
				ActivityWeb.this.finish();
			}
		});
//		shareView.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				web.loadUrl(Constant.getShareGameUri(IlongSDK.mToken).toString());			
//				Intent It = new Intent(Intent.ACTION_VIEW, Constant.getShareGameUri(IlongSDK.mToken));
//				startActivity(It);						
//			}
//		});
		
	}
	
	/**WebView初始化*/
	private void initWebView(final String webUrl){
		web = (WebView) findViewById(ResUtil.getId(this, "ilong_user_web"));
//		String useragent = web.getSettings().getUserAgentString();
//		Log.d("gst", "webView的user_agent-->"+useragent);
		web.setWebViewClient(new WebViewClient(){
	         @Override
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	          view.loadUrl(url);   //在当前的webview中跳转到新的url
	          return true;
	         }

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				Logd.e(TAG, url);
				findViewById(ResUtil.getId(ActivityWeb.this, "IlongActivityWeb_loading_parent")).setVisibility(View.VISIBLE);
				loadingIv.startAnimation(loadingAnim);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				findViewById(ResUtil.getId(ActivityWeb.this, "IlongActivityWeb_loading_parent")).setVisibility(View.GONE);
				loadingIv.clearAnimation();
			}
	         
	        });

		web.getSettings().setJavaScriptEnabled(true);
		web.setWebChromeClient(new MyWebChromeClient(this){

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("温馨提示").
				setMessage(message).setCancelable(false).
				setPositiveButton("确认", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						
					}
				}).
				create().show();
//				result.confirm();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("温馨提示").setMessage(message).setCancelable(false).setPositiveButton
				("确认", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
						
					}
				} ).create().show();
				return super.onJsConfirm(view, url, message, result);
			}
			
			
			
		});
//		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//允许JS弹出对话框
		web.addJavascriptInterface(new Object(){
			/**关闭页面*/
			@JavascriptInterface
			public void doFinish(){
				finish();
			}
			/**修改密码成功*/
			@JavascriptInterface
			public void onResetPwdSuccess(){
				Logd.e("tag", "onResetPwdSuccess call");
				DeviceUtil.setLogout(ActivityWeb.this, true);
			}
			/**修改密码成功*/
			@JavascriptInterface
			public void onDeleteUserInfo(){
				Logd.e("tag", "onDeleteUserInfo call");
				DeviceUtil.setLogout(ActivityWeb.this, true);
			}
			/**切换账号*/
			@JavascriptInterface
			public void onSwitchAccount(){
				Logd.e("tag", "onDeleteUserInfo call");
				DeviceUtil.setLogout(ActivityWeb.this, true);
			}
			/**复制*/
			@JavascriptInterface
			public void copy(String msg){
				Logd.e("tag", "onDeleteUserInfo call");
				DeviceUtil.setLogout(ActivityWeb.this, true);
			}
			
			@JavascriptInterface
			public void sethasPayPwd(){
				System.out.println("设置支付密码");
				Log.d("gst", "Activityweb存密码的id--"+id+"haspay_pwd");
				DeviceUtil.saveData(ActivityWeb.this, IlongSDK.mUserInfo.getId()+"haspay_pwd", "1");
			}
			/**网页点击礼包时候，相应的方法*/
			@JavascriptInterface
			public boolean getGift(String packageName, String apkDownloadUrl){
				//获取的手机中是否安装了应用信息数据,如果packageName判断手机已经安装了该应用，则返回true，如果手机对应packageName的应用，则返回false
				if(!TextUtils.isEmpty(packageName)&&!TextUtils.isEmpty(apkDownloadUrl)){
					List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
					for(int i=0 ;i< packages.size();i++){
						PackageInfo pack = packages.get(i);
						String currentPackName = "";
						currentPackName = pack.packageName;
						if(!packageName.equals(currentPackName)){
							continue;
						}else{
							return true;
						}
					}
					return false;
				}else{
					Logd.d("SDK", "--packageName为空--"+TextUtils.isEmpty(packageName));
					Logd.d("SDK", "--apkDownloadUrl为空--"+TextUtils.isEmpty(apkDownloadUrl));
					return false;
				}
			}
			/**网页调用下载游戏选择浏览器的dialog*/
			@JavascriptInterface
			public void showDownGameDialog(String apkDownloadUrl){
				showIsDownGameDialog(ActivityWeb.this , apkDownloadUrl);
			}
			/**是否切换账号*/
			@JavascriptInterface
			public void switchAccount(){
//				isShowSwitchAccountDialog(ActivityWeb.this);
				Intent intent = new Intent(ActivityWeb.this, SdkLoginActivity.class);
				intent.putExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, true);
				startActivity(intent);
				ActivityWeb.this.finish();
			}
			/**网页上的修改账号密码*/
			@JavascriptInterface
			public void doModifyUserPWD(String name ,String password){
				System.out.println("doModifyUserPWD:"+name+"  pd:"+password);
				HashMap<String,String> map = new HashMap<String, String>();
				map.put(Constant.KEY_DATA_TYPE,Constant.TYPE_USER_NORMAL );
				map.put(Constant.KEY_DATA_USERNAME, name);
				map.put(Constant.KEY_DATA_PWD,password);
				String userinfo ="";
				JSONObject json = new JSONObject();
				try {
					json.put(Constant.KEY_LOGIN_USERNAME, name);
					json.put(Constant.KEY_LOGIN_PWD, password);
					userinfo = DeviceUtil.encodeData(json.toString());
					map.put(Constant.KEY_DATA_CONTENT, userinfo);
					DeviceUtil.writeUserToFile(map, ActivityWeb.this);
				} catch (JSONException e) {
					Logd.d("gst", "网页修改密码发生异常");
					e.printStackTrace();
				}	
			}
			/**网页上的解绑手机号*/
			@JavascriptInterface
			public void doUnbindPhone(String name,String phone){
				String password = DeviceUtil.getPwdFromUser(ActivityWeb.this, phone);
				doModifyUserPWD(name,password);
			}
			
			/**这个用在 登录界面，点击 修改密码成功后，做的操作，后续修改吧*/
			@JavascriptInterface
			public void LoginActivityDoModifyPwd(String name, String password){
				doModifyUserPWD(name,password);
				ActivityWeb.this.finish();		
			}
			
			/**返回游戏信息*/
			@JavascriptInterface
			public String getGameInfo(){
				String gamerInfo = DeviceUtil.getData(ActivityWeb.this, Constant.GAME_INFO);
				return gamerInfo;
			}
			
			/**
			 * 获取手机设备信息
			 * @return jsonObject String类型的json字符串,如果报异常则返回
			 */
			@JavascriptInterface
			public String getDeviceInfo (){
				JSONObject phoneInfo = new JSONObject();
				try {
					phoneInfo.put("phoneModel", DeviceUtil.getPhoneBrand());
					phoneInfo.put("phoneImei", DeviceUtil.getIMEI(ActivityWeb.this));
					phoneInfo.put("networkeType", DeviceUtil.getPhoneNetWork(ActivityWeb.this));
					phoneInfo.put("phoneVersion", DeviceUtil.getVersionCode(ActivityWeb.this));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Logd.e(TAG, "获取手机信息失败");
					e.printStackTrace();
					return "";
				}
				return phoneInfo.toString();
			}
			
			/**用户网页上复制 激活码到系统粘贴栏
			 * 这里已经改为 适应 旧版本的方法了，所以，都返回true
			 * @return ture-->复制成功
			 */
			@JavascriptInterface
			public boolean  copyActivactionCode(String activiaction_code){
					ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					cmb.setText(activiaction_code);
					return true ;
			}
			/**构建一个 发送网页请求对象
			 * boolean  b true 分享到 好友，false 分享到 朋友圈
			 * webUrl --> 分享网址 
			 * webtitle --> 分享网页的title
			 * webdescription --> 分享的
			 * */
			@JavascriptInterface
			public void sendWebpage2WeiXin(boolean b,String webUrl,String webtitle
				,String webdescription){
//				WXWebpageObject webpage = new WXWebpageObject();
//				webpage.webpageUrl = webUrl;
//				WXMediaMessage msg = new WXMediaMessage(webpage);
//				msg.title = webtitle ;
//				msg.description = webdescription ;
//				Bitmap thumb = BitmapFactory.decodeResource(getResources(), ResUtil.getDimenId(ActivityWeb.this, "app_icon"));
//				msg.thumbData = WEIXINUtil.bmpToByteArray(thumb, true);
//				SendMessageToWX.Req req = new SendMessageToWX.Req();
//				req.transaction = buildTransaction("webpage");
//				req.message = msg;
////				req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
//				if(true == b){
//					req.scene =  SendMessageToWX.Req.WXSceneSession;
//				}else{
//					if(api.getWXAppSupportAPI() >TIMELINE_SUPPORTED_VERSION){
//					req.scene = SendMessageToWX.Req.WXSceneTimeline;
//					}else{
//						ActivityWeb.this.runOnUiThread(new Runnable() {							
//							@Override
//							public void run() {
//								Toast.makeText(ActivityWeb.this, "您的微信版本过低，无法分享到朋友圈，请升级到最新版",
//										Toast.LENGTH_SHORT).show();
//								
//							}
//						});
//						Logd.e("SDK", "用户微信版本过低，无法分享到朋友圈，请升级微信到最新版");
//					}
//				}
//				api.sendReq(req);
				Log.d("ActivityWeb", "sendWebpage2WeiXin");
			}
			@JavascriptInterface
			public void goShareSinaWeibo(String url){
				if(!TextUtils.isEmpty(url)){
					Log.d("gst", "去新浪分享的地址-->"+ url);
					Intent it = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
					startActivity(it);
				}
			}
			/**IsJSCloseShareMeue 不要删除，js调用的方法，暂时不用而已*/
//			@JavascriptInterface
//			public void isWebHasShareMeue(boolean isViewVisible){
//				Log.d("gst", "isWebHasShareMeue(b)-->"+isViewVisible);
//				if(!isViewVisible){
//					if( web.canGoBack()){
//						Log.d("gst", "web.canGoBack()--"+web.canGoBack());
//						web.goBack();
//					}else{
//						Log.d("gst", "web.canGoBack()---"+web.canGoBack()+"--ActivityWeb 结束");
//						ActivityWeb.this.finish();
//					}
//				}
//			}
			
			@JavascriptInterface
			public void WebTitleBackKeyPressed(){
				ActivityWeb.this.finish();
			}
			
		}, "bind");
		
		new Thread(new Runnable() {
			
			public void run() {
				ActivityWeb.this.runOnUiThread(new Runnable() {
					public void run() {
						web.loadUrl(webUrl);
					}
				});
			}
		}).start();
		
	}
	
	
	/**弹出 是否前去领取 礼包的dialog样式(其实是Activity)*/
//	public void goGiftActivity(String apkDownloadUrl){
//		Intent it = new Intent(ActivityWeb.this , ActivityGetGift.class);
//		it.putExtra("downGameUrl", apkDownloadUrl);
//		startActivity(it);
//	}
//	@SuppressLint("NewApi")
//	public void copyActivactionCodeA(String activiaction_code){
//		ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//		cmb.setText(activiaction_code);
//	}
	
	/**是否去下载游戏的dialog*/
	public void showIsDownGameDialog(final Context context,   final String apkDownLoadUrl){
		 
		final IlongBasicDialog ilongbasicDialog = 
				new IlongBasicDialog(context, ResUtil.getStyleId(context, "IlongBasicDialogStyle"));
		ilongbasicDialog.setCancelable(false);
		ilongbasicDialog.setCanceledOnTouchOutside(false);
		ilongbasicDialog.show();
		ilongbasicDialog.getDialogcontent().setText("还未安装这个游戏，下载安装游戏后即可使用这个礼包");
		ilongbasicDialog.getDialogtitletext().setText("前去下载游戏");
		ilongbasicDialog.getDialogCloseBtn().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ilongbasicDialog.dismiss();
			}
		});
		ilongbasicDialog.getDialogleftBtn().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ilongbasicDialog != null)
					ilongbasicDialog.dismiss();
			}
		});

		ilongbasicDialog.getDialogrightBtn().setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Uri downGameUri = Uri.parse(apkDownLoadUrl);
				if(ilongbasicDialog != null){
					ilongbasicDialog.cancel();
				}
				if(TextUtils.isEmpty(apkDownLoadUrl)){
					downGameUri = Uri.parse(apkDownLoadUrl);
//					Toast.makeText(ActivityWeb.this, "没检测到礼包对应游戏下载地址", Toast.LENGTH_SHORT).show();
					Logd.e("SDK", "后台没有配置该礼包的包名，下载地址更改为 火拉官网");
					Toast.makeText(ActivityWeb.this, "下载失败", Toast.LENGTH_LONG).show();
					return ;
				}
				if("http".equals(apkDownLoadUrl.substring(0,4))){
					Logd.e(TAG, apkDownLoadUrl);
					Toast.makeText(ActivityWeb.this, "开始下载", Toast.LENGTH_LONG).show();
					Intent It = new Intent(Intent.ACTION_VIEW, downGameUri);
					startActivity(It);
					ilongbasicDialog.dismiss();
				}
			}
		});	
	}
	
	/**网页点击  切换账号 调用dialog，增加确认动作*/
//	public void isShowSwitchAccountDialog(final Context context){
//		final IlongBasicDialog switchAccountDialog = new IlongBasicDialog(context, ResUtil.getStyleId(context, "IlongBasicDialogStyle"));
//		switchAccountDialog.show();
//		switchAccountDialog.getDialogcontent().setText("是否切换账号");
//		switchAccountDialog.getDialogtitletext().setText("切换账号");
//		switchAccountDialog.getDialogCloseBtn().setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switchAccountDialog.dismiss();
//			}
//		});
//		switchAccountDialog.getDialogleftBtn().setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//			switchAccountDialog.dismiss();
//			}
//		});
//		switchAccountDialog.getDialogrightBtn().setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				ActivityWeb.this.runOnUiThread(new Runnable() {			
//					@Override
//					public void run() {
//						Intent intent = new Intent(ActivityWeb.this, SdkLoginActivity.class);
//						intent.putExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, true);
//						startActivity(intent);
//						switchAccountDialog.dismiss();
//						ActivityWeb.this.finish();
//					}
//				});
//				
//			}
//		});
//	}
	
	@Override
	public String getActivityName() {

		return ActivityName;
	}
	
	
	
	
	
	public final static int FILECHOOSER_RESULTCODE = 1;  
	public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;  
	public ValueCallback<Uri> mUploadMessage;  
	public ValueCallback<Uri[]> mUploadMessageForAndroid5; 
	
	@Override  
    public void fileChose(ValueCallback<Uri> uploadMsg) {  
		Log.e(TAG, "fileChose");
        openFileChooserImpl(uploadMsg);  
    }  
      
    @Override  
    public void fileChose5(ValueCallback<Uri[]> uploadMsg) {  
    	Log.e(TAG, "fileChose5");
        openFileChooserImplForAndroid5(uploadMsg);  
    }  
     
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {  
    	Log.e(TAG, "openFileChooserImpl");
        mUploadMessage = uploadMsg;  
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
        i.addCategory(Intent.CATEGORY_OPENABLE);  
        i.setType("image/*");  
        startActivityForResult(Intent.createChooser(i, "File Chooser"),  
                FILECHOOSER_RESULTCODE);  
    }  
      
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) { 
    	Log.e(TAG, "openFileChooserImplForAndroid5");
        mUploadMessageForAndroid5 = uploadMsg;  
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);  
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);  
        contentSelectionIntent.setType("image/*");  
      
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);  
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);  
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");  
      
        startActivityForResult(chooserIntent,  
                FILECHOOSER_RESULTCODE_FOR_ANDROID_5);  
    }   
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode,  
	        Intent intent) {  
	    if (requestCode == FILECHOOSER_RESULTCODE) {  
	        if (null == mUploadMessage)  
	            return;  
	        Uri result = intent == null || resultCode != RESULT_OK ? null  
	                : intent.getData();  
	        mUploadMessage.onReceiveValue(result);  
	        mUploadMessage = null;  
	  
	    } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {  
	        if (null == mUploadMessageForAndroid5)  
	            return;  
	        Uri result = (intent == null || resultCode != RESULT_OK) ? null  
	                : intent.getData();  
	        if (result != null) {  
	            mUploadMessageForAndroid5.onReceiveValue(new Uri[] { result });  
	        } else {  
	            mUploadMessageForAndroid5.onReceiveValue(new Uri[] {});  
	        }  
	        mUploadMessageForAndroid5 = null;  
	    }  
	}  


}
