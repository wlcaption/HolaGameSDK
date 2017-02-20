package com.holagame.tool;

import android.content.Context;
import android.util.Log;

import com.holagame.global.Constant;
import com.holagame.tool.HttpHelper.MHttp;

public class GameInit {
	public static final String Tag = "GameInit";
	private static GameInit instance;
	
	public static GameInit getInstance(){
		if(instance == null){
			instance = new GameInit();
		}
		return instance;
	}
	
	public static void getConfig(Context context, final MHttp mHttp){
		String url = Constant.host + Constant.URL_CONFIG;
		String data = "";
		Log.e(Tag, url);

		HttpHelper.doGet(url, mHttp);
	}
	
	
	
	
}
