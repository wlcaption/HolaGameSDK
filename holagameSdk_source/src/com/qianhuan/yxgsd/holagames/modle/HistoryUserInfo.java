package com.qianhuan.yxgsd.holagames.modle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.holagame.util.DeviceUtil;
import com.qianhuan.yxgsd.holagames.tools.http.Constant;

/**
 * 历史用户信息列表
 * @author zoulong
 *
 */
public class HistoryUserInfo {
	private String username = "";
	private String type = "";
	private String userLoginInfo = "";
	private String userPassword = "";
	private long datetime;
	private String pid="";
	public HistoryUserInfo(String username, String type, String userLoginInfo,
			String userPassword, long datetime, String pid) {
		super();
		this.username = username;
		this.type = type;
		this.userLoginInfo = userLoginInfo;
		this.userPassword = userPassword;
		this.datetime = datetime;
		this.pid = pid;
	}
	
	@Override
	public String toString() {
		return "username=" + username + "\n";
	}
	public HistoryUserInfo() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = (type.equals("0")?Constant.TYPE_USER_NORMAL:Constant.TYPE_USER_NOT_REGISTER);
	}

	public String getUserLoginInfo() {
		return userLoginInfo;
	}

	public void setUserLoginInfo(String userLoginInfo) {
		this.userLoginInfo = userLoginInfo;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = new Long(datetime);
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * 将用户信息转化成一个map,这个map是用来做为登录
	 * @return
	 */
	public Map<String, String> toMap(){
		Map<String, String> userinfo = new HashMap<String, String>();
		if(!this.type.equals(Constant.TYPE_USER_NORMAL)){
			userinfo.put(Constant.KEY_DATA_TYPE, Constant.TYPE_USER_NOT_REGISTER);
			return userinfo;
		}
		userinfo.put(Constant.KEY_DATA_TYPE, this.type);
		userinfo.put(Constant.KEY_DATA_USERNAME, this.username);
		userinfo.put(Constant.KEY_DATA_CONTENT,this.userLoginInfo);
		return userinfo;
		
	}
	
	/**
	 * 将用户信息转化成一个map,这个map是用来做为删除服务器上的历史消息
	 * @return
	 */
	public Map<String, Object> toMap(Context context){
		Map<String, Object> userinfo = new HashMap<String, Object>();
		if(!this.type.equals(Constant.TYPE_USER_NORMAL)){
			userinfo.put("type", "1");
			userinfo.put("pid",DeviceUtil.getUniqueCode((Activity)context));
		}else {
			userinfo.put("type", "0");
		}
		userinfo.put("mac", DeviceUtil.getIMEI(context));
		userinfo.put("username",this.username);
		return userinfo;
		
	}
	
	/**
	 * 将时间戳转化成时间
	 * @param 时间戳
	 * @return 时间格式的字符串
	 */
	public String translateTime() {
		SimpleDateFormat timeformat= new SimpleDateFormat("yyyy/M/d  H:m:s");
		Date data = new Date(new Long(this.datetime));
		return timeformat.format(data);
	}
}
