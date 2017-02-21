package com.qianhuan.yxgsd.holagames.ac;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.holagame.util.DeviceUtil;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.ac.SdkLoginActivity.LoginSilentListener;
import com.qianhuan.yxgsd.holagames.ac.SdkLoginActivity.UpdateListener;
import com.qianhuan.yxgsd.holagames.dialog.WelcomeToast;
import com.qianhuan.yxgsd.holagames.modle.LoginCodeModel;
import com.qianhuan.yxgsd.holagames.modle.NoticeModel;
import com.qianhuan.yxgsd.holagames.modle.PackInfoModel;
import com.qianhuan.yxgsd.holagames.modle.RespModel;
import com.qianhuan.yxgsd.holagames.modle.ResponseData;
import com.qianhuan.yxgsd.holagames.tools.IlongCode;
import com.qianhuan.yxgsd.holagames.tools.Json;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;
import com.qianhuan.yxgsd.holagames.tools.UpdateUtil;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;
import com.qianhuan.yxgsd.holagames.tools.http.HttpUtil;
import com.qianhuan.yxgsd.holagames.tools.http.NetException;
import com.qianhuan.yxgsd.holagames.tools.http.SdkJsonReqHandler;

public class LoginHelper{
	
	private static Activity mActivity;
	private boolean isSilent = false;
	private static LoginHelper mHelper;
	private static LoginSilentListener mLoginListener;
	
	public static LoginHelper getInstance(){
		if(mHelper == null){
			mHelper = new LoginHelper();
		}
		return mHelper;
	}
	
	public void doLogin() {
	HashMap<String, String> map = DeviceUtil.readUserFromFiles(mActivity);
		
		//如果有用户信息，则自动登录
		if (map != null && map.size() > 0) {
			getUpdate(map);
		} else{
			//用户名不存在，直接以游客登录
			//用户名存在，游客用游客方式，正式账号显示登录界面
			getUserFromNet();	
		}
	}
	
