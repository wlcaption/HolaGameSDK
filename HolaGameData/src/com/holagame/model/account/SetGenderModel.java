package com.holagame.model.account;

/**
 * 设置玩家性别
 * 对应数据采集 setGender 事件
 * 
 */
public class SetGenderModel {
	private String event = "setGender";
	//账户id
	private String accountId;
	//性别 (0未知 1男性 2女性)
	private int gender ;
	//时间戳
	private long ts ;
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
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public SetGenderModel() {
		super();
	}
	public SetGenderModel(String accountId, int gender, long ts) {
		super();
		this.accountId = accountId;
		this.gender = gender;
		this.ts = ts;
	}
	
	
	
}
