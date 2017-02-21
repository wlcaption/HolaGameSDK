package com.qianhuan.yxgsd.holagames.dialog;

import com.qianhuan.yxgsd.holagames.tools.ResUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * dialog ,可以获取到 的文本有title，content，左右按钮文本
 * 以及 右上角关闭按钮，dialog左右2按钮
 * @author gaohanqing
 *
 */


public class IlongBasicDialog extends Dialog {

	public IlongBasicDialog(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public  IlongBasicDialog(Context context , int theme){
		super(context, theme);
		this.context = context;
	}
	
	private TextView dialogtitletext;
	private TextView dialogcontent;
	private String leftBtnText;
	private String rightBtnText;
	private Context context;
	private Button dialogleftBtn;
	private Button dialogrightBtn ;
	private ImageView dialogCloseBtn;
	
	

	public TextView getDialogtitletext() {
		return dialogtitletext;
	}

	public void setDialogtitletext(TextView dialogtitletext) {
		this.dialogtitletext = dialogtitletext;
	}

	public TextView getDialogcontent() {
		return dialogcontent;
	}

	public void setDialogcontent(TextView dialogcontent) {
		this.dialogcontent = dialogcontent;
	}

	public String getLeftBtnText() {
		return leftBtnText;
	}

	public void setLeftBtnText(String leftBtnText) {
		this.leftBtnText = leftBtnText;
	}

	public String getRightBtnText() {
		return rightBtnText;
	}

	public void setRightBtnText(String rightBtnText) {
		this.rightBtnText = rightBtnText;
	}
	
	
	
	public Button getDialogleftBtn() {
		return dialogleftBtn;
	}

	public void setDialogleftBtn(Button dialogleftBtn) {
		this.dialogleftBtn = dialogleftBtn;
	}

	public Button getDialogrightBtn() {
		return dialogrightBtn;
	}

	public void setDialogrightBtn(Button dialogrightBtn) {
		this.dialogrightBtn = dialogrightBtn;
	}

	public ImageView getDialogCloseBtn() {
		return dialogCloseBtn;
	}

	public void setDialogCloseBtn(ImageView dialogCloseBtn) {
		this.dialogCloseBtn = dialogCloseBtn;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(context, "ilong_dialog_basic"));
		dialogtitletext = 	(TextView) findViewById(ResUtil.getId(context, "ilong_mydialog_title"));
		dialogcontent = (TextView) findViewById(ResUtil.getId(context, "ilong_mydialog_content"));
		dialogleftBtn =	(Button) findViewById(ResUtil.getId(context, "ilong_mydialog_left_btn"));
		dialogrightBtn = (Button) findViewById(ResUtil.getId(context, "ilong_mydialog_right_btn"));
		dialogCloseBtn =  (ImageView) findViewById(ResUtil.getId(context, "ilong_mydialog_close"));
		
	}
	
	
	
	
}
