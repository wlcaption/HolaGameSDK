package com.holagames.xcds.dialog;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.holagame.util.DeviceUtil;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.tools.ResUtil;
import com.holagames.xcds.tools.ToastUtils;
import com.holagames.xcds.tools.http.Constant;
import com.holagames.xcds.tools.http.HttpUtil;
import com.holagames.xcds.tools.http.NetException;
import com.holagames.xcds.tools.http.SdkJsonReqHandler;
/**
 * 实名认证dialog
 * @author zoulong
 *
 */
public class UserCardDialog extends Dialog{

	private View mainView;//主视图
	private EditText card_name_et;//用户名输入框
	private EditText card_id_et;//身份证输入框
	private Button authentication_bt;//确认实名按钮
	private VerifyUserID verify_inf=null;//实名状态回调接口
	private ImageView close_bt;//关闭按钮，默认不显示与isCanecl配合使用
	private boolean isCancel = false;//是否可取消
	private boolean status = true;//实名状态
	public UserCardDialog(Context context) {
		super(context, ResUtil.getStyleId(context, "security_dialog"));
		mainView = LayoutInflater.from(context).inflate(ResUtil.getLayoutId(context, "ilong_dialog_user_id_card"), null);
		InitView();
		setContentView(mainView);
		InitData();
	}
	
	public static interface VerifyUserID{
		public void onSuccess();
		public void onFailed();
	}
	
	private void InitData() {
		mainView.bringToFront();
		authentication_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userName = card_name_et.getText().toString().trim();
				String cardNumber = card_id_et.getText().toString().trim();
				if(!DeviceUtil.isValidateAllUserName(userName)){
					ToastUtils.show(getContext(), "姓名格式不合法");
				}else if(!DeviceUtil.isValidatedAllIdcard(cardNumber)){
					ToastUtils.show(getContext(), "身份证格式不合法");
				}else{
					bindUserCard(userName,cardNumber);
				}
			}
		});
		
		close_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	private void InitView() {
		authentication_bt = (Button) mainView.findViewById(ResUtil.getId(getContext(), "user_id_card_authentication_bt"));
		card_id_et = (EditText) mainView.findViewById(ResUtil.getId(getContext(), "user_id_card_number"));
		card_name_et = (EditText) mainView.findViewById(ResUtil.getId(getContext(), "user_id_card_username"));
		close_bt = (ImageView) mainView.findViewById(ResUtil.getId(getContext(), "ilong_dialog_user_id_card_close"));
	}
	
	public void isCancel(boolean isCancel){
		if(isCancel){
			isCancel = true;
			close_bt.setVisibility(View.VISIBLE);
		}
	}
	
	public void bindUserCard(String userName,String userCardId){
		String url = Constant.httpHost +Constant.BIND_USER_CARD;
		Map<String, Object> params = new HashMap<String, Object>(0);
		params.put("realname", userName);
		params.put("idcard", userCardId);
		params.put("access_token", IlongSDK.getInstance().mToken);
		HttpUtil.newHttpsIntance(getContext()).httpsPostJSON(getContext(), url, params, new SdkJsonReqHandler(params) {
			
			@Override
			public void ReqYes(Object reqObject, String content) {
				try {
					System.out.println("content:"+content);
					JSONObject obj = new JSONObject(content);
					int code = obj.getInt("errno");
					if(code == 200){
						ToastUtils.show(getContext(), "认证成功");
						IlongSDK.mUserInfo.setVerify("1");
						dismiss();
					}else if(code == 102){
						ToastUtils.show(getContext(), "您的账号已在其它设备实名");
						IlongSDK.mUserInfo.setVerify("1");
						dismiss();
					}else{
						ToastUtils.show(getContext(),"您输入格式不合法");
					}
					status = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status = false;
				}
			}
			
			@Override
			public void ReqNo(Object reqObject, NetException slException) {
				ToastUtils.show(getContext(), "认证失败");
				status = false;
			}
		});
	}
	public void setVerify_inf(VerifyUserID verify_inf) {
		this.verify_inf = verify_inf;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		close_bt.setVisibility(isCancel?View.VISIBLE:View.GONE);
		super.show();
	}
    @Override
    public void setCancelable(boolean flag) {
    	// TODO Auto-generated method stub
    	isCancel = flag;
    	super.setCancelable(flag);
    }
    
    @Override
    public void dismiss() {
    	// TODO Auto-generated method stub
    	if(status){
    		verify_inf.onSuccess();
    	}else{
    		verify_inf.onFailed();
    	}
    	super.dismiss();
    }
}
