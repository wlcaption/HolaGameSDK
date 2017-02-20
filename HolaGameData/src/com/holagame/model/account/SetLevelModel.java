package com.holagame.model.account;

/**
 * 设置玩家等级事件
 * 对应 数据采集中的 setLevel
 * 
 */
public class SetLevelModel {
	//事件名
	private String event = "setLevel";
	//账户id
	private String accountId ;
	//等级
	private int level ;
	//ts 
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public SetLevelModel() {
		super();
	}
	public SetLevelModel(String accountId, int level, long ts) {
		super();
		this.accountId = accountId;
		this.level = level;
		this.ts = ts;
	}
	
	
	
	
}
