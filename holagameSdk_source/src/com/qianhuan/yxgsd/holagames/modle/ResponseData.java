package com.qianhuan.yxgsd.holagames.modle;

public class ResponseData {
private String code;
private String openId;
public ResponseData() {
	super();
}
public ResponseData(String code, String openId) {
	super();
	this.code = code;
	this.openId = openId;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getOpenId() {
	return openId;
}
public void setOpenId(String openId) {
	this.openId = openId;
}

}
