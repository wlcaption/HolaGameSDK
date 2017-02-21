package com.qianhuan.yxgsd.holagames.i;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * SDK接口
 * @author niexiaoqiang
 */
public interface IlongGame {
	/**
	 * sdk初始化接口
	 * @param a
	 * @param ilongCallBack
	 */
	public void init(Activity a, ILongInitCallback ic, IlongLoginCallBack lc, ILongPayCallback pc, ILongExitCallback ec);

	/**
	 * 登录接口
	 * @param ilongCallBack
	 */
	public void login();

	public void logout();

	public void showFloatView();

	public void hideFloatView();

	public void exitSDK();

	public void pay(Bundle bundle);
	
	public void showUserCenter();
}
