package com.holagames.xcds.ac;

import com.holagame.constant.ActivityLifeTypeConstant;
import com.holagame.tool.Gamer;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.tools.ResUtil;

import android.app.Activity;
import android.os.Bundle;


public abstract class BaseActivity extends Activity{
	

	/**页面onResume时间*/
	public long onResumeTs = 0l ;
	/**页面 onPause 的时间*/
	public long onPauseTs = 0l ; 
	
	public abstract String getActivityName();
	
	/**Activity 显示在前端的的生周时间*/
	public long activitytime  = 0l;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(IlongSDK.ISLONG){
			setTheme(ResUtil.getStyleId(this, "Ilong_Theme"));
		}else{
			setTheme(ResUtil.getStyleId(this, "HR_Theme"));
		}
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId, getActivityName(),ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONCREATE);	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onResumeTs = System.currentTimeMillis();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId, getActivityName(),ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONRESUME);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		onPauseTs = System.currentTimeMillis();
		getActivityLifeTime();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId, getActivityName(),ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONPAUSE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Gamer.sdkCenter.ActivityLife(IlongSDK.AccountId, getActivityName(),ActivityLifeTypeConstant.TYPE_ACTIVITY_LIFE_ONDESTROY );
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Gamer.sdkCenter.ActivityUserRunningTime(IlongSDK.AccountId, getActivityLifeTime(), getActivityName());
		
	}
	
	/**得到一个Activity的运行时间段,返回的int值，值得多少约等于 多少秒*/
	public int getActivityLifeTime(){
		activitytime += onPauseTs - onResumeTs;
		if(activitytime != 0l){
			activitytime = (int)activitytime / 1000;
			return (int) activitytime;
		}else{
			return 0 ;
		}
		
	}
	
	
}
