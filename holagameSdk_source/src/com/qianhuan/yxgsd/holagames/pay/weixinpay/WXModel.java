package com.qianhuan.yxgsd.holagames.pay.weixinpay;

import org.json.JSONException;
import org.json.JSONObject;

import com.holagame.util.Logd;


public class WXModel {
	private String appId;
	private String partnerId;
	private String prepayId;
	private String packageValue;
	private String nonceStr;
	private String timeStamp;
	private String sign;
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "WXModel [appId=" + appId + ", partnerId=" + partnerId
				+ ", prepayId=" + prepayId + ", packageValue=" + packageValue
				+ ", nonceStr=" + nonceStr + ", timeStamp=" + timeStamp
				+ ", sign=" + sign + "]";
	}
	
	public static WXModel jsonData(String str){
		WXModel entry = new WXModel();
		try {
			JSONObject jsonObject = new JSONObject(str);
			if(jsonObject.optString("appid") != null){
				entry.setAppId(jsonObject.getString("appid"));
			}
			if(jsonObject.optString("timestamp") != null){
				entry.setTimeStamp(jsonObject.getString("timestamp"));
			}
			if(jsonObject.optString("package") != null){
				entry.setPackageValue(jsonObject.getString("package"));
			}
			if(jsonObject.optString("partnerid") != null){
				entry.setPartnerId(jsonObject.getString("partnerid"));
			}
			if(jsonObject.optString("noncestr") != null){
				entry.setNonceStr(jsonObject.getString("noncestr"));
			}
			if(jsonObject.optString("prepayid") != null){
				entry.setPrepayId(jsonObject.getString("prepayid"));
			}
			if(jsonObject.optString("sign") != null){
				entry.setSign(jsonObject.getString("sign"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logd.e("WXEnter", "entry" + entry.toString());
		return entry;
	}
	
	
}
