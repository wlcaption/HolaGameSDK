package com.holagame.androidm.permission;

import android.content.Context;

/**
 * 一个抽象类需要去实现获取特殊权限的方法，和判断特殊权限是否获取的方法
 * @author 邹龙
 *
 */
public abstract class PermissionSpecialHandler {
	private String permissionName;
	private Context context;
	/**
	 * 特殊权限申请方法
	 */
	public abstract void HandlerPermission();
	/**
	 * 是否获取到权限方法
	 * @return 获取到权限返回true，否则返回false
	 */
	public abstract boolean hasPermission();
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	
	public PermissionSpecialHandler(String permissionName, Context context) {
		super();
		this.permissionName = permissionName;
		this.context = context;
	}
	
}
