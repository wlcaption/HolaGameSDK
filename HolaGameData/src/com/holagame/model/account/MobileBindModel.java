package com.holagame.model.account;
/**
 * 绑定手机 事件模型
 *对应 数据采集事件中的  手机绑定 mobileBind
 */
public class MobileBindModel {
	//事件
	private String event = "mobileBind";
	//账户id
	private String accountId ;
	//手机号
	private String mobile ;
	//时间戳
	private long ts ;
	//是否绑定成功
	private boolean isok ;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public boolean isIsok() {
		return isok;
	}
	public void setIsok(boolean isok) {
		this.isok = isok;
	}
	public MobileBindModel() {
		super();
	}
	public MobileBindModel(String accountId, String mobile,
			long ts, boolean isok) {
		super();
		this.accountId = accountId;
		this.mobile = mobile;
		this.ts = ts;
		this.isok = isok;
	}
	
	
	
	
}
