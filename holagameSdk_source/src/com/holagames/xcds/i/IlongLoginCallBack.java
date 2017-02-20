package com.holagames.xcds.i;

public interface IlongLoginCallBack {

	/**
	 * 回调接口
	 * @param code
	 * @param msg
	 */
	public void onSuccess(String auth_code);
	public void onFailed(String msg);
	public void onLogout();
	public void onCancel();
	public void onSwitchAccount(String auth_code);
}


