package com.holagame.model.account;
/**
 * 设置玩家年龄模型
 * 对应 数据采集 中 setAge，设置玩家年龄
 *
 */
public class SetAgeModel {
	private String event = "setAge";
	private String accountId ;
	/** 年龄 */
	private int age;
	/** 时间戳 */
	private long ts;
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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	
	public SetAgeModel() {
		super();
	}
	public SetAgeModel(String accountId, int age, long ts) {
		super();
		this.accountId = accountId;
		this.age = age;
		this.ts = ts;
	}
	
}
