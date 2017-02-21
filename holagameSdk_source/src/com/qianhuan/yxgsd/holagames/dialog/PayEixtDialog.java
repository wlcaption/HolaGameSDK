package com.qianhuan.yxgsd.holagames.dialog;

import com.qianhuan.yxgsd.holagames.tools.ResUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PayEixtDialog extends Dialog{

	private View mainView;
	private TextView message_context;
	private Button canel;
	private Button positive;
	public PayEixtDialog(Context context) {
		super(context,ResUtil.getStyleId(context, "security_dialog"));
		mainView = LayoutInflater.from(context).inflate(ResUtil.getLayoutId(context, "ilong_dialog_exit_pay"), null);
		setContentView(mainView);
		InitView();
	}
	private void InitView() {
		// TODO Auto-generated method stub
		message_context = (TextView) mainView.findViewById(ResUtil.getId(getContext(), "ilong_dialog_base_message"));
		canel = (Button) mainView.findViewById(ResUtil.getId(getContext(), "ilong_dialog_base_canel"));
		positive = (Button) mainView.findViewById(ResUtil.getId(getContext(), "ilong_dialog_base_positive"));
	}
	
	public void setCanelText(String text){
		canel.setText(text);
	}
	
	public void setPositive(String text){
		positive.setText(text);
	}
	public void setMessage(String message){
		message_context.setText(message);
	}
	
	public void setCanel(View.OnClickListener listener){
		canel.setOnClickListener(listener);
	}
	
	public void setPositiveListener(View.OnClickListener listener){
		positive.setOnClickListener(listener);
	}

}
