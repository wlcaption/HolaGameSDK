package com.holagames.xcds.dialog;

import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.enums.PayResultType;
import com.holagames.xcds.tools.ResUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PayResultDialog extends Dialog {
	private PayResultDialog payResult;
	private Context context;
	private View mainView;
    private PayResultType type = PayResultType.FAIL_PAY;
    private PayResultType pay_state = PayResultType.FAIL_PAY;
    private TextView pay_order_info;
    private TextView pay_order_info_fail;
    private Button result_bt;
    
    private View pay_result_success;
    private View pay_result_fail_ly;
    private View pay_result_fial_pay;
    
    private boolean isBack = false;
	public PayResultDialog(Context context,PayResultType type) {
		super(context, ResUtil.getStyleId(context, "security_dialog"));
		this.context = context;
		this.type = type;
		mainView = LayoutInflater.from(context).inflate(ResUtil.getLayoutId(context, "ilong_pay_result"), null);
		setContentView(mainView);
		setCanceledOnTouchOutside(false);
		InitView();
	}
	
	/**
	 * 初始化视图
	 */
	private void InitView() {
		pay_order_info = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_order_info"));
		pay_order_info_fail = (TextView) mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_order_info_fail"));
		result_bt = (Button) mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_bt"));
		pay_result_fail_ly = mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_fail_ly"));
		pay_result_fial_pay = mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_fail_pay"));
		pay_result_success = mainView.findViewById(ResUtil.getId(context, "ilong_pay_result_success"));
		
		result_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(PayResultType.SUCCESS == pay_state){
					IlongSDK.getInstance().callbackPay.onSuccess4Bind();
				}else if(PayResultType.FAIL_PAY == pay_state){
					IlongSDK.getInstance().callbackPay.onFailed();
				}else if(PayResultType.CANEL == pay_state){
					IlongSDK.getInstance().callbackPay.onCancel();
				}
				isBack = true;
				dismiss();
				((Activity)context).finish();
			}
		});
	}
	
	

	/**
	 * 设置火拉币的余额
	 * @param lyCoin
	 */
	public void setLyCoin(int lyCoin){
		pay_order_info.setText("");
		pay_order_info_fail.setText("");
	}
	
	public void setResultType(PayResultType type){
		this.type = type;
	}
	
	private void InitData() {
		pay_result_success.setVisibility(View.GONE);
		pay_result_fail_ly.setVisibility(View.GONE);
		pay_result_fial_pay.setVisibility(View.GONE);
		if(type == PayResultType.SUCCESS){//成功
			pay_result_success.setVisibility(View.VISIBLE);
		}else if(type == PayResultType.FAIL_PAY){//支付失败
			pay_result_fial_pay.setVisibility(View.VISIBLE);
		}else if(type == PayResultType.FAIL_LY){//充值成功，消费失败
			pay_result_fail_ly.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void show() {
		InitData();
		super.show();
	}
	
	public void setPayState(PayResultType type){
		this.pay_state = type;
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if(!isBack) return;
		isBack = false;
		super.dismiss();
	}
	
}
