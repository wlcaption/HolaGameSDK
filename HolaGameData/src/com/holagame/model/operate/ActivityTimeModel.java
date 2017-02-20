package com.holagame.model.operate;

public class ActivityTimeModel {
	//事件名
	private String event = "activityTime";
	//accountId 用户名
	private String accountId ;
	//time 停留时间
	private int time ;
	//acivity 名
	private String name ;
	
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
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public ActivityTimeModel() {
		super();
	}
	
	public ActivityTimeModel(String accountId, int time,
			String name,long ts) {
		super();
		this.accountId = accountId;
		this.time = time;
		this.name = name;
		this.ts = ts ;
	}
	
}
