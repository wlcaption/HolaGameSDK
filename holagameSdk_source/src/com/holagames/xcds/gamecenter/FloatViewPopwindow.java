package com.holagames.xcds.gamecenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.ac.ActivityUser;
import com.holagames.xcds.tools.ResUtil;
import com.nineoldandroids.view.ViewHelper;
/**
 * 悬浮窗，不可再oncreat中调用。
 * @author lenovo
 *
 */
public class FloatViewPopwindow implements OnTouchListener{
    private final int DELAY_HIDE = 3000;
    private final int MESSAGE_WHAT = -1;//平常发送隐藏消息
	
	public PopupWindow floatView;
	private View mainView;
	private View rootView;//根视图
	public Button floatView_bt;
	private Activity activity;
	
	private int floatViewSize;
	
	private Handler handler;
	
	private int[] floatViewLocation = new int[2];//悬浮窗的位置
	private boolean isMove = false; 
	private FloatCloseViewPopwindow floatCloseView = null;
	private static FloatViewPopwindow floatViewPopwindow = null;
	public FloatViewPopwindow(Activity activity){
		this.activity = activity;
		InitView();
		InitData();
		
	}
	
	public static FloatViewPopwindow getInstance(Activity mainActivity){
		if(floatViewPopwindow == null){
			floatViewPopwindow = new FloatViewPopwindow(mainActivity);
		}
		return floatViewPopwindow;
	}
	
	private void InitView() {
		rootView = (activity.getWindow().getDecorView().findViewById(android.R.id.content));
		mainView = LayoutInflater.from(activity).inflate(ResUtil.getLayoutId(activity, "ilong_float_view_ly"), null);
		floatView_bt = (Button) mainView.findViewById(ResUtil.getId(activity, "float_imageview"));
		floatView_bt.setOnTouchListener(this);
	}
	
	private void InitData() {
		floatViewSize = activity.getResources().getDimensionPixelSize(ResUtil.getDimenId(activity, "float_circle_size"));
		initFloatView();
		initHanderMessage();
		hideFloatView();
		floatCloseView = new FloatCloseViewPopwindow(activity);
		floatView_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isMove) return;
				Intent intent = new Intent(activity, ActivityUser.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intent);
			}
		});
	}
	
	/**
	 * 初始化消息处理器
	 */
	private void initHanderMessage() {
		handler = new Handler(){@Override
		public void handleMessage(Message msg) {
			if(msg.what == MESSAGE_WHAT){
				absorbSide();
			}
		}};
	}
	
	/**
	 * 吸边
	 * 理解：popwindow会自动判断边界如果要向右吸附，值需要设置无限大，就会靠边。这样就不用重新测量屏幕
	 * 因为popweindow不能检测屏幕旋转的情况
	 */
	private void absorbSide() {
		getScreenInfo();
		floatView_bt.getLocationOnScreen(floatViewLocation);
		if((floatViewLocation[0]+floatViewSize/2)<IlongSDK.getInstance().screenInfo.widthPixels/2){
			//左边
			changeFloatShowHalf(true);
			updateLocation(0,floatViewLocation[1]);
		}else{
			//右边
			updateLocation(IlongSDK.getInstance().screenInfo.widthPixels, floatViewLocation[1]);
			changeFloatShowHalf(false);
		}
	}

	/**重新测量了屏幕*/
	public void  getScreenInfo() {
		WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics screenInfo=new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(screenInfo);
		IlongSDK.screenInfo=screenInfo;
	}
	
	/**
	 * 初始化悬浮窗
	 */
	private void initFloatView() {
		floatView = new PopupWindow(activity);
		floatView.setHeight(floatViewSize);
		floatView.setWidth(floatViewSize);
		floatView.setBackgroundDrawable(new ColorDrawable(new Color().TRANSPARENT));
		floatView.setOutsideTouchable(false);
		floatView.setContentView(mainView);
		floatView.setOutsideTouchable(false);
		floatView.showAtLocation(rootView, Gravity.TOP|Gravity.LEFT, 0,0);
		updateLocation(0,(IlongSDK.screenInfo.heightPixels-floatViewSize)/2);
	}

	private void updateLocation(int locationX,int locationY) {
		if(floatView != null){
			floatView.update(locationX, locationY, floatViewSize, floatViewSize);
		}
	}
	
	/**
	 * 发送消息队列吸边
	 */
	public void hideFloatView(){
		Message msg = new Message();
		msg.what = MESSAGE_WHAT;
		handler.sendMessageDelayed(msg, DELAY_HIDE);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int rawX = (int) event.getRawX();
		int rawY = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			changeFloatFull();//恢复全貌
			removeAllMessage();//清除所有延迟消息
			floatCloseView.show();
			floatCloseView.getFloatLocation();
			isMove = false;
			break;
		case MotionEvent.ACTION_MOVE:
			//移动事件处理，当移动位置超过悬浮窗尺寸的2/3时，方为移动悬浮窗，否则为点击事件
			if(Math.abs(event.getX())>floatViewSize/3*2||Math.abs(event.getY())>floatViewSize/3*2){
				isMove = true;
			}
			if(floatCloseView.isCenter(rawX, rawY, floatViewSize)){
				updateLocation(floatCloseView.getxCenter()-floatViewSize/2,floatCloseView.getyCenter()-floatViewSize/2);
			}else if(isMove){
				updateLocation(rawX-floatViewSize/2, rawY-floatViewSize/2);
			}
			break;
		case MotionEvent.ACTION_UP:
			hideFloatView();
			floatCloseView.close();
			if(floatCloseView.isCenter){
				floatView.dismiss();
			}
//			if(!isMove){
//				floatView_bt.performClick();
//			}
			break;
		}
		return false;
	}
	
	/**
	 * 清除所有延迟消息
	 */
	private void removeAllMessage() {
		// TODO Auto-generated method stub
		handler.removeMessages(MESSAGE_WHAT);
	}

	/**
	 * 显示一半悬浮窗
	 * @param isleft 是否靠左 true靠左
	 */
	public void changeFloatShowHalf(boolean isleft){
		ViewHelper.setAlpha(mainView, 0.5f);
		if (isleft) {
			ViewHelper.setTranslationX(mainView, - floatViewSize / 2);
		} else {
			ViewHelper.setTranslationX(mainView, floatViewSize / 2);
		}
	}
	
	/**
	 * 
	 */
	public void changeFloatFull(){
		ViewHelper.setAlpha(mainView, 1.0f);
		ViewHelper.setTranslationX(mainView, 0);
	}
	
	/**
	 * 屏幕发生改变
	 */
	public void onConfigurationChanged(){
		if(floatView != null && floatView.isShowing()){
			absorbSide();
		}
	}
	
	public void destroy(){
		floatView.dismiss();
		floatCloseView.close();
		floatViewPopwindow = null;
		floatCloseView = null;
	}
}
