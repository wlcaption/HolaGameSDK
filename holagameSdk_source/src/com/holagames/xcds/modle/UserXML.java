package com.holagames.xcds.modle;

public class UserXML {
	public String userName = "";
	public String pwd = "";
	public String type = "";
	public UserXML(String userName, String pwd, String type){
		this.userName = userName;
		this.pwd = pwd;
		this.type = type;
	}
	public UserXML() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
