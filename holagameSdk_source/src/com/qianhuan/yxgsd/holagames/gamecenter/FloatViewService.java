package com.qianhuan.yxgsd.holagames.gamecenter;

import com.qianhuan.yxgsd.holagames.IlongSDK;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

public class FloatViewService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		IlongSDK.getInstance().onConfigurationChanged();
	}

}
