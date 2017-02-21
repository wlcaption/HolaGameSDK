package com.qianhuan.yxgsd.holagames.ac;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.loopj.android.http.RequestHandle;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.dialog.LyProgressDialog;
import com.qianhuan.yxgsd.holagames.modle.RespModel;
import com.qianhuan.yxgsd.holagames.tools.Json;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.ToastUtils;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;
import com.qianhuan.yxgsd.holagames.tools.http.HttpUtil;
import com.qianhuan.yxgsd.holagames.tools.http.NetException;
import com.qianhuan.yxgsd.holagames.tools.http.SdkJsonReqHandler;


public class ActivityBindPhone extends BaseActivity{
	
	private LyProgressDialog progressDialog;
	private EditText ilong_reg_usernme;
	private EditText ilong_reg_pwd;
	
	private Button get_verif_button;
	
	public Dialog dialogBindCancel;
	public Dialog dialogBindSuccess;
	
	public boolean isFromNotRegister = false;
	
	private static String ActivityName = "com.longyuan.sdk.ac.ActivityBindPhone";
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(ResUtil.getLayoutId(this, "ilong_activity_bind_phone"));
		isFromNotRegister = getIntent().getBooleanExtra(Constant.BIND_PAY_NOT_REGISTER, false);
		initView();
	}
	
	private void payCallBack(){
		if(isFromNotRegister){
			return;
		}
		IlongSDK.getInstance().callbackPay.onSuccess();
	}
	
	private void initView(){
		get_verif_button = (Button)findViewById(ResUtil.getId(this, "get_verif_button"));
		get_verif_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".get_verif_button");
				//获取验证码
				String phone = ilong_reg_usernme.getText().toString();
				if (null == phone || phone.isEmpty() || phone.length() != 11 || !phone.startsWith("1")) {
					ToastUtils.show(ActivityBindPhone.this, "请输入手机号");
					return;
				}
				sendSms(phone);
			}
		});
		
		
		ilong_reg_usernme = (EditText)findViewById(ResUtil.getId(this, "ilong_reg_usernme"));
		ilong_reg_pwd = (EditText)findViewById(ResUtil.getId(this, "ilong_reg_pwd"));
		//返回按钮
		findViewById(ResUtil.getId(this, "ilong_bind_close")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dissmissProgressDialog();
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".ilong_bind_close");
				//回调是否支付成功
				payCallBack();
				finish();
			}
		});
		
		
		findViewById(ResUtil.getId(this, "ilong_onkey_regist_finished")).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".ilong_onkey_regist_finished");
				String phone = ilong_reg_usernme.getText().toString().trim();
				String pwd = ilong_reg_pwd.getText().toString().trim();
				EditText editVerify = (EditText) findViewById(ResUtil.getId(ActivityBindPhone.this, "v_edittext"));
				String verify = editVerify.getText().toString().trim();
				if (null == phone || phone.isEmpty() || phone.length() != 11 || !phone.startsWith("1")) {
					ToastUtils.show(ActivityBindPhone.this, "请输入手机号");
					return;
				}
				if(pwd.length() < 6){
					DeviceUtil.showToast(ActivityBindPhone.this, "密码长度至少6位");
					return;
				}
				if(pwd.length() > 16){
					DeviceUtil.showToast(ActivityBindPhone.this, "密码长度不能大于16位");
					return;
				}
				if (null == verify || verify.isEmpty() || verify.length() < 4 || verify.length() > 8) {
					ToastUtils.show(ActivityBindPhone.this, "请输入正确的验证码");
					return;
				}
				bindPhone(phone, pwd, verify);
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
		return DeviceUtil.encodeData(json.toString());
	}
	
	
	
	/**
	 * 绑定
	 */
	public void bindPhone(final String phone, final String password, final String verify_code) {
		showProgressDialog();
		final HashMap<String, String> map = new HashMap<String, String>();
		try {			
			String sign = makeUserInfo(Constant.TYPE_USER_NORMAL, phone, password);
			map.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NORMAL);
	        map.put(Constant.KEY_DATA_PHONE, phone);
	        map.put(Constant.KEY_DATA_USERNAME, phone);
	        map.put(Constant.KEY_DATA_CONTENT, sign);
		} catch (Exception e) {
			e.printStackTrace();
			DeviceUtil.showToast(this, "请求失败，请重试");
			return;
		}
		
		
		String url = Constant.httpHost + Constant.USER_BIND_PHONE;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("access_token", IlongSDK.mToken);
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("code", verify_code);
		params.put("sign", map.get(Constant.KEY_DATA_CONTENT));
		params.put("phone", phone);
		
		RequestHandle requestHandle = HttpUtil.newIntance().httpPost(this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
 				try {
					JSONObject json = new JSONObject(content);
					int errno = json.getInt("errno");
					if(errno == 200){
						DeviceUtil.showToast(ActivityBindPhone.this, "恭喜绑定成功");
						DeviceUtil.writeUserToFile(map, ActivityBindPhone.this);
						dissmissProgressDialog();
						showBindSuccess(phone);
					}else{
						dissmissProgressDialog();
						ToastUtils.show(ActivityBindPhone.this, "请求失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				System.out.println(reqObject.toString() + ", " + slException.toString());
				dissmissProgressDialog();
				ToastUtils.show(ActivityBindPhone.this, "请求失败: " + slException.getMessage());
			}
		});
	}

	/**
	 * 发送验证码
	 * @param phone
	 */
	public void sendSms(final String phone) {
		showProgressDialog();
		get_verif_button.setEnabled(false);
		String url = Constant.httpHost + Constant.USER_REG_SMS;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("client_id", IlongSDK.getInstance().getAppId());
		params.put("pack_key", IlongSDK.getInstance().getSid());
		params.put("phone", phone);
		RequestHandle requestHandle = HttpUtil.newHttpsIntance(ActivityBindPhone.this).httpPost(ActivityBindPhone.this, url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					new TimerDown(get_verif_button, 60 * 1000, 1000).start();
					Toast.makeText(ActivityBindPhone.this, "短信验证码已发送，请注意查收", Toast.LENGTH_LONG).show();
				} else if(respModel.getErrno() ==  Constant.ERRNO_MOBILE_EXISTS){
					ToastUtils.show(ActivityBindPhone.this, "该手机号已绑定");
					get_verif_button.setEnabled(true);
				}
				else{
					get_verif_button.setEnabled(true);
					ToastUtils.show(ActivityBindPhone.this, "请不要频繁发送验证码");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				ToastUtils.show(ActivityBindPhone.this, "发送失败," + slException.getMessage());
				get_verif_button.setEnabled(true);
			}
		});
	}

	/**
	 * 多少秒后重新获取
	 * @author niexiaoqiang
	 */
	class TimerDown extends CountDownTimer {
		private Button mButton;

		public TimerDown(Button button, long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			this.mButton = button;
		}

		@Override
		public void onFinish() {
			mButton.setText("获取验证码");
			mButton.setTextColor(Color.argb(0XFF, 0X2A, 0X6E, 0xD3));
			mButton.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mButton.setEnabled(false);
			mButton.setTextColor(Color.argb(0XFF, 0X92, 0X92, 0x92));
			mButton.setText("重新获取(" + millisUntilFinished / 1000 + ")S");
		}
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
		View view = getLayoutInflater().inflate(ResUtil.getLayoutId(this,"ilong_dialog_bindphone_success"), null);
		View continueBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_go"));
		TextView text = (TextView) view.findViewById(ResUtil.getId(this, "ilong_text_phone"));
		text.setText(phone);
		
		continueBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, ActivityName+".ilong_bind_Success");
					if(dialogBindSuccess != null && dialogBindSuccess.isShowing()){
						dialogBindSuccess.cancel();
						finish();
						//回调是否支付成功
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
	public void showBindCancel(){
		dialogBindCancel = new Dialog(this, ResUtil.getStyleId(this,"ilongyuanAppUpdataCanCancle"));
		View view = getLayoutInflater().inflate(ResUtil.getLayoutId(this,"ilong_dialog_bindphone_cancel"), null);
		View CancleBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_cancel"));
		View continueBtn = view.findViewById(ResUtil.getId(this, "ilong_bind_go"));
		//取消绑定，此时会退出到游戏主界面，并回调支付是否成功
		CancleBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, "cancle.bind.ilong_bind_cancel ");
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
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, "cancle.bind.ilong_bind_go");
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
	
	@Override
	public String getActivityName() {
		
		return ActivityName;
	}
	
	
	
	
}