	/**游客获取*/
	public void getUserFromNet(){
		String url = Constant.httpHost + Constant.USER_QUICK_REG;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("pid", DeviceUtil.getUniqueCode(mActivity));
		params.put("version", "201512");
		if(IlongSDK.getInstance().getDebugMode()){
			DeviceUtil.appendToDebug("getUserFromNet params: " + params.toString());
		}
		HttpUtil.newIntance().httpGet(mActivity, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, String content) {
				try {
					JSONObject json = new JSONObject(content);
					int errno = json.getInt("errno");
					if(errno == 200){
						String userName = json.getJSONObject("data").getString("username");
						String pid = DeviceUtil.getUniqueCode(mActivity);
						String userinfo = makeUserInfo(Constant.TYPE_USER_NOT_REGISTER, userName, pid);
						//开始登录
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NOT_REGISTER);
				        map.put(Constant.KEY_DATA_USERNAME, userName);
				        map.put(Constant.KEY_DATA_CONTENT, userinfo);
				        if(IlongSDK.getInstance().getDebugMode()){
							DeviceUtil.appendToDebug("getUpdate  map: " + map.toString());
						}
						getUpdate(map);
					}else{
						callFailed();				
					}
				} catch (Exception e) {
					e.printStackTrace();
					callFailed();	
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				callFailed();		
			}
		});
	}
	
	
	
	public String makeUserInfo(String type, String userName, String pwd) throws Exception{
		JSONObject json = new JSONObject();
		json.put(Constant.KEY_LOGIN_USERNAME, userName);
		if(type.equals(Constant.TYPE_USER_NORMAL)){
			json.put(Constant.KEY_LOGIN_PWD, pwd);
		}else if(type.equals(Constant.TYPE_USER_NOT_REGISTER)){
			json.put(Constant.KEY_LOGIN_PID, pwd);
		}
		return DeviceUtil.getencodeData(json.toString());
	}
	
	

	public void getUpdate(final HashMap<String, String> map) {
		//执行请求包信息
		String url = Constant.httpHost + Constant.USER_PACK_INFO;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("version", UpdateUtil.getVersion(IlongSDK.getActivity()));
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		//os",标识传给服务器的是 安卓平台还是 苹果IOS平台的，安卓端填写固定值 "android"。如果是苹果用户，填写"ios";
		params.put("os", "android");

		HttpUtil.newHttpsIntance(mActivity).httpsPost(mActivity, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, String content) {
				try {
					RespModel respModel = Json.StringToObj(content, RespModel.class);
					if (null != respModel && respModel.getErrno() == 200) {
						
						PackInfoModel packInfoModel = Json.StringToObj(respModel.getData(), PackInfoModel.class);
						//论坛地址
						IlongSDK.URL_BBS = packInfoModel.getBbs();
						
						if (packInfoModel.getKf() == 1) {
							IlongSDK.getInstance().setHasChat(true);
						}
						//包信息
						IlongSDK.getInstance().packInfoModel = packInfoModel;
//						判断强制更新不？ uptadta>0有更新，force>0 必须更新
						int update = packInfoModel.getUpdate();
						String uri = packInfoModel.getUri();
						if (update > 0 && uri != null && uri.startsWith("http:")) {
							
							IlongSDK.showUpdateCancle(mActivity, packInfoModel, new UpdateListener() {
								
								@Override
								public void doLogin() {
									//执行登录
									login(map);
								}
							});
						} 
						else {
							//执行登录
							login(map);
						}
					} else {
						callFailed();	
					}
				} catch (Exception e) {
					e.printStackTrace();
					callFailed();
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				DeviceUtil.showToast(mActivity, slException.getMessage());
				callFailed();		
			}
		});
	}
	
	public void callFailed(){
		DeviceUtil.showToast(mActivity, "登录失败");
		IlongSDK.getInstance().callbackLogin.onFailed("登录失败");		
	}
	

	public void login(final HashMap<String, String> map) {
		final String url = Constant.httpHost + Constant.USER_QUICK_LOGIN;
		final Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("sign", map.get(Constant.KEY_DATA_CONTENT));
		params.put("pack_key", IlongSDK.getInstance().getSid());
		

//		params.put("pid", DeviceUtil.getUniqueCode(mActivity));
		//android SDK 填写默认值，android。
		params.put("os", "android");
		params.put("os_version", DeviceUtil.getVersionCode(mActivity));
		params.put("manufacturer", DeviceUtil.getPhoneManufacturer());
		params.put("brand", DeviceUtil.getPhoneBrand());
		//IMEI 采用小写 imei
		params.put("imei", DeviceUtil.getIMEI(mActivity));
		//SDK版本号
		params.put("sdk_version_code", DeviceUtil.SDK_VERSION);
		
		if(IlongSDK.getInstance().getDebugMode()){
			DeviceUtil.getUniqueCode(mActivity);
			DeviceUtil.appendToDebug("login params: " + map.toString() + "\n\n  " + params.toString());
		}
		HttpUtil.newIntance().httpPost(mActivity, url, params, new SdkJsonReqHandler(params) {
			@Override
			public void ReqYes(Object reqObject, final String content) {
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (null != respModel) {
					if (respModel.getErrno() == IlongCode.S2C_SUCCESS_CODE) {
						LoginCodeModel codeModel = Json.StringToObj(respModel.getData(), LoginCodeModel.class);
						DeviceUtil.setLogout(mActivity, false);
						//记住用户类型
						if(map.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL)){
							IlongSDK.TYPE_USER = Constant.TYPE_USER_NORMAL;
							//save
							DeviceUtil.writeUserToFile(map, mActivity);
						}else{
							IlongSDK.TYPE_USER = Constant.TYPE_USER_NOT_REGISTER;
						}
						new WelcomeToast(mActivity, map.get(Constant.KEY_DATA_USERNAME)).show();
						
						//是否是切换账号类型  如果是切换账号类型，回调切换账号
//						if(isSwitch){
//							IlongSDK.getInstance().callbackLogin.onSwitchAccount(codeModel.getCode());
//						}else{
//							IlongSDK.getInstance().callbackLogin.onSuccess(codeModel.getCode());
							//公告暂时不放出来
//							String noticeId = DeviceUtil.getData(SdkLoginActivity.this,DeviceUtil.KEY_NOTICE_ID);
//							Logd.d("gst", "手机中获取到的id---"+noticeId);
//							if(noticeId.equals(IlongSDK.mNotice.id)){
//								IlongSDK.getInstance().callbackLogin.onSuccess(codeModel.getCode());
//							}else{
//								goNoticePage(codeModel.getCode());
//							}
																								
//						}
						String auth_code = Json.StringToObj(respModel.getData(),ResponseData.class).getCode();
						if(auth_code!=null && !auth_code.equals("")){
							IlongSDK.getInstance().callbackLogin.onSuccess(auth_code);
						}else{
							IlongSDK.getInstance().callbackLogin.onFailed("auth_code转换为空，登录失败");
						}
					} else {
						DeviceUtil.setLogout(mActivity, true);
						Constant.paseError(respModel.getErrno());
						IlongSDK.getInstance().callbackLogin.onFailed("登录失败");
						DeviceUtil.showToast(mActivity, "登录失败");
					}
				} else {
					DeviceUtil.showToast(mActivity, "登录失败");
					IlongSDK.getInstance().callbackLogin.onFailed("登录失败");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				
				ToastUtils.show(mActivity, "登录失败," + slException.getMessage());
			}
		});
	}
	
	public void init(Activity activity, boolean isSilent) {
		mActivity = activity;
		this.isSilent = isSilent;
	}

}
