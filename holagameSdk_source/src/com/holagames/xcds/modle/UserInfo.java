package com.holagames.xcds.modle;

import java.io.Serializable;

/**
 * 用户信息
 * //"id":"749505","name":"cool6300","phone":"","isPhoneBind":"0","money":"1"
 * ,"score":"0"
 * 
 */
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name = "";
	private String phone = "";
	private String isPhoneBind = "";
	private String money = "";
	private String score = "";
	/**id
	 * 提供给游戏方用的id值
	 * */
	private String id = "";
	private String level = "0";
	/**uid 是一个短的数字字符串，用于显示给玩家看，游客是没有uid*/
	private String uid="0";
	/**火拉币支付密码的判断值，0 没有支付密码，1有支付密码*/
	private String pay_password = "";
	private String verify = "1";//是否实名默认是实名
	public UserInfo() {
		super();
	}

	public String getPay_password() {
		return pay_password;
	}

	public void setPay_password(String pay_password) {
		this.pay_password = pay_password;
	}

	


	@Override
	public String toString() {
		return "UserInfo [name=" + name + ", phone=" + phone + ", isPhoneBind="
				+ isPhoneBind + ", money=" + money + ", score=" + score
				+ ", id=" + id + ", level=" + level + ", uid=" + uid
				+ ", pay_password=" + pay_password + "]";
	}

	public UserInfo(String name, String phone, String isPhoneBind,
			String money, String score, String id, String level, String uid) {
		super();
		this.name = name;
		this.phone = phone;
		this.isPhoneBind = isPhoneBind;
		this.money = money;
		this.score = score;
		this.id = id;
		this.level = level;
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIsPhoneBind() {
		return isPhoneBind;
	}

	public void setIsPhoneBind(String isPhoneBind) {
		this.isPhoneBind = isPhoneBind;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}
}
