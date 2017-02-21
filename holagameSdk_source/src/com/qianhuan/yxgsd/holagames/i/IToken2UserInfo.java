package com.qianhuan.yxgsd.holagames.i;

import com.qianhuan.yxgsd.holagames.modle.UserInfo;

/**
 * 使用token来获取用户信息
 * @author niexiaoqiang
 */
public interface IToken2UserInfo {

	public void onSuccess(UserInfo userInfo);
	public void onFailed();
}
