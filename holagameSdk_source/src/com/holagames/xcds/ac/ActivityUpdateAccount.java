package com.holagames.xcds.ac;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagame.util.Logd;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.dialog.LyProgressDialog;
import com.holagames.xcds.modle.RespModel;
import com.holagames.xcds.tools.Json;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.TimerDown;
import com.holagames.xcds.tools.ToastUtils;
import com.holagames.xcds.tools.http.Constant;
import com.holagames.xcds.tools.http.HttpUtil;
import com.holagames.xcds.tools.http.NetException;
import com.holagames.xcds.tools.http.SdkJsonReqHandler;
/**
 * 账号升级
 * @author LY
 *
 */
public class ActivityUpdateAccount extends BaseActivity{
	
	private LyProgressDialog progressDialog;
	private EditText ilong_reg_usernme;
	private EditText ilong_reg_pwd;
	
	
	
	public Dialog dialogSwitchAccount;
	public Dialog dialogBindSuccess;
	
	public static boolean isFromNotRegister = false;
	private Dialog dialogBindCancel;
	
	private boolean isPhoneUpdate = true;
    //	验证码输入框
	private EditText editCode;
	//	获取验证码
	private Button get_verif_button;
	//	底部的切换方式图片
	private ImageView updata_method_icon;
	//	验证码栏
	private View code_ry;
	//	眼睛按钮
	private ImageView close_eye_bt;
	//	安全性提示框
	private Dialog securityDialog; 
	//	提示框填充试图
	private View securityDialogView;


	private String ActivityName = "com.longyuan.sdk.ac.ActivityUpdateAccount";
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(ResUtil.getLayoutId(this, "ilong_layout_account_update"));
		
