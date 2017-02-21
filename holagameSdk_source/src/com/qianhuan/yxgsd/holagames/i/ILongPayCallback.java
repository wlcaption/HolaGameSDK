package com.qianhuan.yxgsd.holagames.i;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.holagame.tool.Gamer;
import com.qianhuan.yxgsd.holagames.IlongSDK;
import com.qianhuan.yxgsd.holagames.ac.ActivityUpdateAccount;
import com.qianhuan.yxgsd.holagames.tools.ResUtil;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;

public abstract class ILongPayCallback{
	
	/**sdk支付成功，还需要询问是否绑定手机*/
	public void onSuccess4Bind(){
		
		if(IlongSDK.TYPE_USER.equals(Constant.TYPE_USER_NORMAL)){
			onSuccess();
		}else{
			showBindPhone();
		}
	}
	
	public Dialog dialogBind;
	
	/**显示绑定手机界面*/
	public void showBindPhone(){
		Activity activity = IlongSDK.getActivity();
		dialogBind = new Dialog(activity, ResUtil.getStyleId(activity,"ilongyuanAppUpdataCanCancle"));
		View view = activity.getLayoutInflater().inflate(ResUtil.getLayoutId(activity,"ilong_dialog_update_user_start"), null);
		View CancleBtn = view.findViewById(ResUtil.getId(activity, "ilong_bind_cancel"));
		View OKUpdataBtn = view.findViewById(ResUtil.getId(activity, "ilong_bind_go"));
		View cancel = view.findViewById(ResUtil.getId(activity, "ilong_bind_close"));
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, "updata_user_start_close)");
				if(dialogBind != null && dialogBind.isShowing()){
					dialogBind.cancel();
					IlongSDK.getInstance().callbackPay.onSuccess();
				}
			}
		});
		CancleBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, "updata_user_start_cancle");
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
				Gamer.sdkCenter.ButtonClick(IlongSDK.AccountId, "updata_user_start_ok");
				if(dialogBind != null && dialogBind.isShowing()){
					dialogBind.cancel();
				}
				Intent it = new Intent(IlongSDK.getActivity(), ActivityUpdateAccount.class);
				it.putExtra(Constant.TYPE_FROM_PAY, true);
				IlongSDK.getActivity().startActivity(it);
			}
		});

		dialogBind.setCancelable(false);
		dialogBind.setCanceledOnTouchOutside(false);
		dialogBind.setContentView(view);
		dialogBind.show();
	}
	
	/**支付总的回调，支付成功*/
	public abstract void onSuccess();
	
	public abstract void onFailed();
	
	public abstract void onCancel();
	
	
}
