package com.holagames.xcds.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.holagame.tool.Gamer;
import com.holagame.util.DeviceUtil;
import com.holagames.xcds.IlongSDK;
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
 * 是否绑定手机弹窗
 * 展示说明：
 * 1.第一次注册登录不提示，第二次登录提示
 * 2.每天只提示一次
 * 3.用不提示后，将不再提示
 * @author 邹龙
 *
 */
public class BindPhoneNumber extends Dialog{
	private LyProgressDialog progressDialog;
	//最后一次展示的时间
	private final String LASTSHOW = "lastShow";
    //是否不在显示
	private final String NEVERSHOW= "isBindPhoneNumber";
    //主视图
	private View mainView;
	//发送验证码
	private Button getVerif_bt;
	//发送绑定消息
	private Button sendBind_bt;
	//关闭
	private Button close_bt;
	//不再提示
	private Button neverShow_bt;
	//手机号输入框
	private EditText phoneNumber_et;
	//验证码输入框
	private EditText code_et;
	//单例
	public static BindPhoneNumber bindPhoneNumber;
	//是否是第一次注册登录
	public static boolean isFirstRegist = false;
	
	public static final String  DialogName = "BindPhoneNumber";
	
	private ArrayList<String> lastShowUserInfo;
	public static BindPhoneNumber getInit(Context context,int theme){
		if(bindPhoneNumber == null){
			bindPhoneNumber = new BindPhoneNumber(context,theme);
		}
		return bindPhoneNumber;
	}
	public BindPhoneNumber(Context context, int theme) {
		super(context, theme);
		InitView(context);
		InitData();
	}
	private void InitData() {
		// TODO Auto-generated method stub
		lastShowUserInfo = new ArrayList<String>();
		setCanceledOnTouchOutside(false);
		setContentView(mainView);
		//获取验证码
		getVerif_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, DialogName+".ilong_get_verif_button");
				String phoneNumber = phoneNumber_et.getText().toString().trim();
				sendSms(phoneNumber);
			}
		});
		
		//获取验证码
		sendBind_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, DialogName+".ilong_onkey_bind_phone_finished");
				String phoneNumber = phoneNumber_et.getText().toString().trim();
				String code = code_et.getText().toString().trim();
				bingPhoneNumber(phoneNumber,code);
			}
		});
		//关闭
		close_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, DialogName+".ilong_close");
				dismiss();
			}
		});
		//永不提示
		neverShow_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, DialogName+".iong_bing_phone_never_show");
				DeviceUtil.saveData(getContext(), NEVERSHOW, "false");
				dismiss();
			}
		});
	}
	
	/**
	 * 是否要显示
	 * @return 是否要初始化
	 */
	private boolean isShow() {
		String neverShow = DeviceUtil.getData(getContext(), NEVERSHOW);
		String lastShow = DeviceUtil.getData(getContext(), LASTSHOW);
		if(!neverShow.equals("")){//永不显示
			return false;
		}else if(!IlongSDK.TYPE_USER.equals(Constant.TYPE_USER_NORMAL)){//不是正式用户则不显示
			return false;
		}else if(IlongSDK.mUserInfo!=null && IlongSDK.mUserInfo.getIsPhoneBind().equals("1")){//手机已经绑定不显示
			return false;
		}else if(isLastShow()){//今天已经显示过了不显示
			return false;
		}
		return true;
	}

	private void InitView(Context context) {
		mainView = getLayoutInflater().inflate((ResUtil.getLayoutId(context, "ilong_dialog_bind_phone_number")),null);
		getVerif_bt = (Button) mainView.findViewById(ResUtil.getId(context, "ilong_get_verif_button"));
		sendBind_bt = (Button) mainView.findViewById(ResUtil.getId(context, "ilong_onkey_bind_phone_finished"));
		phoneNumber_et = (EditText) mainView.findViewById(ResUtil.getId(context, "ilong_bind_phone_usernme"));
		code_et = (EditText) mainView.findViewById(ResUtil.getId(context, "ilong_v_edittext"));
		close_bt = (Button) mainView.findViewById(ResUtil.getId(context, "ilong_close"));
		neverShow_bt = (Button) mainView.findViewById(ResUtil.getId(context, "iong_bing_phone_never_show"));
	}

	/**
	 * 发送验证码
	 * @param phone
	 */
	public void sendSms(final String phone) {
		if(!isMobileNum(phone))
		{
			ToastUtils.show(getContext(), "请输入正确的号码");
			return;
		}
		showProgressDialog();
		getVerif_bt.setEnabled(false);
		String url = Constant.httpHost + Constant.BIND_PHONENUMBER_GET_CODE;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("mobile", phone);
		if(true){
			Log.e("", url + ", " + params.toString());
		}
	    HttpUtil.newHttpsIntance(getContext()).httpsPostJSON(getContext(), url, params, new SdkJsonReqHandler(params) {

			@Override
			public void ReqYes(Object reqObject, final String content) {
				dissmissProgressDialog();
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					new TimerDown(getVerif_bt, 60 * 1000, 1000).start();
					ToastUtils.show(getContext(), "短信验证码已发送，请注意查收");
				} else if(respModel.getErrno() ==  Constant.ERRNO_MOBILE_EXISTS){
					ToastUtils.show(getContext(), "该手机号已绑定");
					getVerif_bt.setEnabled(true);
				}else if(respModel.getErrno() ==  Constant.API_ERR_SMS){
					ToastUtils.show(getContext(), Constant.paseError(respModel.getErrno()));
					getVerif_bt.setEnabled(true);
				}
				else{
					getVerif_bt.setEnabled(true);
					ToastUtils.show(getContext(), "请不要频繁发送验证码");
				}
			}

			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				dissmissProgressDialog();
				ToastUtils.show(getContext(), "发送失败," + slException.getMessage());
				getVerif_bt.setEnabled(true);
			}
		});
	}
	
	/**
	 * 绑定手机
	 * @param phoneNumber 手机号
	 * @param code 验证码
	 */
	private void bingPhoneNumber(String phoneNumber, String code) {
		if (null == phoneNumber || phoneNumber.isEmpty() || phoneNumber.length() != 11 || !phoneNumber.startsWith("1")) {
			ToastUtils.show(getContext(), "请输入11位手机号");
			return;
		}
		
		if (null == code || code.isEmpty() || code.length() < 4 || code.length() > 8) {
			ToastUtils.show(getContext(), "请输入正确的验证码");
			return;
		}
		sendBind_bt.setFocusable(false);
		String url = Constant.httpHost + Constant.BIND_PHONENUMBER;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("access_token", IlongSDK.mToken);
		params.put("mobile", phoneNumber);
		params.put("verify_code", code);
		HttpUtil.newHttpsIntance(getContext()).httpsPostJSON(getContext(), url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				// TODO Auto-generated method stub
				RespModel respModel = Json.StringToObj(content, RespModel.class);
				if (respModel.getErrno() == 200) {
					ToastUtils.show(getContext(), "绑定成功");
					dismiss();
				}else{
					ToastUtils.show(getContext(), "绑定失败："+respModel.getErrinfo());
				}
				sendBind_bt.setFocusable(true);
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				// TODO Auto-generated method stub
				ToastUtils.show(getContext(), "绑定失败："+slException.getMessage());
				sendBind_bt.setFocusable(true);
			}
		});
		
	}
	public boolean isMobileNum(String mobiles) {
		if(mobiles.length()!=11||!mobiles.startsWith("1")||!mobiles.matches("[0-9]*"))
			return false;
        return true;
    }
	
	/**
	 * 显示加载框
	 */
	private void showProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = new LyProgressDialog(getContext());
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		if(bindPhoneNumber==null|| !isShow() || isFirstRegist)
			return;
		saveLastShow();
		super.show();
	}
	
	//账号最后一次弹出的时间,如果已经弹出返回true
	@SuppressWarnings("unchecked")
	private boolean isLastShow() {
		String data = DeviceUtil.getData(getContext(), LASTSHOW);
		//本地没有保存则返回
		if(data.equals("")){
			lastShowUserInfo.add(getSystemTime());
			return false;
		}
		lastShowUserInfo = (ArrayList<String>) JSON.parseArray(data, String.class);
		//如果时间更新，将所有列表更新
		if(!lastShowUserInfo.get(0).equals(getSystemTime())){
			lastShowUserInfo.clear();
			lastShowUserInfo.add(getSystemTime());
			return false;
		}
		for(int i=1;i<lastShowUserInfo.size();i++){
			//本地记录的今天已显示的用户是否是当前用户，如果是，则返回 
			if(lastShowUserInfo.get(i).equals(IlongSDK.getInstance().mUserInfo.getUid())) return true;
		}
		return false;
	}
	private void saveLastShow() {
		if(lastShowUserInfo == null) return;
		lastShowUserInfo.add(IlongSDK.getInstance().mUserInfo.getUid());
		DeviceUtil.saveData(getContext(), LASTSHOW, JSON.toJSONString(lastShowUserInfo));
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
	
	/**
	 * 获取系统时间
	 */
	public String getSystemTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
}