		isFromNotRegister = getIntent().getBooleanExtra(Constant.TYPE_FROM_PAY, false);
		if(isFromNotRegister){
			View switchAcc = findViewById(ResUtil.getId(this, "ilong_update_switch_account"));
			switchAcc.setVisibility(View.INVISIBLE);
		}
		initView();
	}
	
	
	
	private void initView(){
		securityDialog = new Dialog(this, ResUtil.getStyleId(this, "security_dialog"));
		securityDialogView = LayoutInflater.from(this).inflate(ResUtil.getLayoutId(this, "ilong_dialog_account_security"), null);
		securityDialog.setContentView(securityDialogView);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		securityDialog.setContentView(securityDialogView,params);
		securityDialog.show();
		
		updata_method_icon = (ImageView) findViewById(ResUtil.getId(this, "updata_method_icon"));
		code_ry = findViewById(ResUtil.getId(this, "code_ry"));
		ilong_reg_usernme = (EditText)findViewById(ResUtil.getId(this, "ilong_reg_usernme"));
		ilong_reg_pwd = (EditText)findViewById(ResUtil.getId(this, "ilong_reg_pwd"));
        //弹框的确定按钮
		securityDialogView.findViewById(ResUtil.getId(this, "security_sure")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".security_sure");
				securityDialog.dismiss();
			}
		});
		//返回按钮
		findViewById(ResUtil.getId(this, "ilong_close")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_close");
				dissmissProgressDialog();
				payCallBack();
				finish();
			}
		});
		//确定按钮
		findViewById(ResUtil.getId(this, "ilong_update_user_confirm")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_update_user_confirm" );
				String userName = ilong_reg_usernme.getText().toString().trim();
				String pwd = ilong_reg_pwd.getText().toString().trim();
				editCode = (EditText) findViewById(ResUtil.getId(ActivityUpdateAccount.this, "v_edittext"));
				String code = editCode.getText().toString().trim();
				if(isPhoneUpdate && SdkLoginActivity.verifyRegparamPhone(ActivityUpdateAccount.this, pwd, userName, code)){
					setPhone(userName, pwd, code);
				}else if(SdkLoginActivity.verifyRegParamUserName(ActivityUpdateAccount.this, userName, pwd)){
					setUser(userName, pwd);
				}				
			}
		});
		
		//切换账号
		findViewById(ResUtil.getId(this, "ilong_update_switch_account")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_update_switch_account");
				v.setEnabled(false);
				showSwitchAccount();
				v.setEnabled(true);
			}
		});		
		
		get_verif_button = (Button) findViewById(ResUtil.getId(this, "get_verif_button"));
		get_verif_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".get_verif_button");
				//获取验证码
				String phone = ilong_reg_usernme.getText().toString();
				if (null == phone || phone.isEmpty()) {
					ToastUtils.show(ActivityUpdateAccount.this, "请输入手机号");
					return;
				}
				sendSms(phone);
			}
		});
	
		//切换升级方式
		final ImageView name_icon = (ImageView) findViewById(ResUtil.getId(this, "name_icon"));
		findViewById(ResUtil.getId(this, "change_updata_method")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".change_updata_method");
				if(isPhoneUpdate){
					ilong_reg_usernme.setHint("请输入账号");
					InputFilter[] filters = {new InputFilter.LengthFilter(15)};
					ilong_reg_usernme.setFilters(filters);
					ilong_reg_usernme.setText(null);
					ilong_reg_pwd.setText(null);
					ilong_reg_usernme.setInputType(InputType.TYPE_CLASS_TEXT);
					code_ry.setVisibility(View.GONE);
					name_icon.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, IlongSDK.ISLONG?"ilong_icon_user":"hr_icon_user"));
					updata_method_icon.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, IlongSDK.ISLONG?"ilong_account_phone":"hr_account_phone"));
				}else{
					InputFilter[] filters = {new InputFilter.LengthFilter(11)};
					ilong_reg_usernme.setFilters(filters);
					ilong_reg_usernme.setText(null);
					ilong_reg_pwd.setText(null);
					ilong_reg_usernme.setHint("请输入11位手机号");
					ilong_reg_usernme.setInputType(InputType.TYPE_CLASS_PHONE);
					code_ry.setVisibility(View.VISIBLE);
					name_icon.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, IlongSDK.ISLONG?"ilong_icon_phone":"hr_icon_phone"));
					updata_method_icon.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, IlongSDK.ISLONG?"ilong_account_username":"hr_account_username"));
				}
				isPhoneUpdate = !isPhoneUpdate;
				
			}
		});
		
		close_eye_bt = (ImageView) findViewById(ResUtil.getId(this, "ilong_close_eye_bt"));
		close_eye_bt.setSelected(false);
		close_eye_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,ActivityName+".ilong_close_eye_bt");
				if(!close_eye_bt.isSelected()){
					ilong_reg_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					close_eye_bt.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, "ilong_eye"));
				}else{
					close_eye_bt.setImageResource(ResUtil.getDrawableId(ActivityUpdateAccount.this, IlongSDK.ISLONG?"ilong_close_eye":"hr_eye"));
					ilong_reg_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				ilong_reg_pwd.setSelection(ilong_reg_pwd.getText().toString().length());
				close_eye_bt.setSelected(!close_eye_bt.isSelected());
			}
		});
	}
	
	
	private String makeUserContent(String userName, String pwd) throws Exception{
		JSONObject json = new JSONObject();
		json.put(Constant.KEY_LOGIN_USERNAME, userName);
		json.put(Constant.KEY_LOGIN_PWD, pwd);
		return DeviceUtil.getencodeData(json.toString());
	}
	/**
	 * 
	 * 手机升级
	 */
	public void setPhone(final String phone, final String password, final String verify_code) {
		showProgressDialog();
		String url = Constant.httpHost + Constant.ACCOUNT_USER_PHONE;
		Map<String, Object> params = new HashMap<String, Object>(0);
		JSONObject phoneJson  = new JSONObject();
		try {
			phoneJson.put("phone", phone);
			phoneJson.put("code", verify_code);
			phoneJson.put("access_token", IlongSDK.mToken);
			phoneJson.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("client_id",IlongSDK.getInstance().getAppId());
		params.put("sign", DeviceUtil.getencodeData(phoneJson.toString()));
		 HttpUtil.newHttpsIntance(ActivityUpdateAccount.this).httpPost(ActivityUpdateAccount.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					//注册成功，就清除掉注册信息
					ilong_reg_usernme.setText("");
					ilong_reg_pwd.setText("");
					editCode.setText("");
					try {
						final HashMap<String, String> map = new HashMap<String, String>();
						map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
						map.put(Constant.KEY_DATA_USERNAME, phone);
						map.put(Constant.KEY_DATA_PWD, password);
						map.put(Constant.KEY_DATA_CONTENT, makeUserContent(phone, password));
						DeviceUtil.writeUserToFile(map, ActivityUpdateAccount.this);
						//升级前的用户名
						String oldUserName = IlongSDK.getInstance().mUserInfo.getName();
						DeviceUtil.updataUserInfoToLocal(oldUserName, map, ActivityUpdateAccount.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					IlongSDK.TYPE_USER = Constant.TYPE_USER_NORMAL;
					dissmissProgressDialog();
					showBindSuccess(phone);
					//增加绑定手机数据上传
					Gamer.sdkCenter.MobileBind(IlongSDK.AccountId,phone, true);
				}else{
					dissmissProgressDialog();
					Constant.paseError(respModel.getErrno());
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				System.out.println(reqObject.toString() + ", " + slException.toString());
				dissmissProgressDialog();
				ToastUtils.show(ActivityUpdateAccount.this, "注册失败," + slException.getMessage());
			}
		});
	}
	
	/**
	 * 用户名绑定
	 */
	public void setUser(final String userName, final String pwd) {
		showProgressDialog();
		String sign = "";
		try {			
			sign = makeUserInfo(userName, pwd);
		} catch (Exception e) {
			e.printStackTrace();
			DeviceUtil.showToast(this, "请求失败，请重试");
			return;
		}
		String url = Constant.httpHost + Constant.USER_RENAME;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("version", "201512");
		params.put("sign", sign);
		params.put("access_token", IlongSDK.mToken);
		params.put("mac", DeviceUtil.getIMEI(this));
		params.put("pid", DeviceUtil.getUniqueCode(this));
		HttpUtil.newIntance().httpPost(this, url, params, new SdkJsonReqHandler(params)  {

			@Override
			public void ReqYes(Object reqObject, final String content) {
 				try {
					JSONObject json = new JSONObject(content);
					Log.d(TAG, "bindUserNameCall:"+content);
					int errno = json.getInt("errno");
					if(errno == 200){
                        DeviceUtil.showToast(ActivityUpdateAccount.this, "账户升级成功");
						
						final HashMap<String, String> map = new HashMap<String, String>();
						map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
						map.put(Constant.KEY_DATA_USERNAME, userName);
						map.put(Constant.KEY_DATA_PWD, pwd);
						map.put(Constant.KEY_DATA_CONTENT, makeUserContent(userName, pwd));
						DeviceUtil.writeUserToFile(map, ActivityUpdateAccount.this);
						//升级前的用户名
						String oldUserName = IlongSDK.getInstance().mUserInfo.getName();
						DeviceUtil.updataUserInfoToLocal(oldUserName, map, ActivityUpdateAccount.this);
						IlongSDK.TYPE_USER = Constant.TYPE_USER_NORMAL;
						dissmissProgressDialog();
						showBindSuccess(userName);
						//增加绑定手机数据上传
//						Gamer.sdkCenter.MobileBind(IlongSDK.AccountId,"", true);
					}else{
						dissmissProgressDialog();
						Constant.paseError(errno);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				ToastUtils.show(ActivityUpdateAccount.this, "请求失败: " + slException.getMessage());
			}
		});
	}

	/**
	 * 发送验证码
	 * @param phone
	 */
	public void sendSms(final String phone) {
		if(!isMobileNum(phone))
		{
			ToastUtils.show(ActivityUpdateAccount.this, "请输入正确的号码");
			return;
		}
		showProgressDialog();
		get_verif_button.setEnabled(false);
		String url = Constant.httpHost + Constant.ACCOUNT_USER_SMS;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("phone",phone);
		params.put("client_id",IlongSDK.getInstance().getAppId());
		if(true){
			Log.e("", url + ", " + params.toString());
		}
	    HttpUtil.newHttpsIntance(this).httpPost(this, url, params, new SdkJsonReqHandler(params) {
			@Override
			public void ReqYes(Object reqObject, final String content) {
				Logd.d(TAG, "升级时发送短信接口调用Content："+content);
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					new TimerDown(get_verif_button, 60 * 1000, 1000).start();
					ToastUtils.show(ActivityUpdateAccount.this, "短信验证码已发送，请注意查收");
				} else if(respModel.getErrno() ==  Constant.ERRNO_MOBILE_EXISTS){
					ToastUtils.show(ActivityUpdateAccount.this, "该手机号已绑定");
					get_verif_button.setEnabled(true);
				}else if(respModel.getErrno() ==  Constant.API_ERR_SMS){
					ToastUtils.show(ActivityUpdateAccount.this, Constant.paseError(respModel.getErrno()));
					get_verif_button.setEnabled(true);
				}
				else{
					get_verif_button.setEnabled(true);
					ToastUtils.show(ActivityUpdateAccount.this, "请不要频繁发送验证码");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				ToastUtils.show(ActivityUpdateAccount.this, "发送失败," + slException.getMessage());
				get_verif_button.setEnabled(true);
			}
		});
	}
			
	/**
	 * 显示加载框
	 */
	private void showProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = new LyProgressDialog(this);
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 隐藏加载框
	 */
	private void dissmissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(dialogBindCancel != null && dialogBindCancel.isShowing()){
				dialogBindCancel.cancel();
			}else{
				showBindCancel();
			}
        	return true;
        }
		
        return super.onKeyDown(keyCode, event);
    }
	
	
	public void showBindSuccess(String phone){
		dialogBindSuccess = new Dialog(this, ResUtil.getStyleId(this,"ilongyuanAppUpdataCanCancle"));
		View view = getLayoutInflater().inflate(ResUtil.getLayoutId(this,"ilong_dialog_update_success"), null);
		View continueBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_go"));
		TextView text = (TextView) view.findViewById(ResUtil.getId(this, "ilong_text_phone"));
		text.setText(phone);
		
		continueBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if(dialogBindSuccess != null && dialogBindSuccess.isShowing()){
						dialogBindSuccess.cancel();
						finish();
						payCallBack();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		dialogBindSuccess.setCancelable(false);
		dialogBindSuccess.setCanceledOnTouchOutside(false);
		dialogBindSuccess.setContentView(view);
		dialogBindSuccess.show();
	}
	
	
	
	/**显示绑定手机界面*/
	public void showSwitchAccount(){
		dialogSwitchAccount = new Dialog(this, ResUtil.getStyleId(this,"ilongyuanAppUpdataCanCancle"));
		View view = getLayoutInflater().inflate(ResUtil.getLayoutId(this,"ilong_dialog_update_switch_alert"), null);
		View CancleBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_cancel"));
		View continueBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_go"));
		View cancelBtnRight = view.findViewById(ResUtil.getId(this, "ilong_bind_close"));
		cancelBtnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,"switchAccount_close");
				try {
					if(dialogSwitchAccount != null && dialogSwitchAccount.isShowing()){
						dialogSwitchAccount.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		//取消绑定，此时会退出到游戏主界面，并回调支付是否成功
		CancleBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,"switchAccount_cancle");
				try {
					if(dialogSwitchAccount != null && dialogSwitchAccount.isShowing()){
						dialogSwitchAccount.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
		//继续绑定
		continueBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,"switchAccount_ok");
				try {
					v.setEnabled(false);
					if(dialogSwitchAccount != null && dialogSwitchAccount.isShowing()){
						dialogSwitchAccount.cancel();
					}
                    //					切换按钮允许返回
					IlongSDK.getInstance().setBackEable(true);
					Intent intent = new Intent(ActivityUpdateAccount.this, SdkLoginActivity.class);
					intent.putExtra(Constant.TYPE_IS_LOGIN_SWITCH_ACCOUNT, true);
					startActivity(intent);
					finish();
					v.setEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		dialogSwitchAccount.setCancelable(true);
		dialogSwitchAccount.setCanceledOnTouchOutside(false);
		dialogSwitchAccount.setContentView(view);
		dialogSwitchAccount.show();
	}
	
	/**显示取消绑定界面*/
	public void showBindCancel(){
		dialogBindCancel = new Dialog(this, ResUtil.getStyleId(this,"ilongyuanAppUpdataCanCancle"));
		View view = getLayoutInflater().inflate(ResUtil.getLayoutId(this,"ilong_dialog_update_user_cancel"), null);
		//绑定成功是否进入游戏
		View gotoGameBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_go"));
		View continueBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_cancel"));
		gotoGameBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,"switchAccount_cancle");
				try {
					if(dialogBindCancel != null && dialogBindCancel.isShowing()){
						dialogBindCancel.cancel();
					}
					//回调是否支付成功
					payCallBack();
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
		//继续绑定
		continueBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId,"switchAccount_ok");
				try {
					if(dialogBindCancel != null && dialogBindCancel.isShowing()){
						dialogBindCancel.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		dialogBindCancel.setCancelable(true);
		dialogBindCancel.setCanceledOnTouchOutside(false);
		dialogBindCancel.setContentView(view);
		dialogBindCancel.show();
	}
	
	private void payCallBack(){
		if(isFromNotRegister){
			IlongSDK.getInstance().callbackPay.onSuccess();
		}
		
	}
	
	public String makeUserInfo(String userName, String pwd) throws Exception{
		JSONObject json = new JSONObject();
		json.put("username", userName);
		json.put("password", pwd);
		return DeviceUtil.getencodeData(json.toString());
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
	
	
	
}
