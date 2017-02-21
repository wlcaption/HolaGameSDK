package com.qianhuan.yxgsd.holagames.gamecenter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.qianhuan.yxgsd.holagames.tools.ResUtil;

public class FloatCloseViewPopwindow{
	public PopupWindow floatColseView;
	private View mainView;
	private View rootView;//根视图
	private Activity activity;
	private ImageView close_iv;
	private int closeFloatSize;
	private GradientDrawable hiddenImageDrawable;
	
	private int xCenter;
	private int yCenter;
	
	public static boolean isCenter = false;
	
	public FloatCloseViewPopwindow(Activity activity) {
		this.activity = activity;
		InitView();
		InitData();
	}
	private void InitData() {
		closeFloatSize = activity.getResources().getDimensionPixelSize(ResUtil.getDimenId(activity, "float_close_circle_size"));
		hiddenImageDrawable = new GradientDrawable();
		setCloseFloatBackground(0.3f);
		InitFloatColseView();
		getFloatLocation();
	}
	private void InitFloatColseView() {
		int floatCloseWide = activity.getResources().getDimensionPixelSize(ResUtil.getDimenId(activity, "float_close_circle_ly_w"));
		int floatCloseHight = activity.getResources().getDimensionPixelSize(ResUtil.getDimenId(activity, "float_close_circle_ly_h"));
		floatColseView = new PopupWindow(activity);
		floatColseView.setContentView(mainView);
		floatColseView.setOutsideTouchable(false);
		floatColseView.setContentView(mainView);
		floatColseView.setWidth(floatCloseWide);
		floatColseView.setHeight(floatCloseHight);
		floatColseView.setBackgroundDrawable(new ColorDrawable(new Color().TRANSPARENT));
		
	}
	private void InitView() {
		mainView = LayoutInflater.from(activity).inflate(ResUtil.getLayoutId(activity, "hidden_tipview"), null);
		rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		close_iv = (ImageView) mainView.findViewById(ResUtil.getId(activity, "hidden_image"));
	}
	
	public boolean isCenter(int floatX,int floatY,int floatW){
		if(xCenter<=0 || yCenter<=0) return isCenter;
		int distance = (int) Math.sqrt(Math.pow(Math.abs(floatX - xCenter), 2) + Math.pow(Math.abs(floatY - yCenter),2));
		isCenter = distance<(floatW/2+closeFloatSize/2);
		if(isCenter){
			setCloseFloatBackground(0.5f);
		}else{
			setCloseFloatBackground(0.3f);
		}
		return isCenter;
	}
	
	public void show(){
		floatColseView.showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	public void close(){
		floatColseView.dismiss();
	}
	
	private void setCloseFloatBackground(float alpha) {
		hiddenImageDrawable.setShape(GradientDrawable.OVAL);
		hiddenImageDrawable.setStroke(3, Color.argb(0XFF, 0X7A, 0X7A, 0X7A));
		hiddenImageDrawable.setColor(Color.argb((int) (255 * alpha), 0X7A, 0X7A, 0X7A));
		close_iv.setBackgroundDrawable(hiddenImageDrawable);
	}
	
	public void getFloatLocation(){
		ViewTreeObserver vto = close_iv.getViewTreeObserver(); 
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
		    public boolean onPreDraw() { 
		    	int[] location =new int[2];
		    	close_iv.getLocationOnScreen(location);
			    FloatCloseViewPopwindow.this.xCenter = location[0]+closeFloatSize/2;
			    FloatCloseViewPopwindow.this.yCenter = location[1]+closeFloatSize/2;
		        return true; 
		    } 
		}); 
		
	}
	
	public int getxCenter() {
		return xCenter;
	}

	public int getyCenter() {
		return yCenter;
	}
	
	
}
