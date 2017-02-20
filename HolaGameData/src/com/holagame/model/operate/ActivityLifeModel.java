package com.holagame.model.operate;

public class ActivityLifeModel {
	/**activityLife*/
	private String  event = "activityLife";
	/**账户ID, 有些用户没有登录，所以没有ID*/
	private String accountId ;
	/**页面名*/
	private String page ;
	/**页面动作, 包括创建、暂停、恢复、注销*/
	private String action ;
	
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
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public ActivityLifeModel() {
		super();
	}
	
	public ActivityLifeModel(String accountId, String page,
			String action,long ts) {
		super();
		this.accountId = accountId;
		this.page = page;
		this.action = action;
		this.ts = ts;
	}
	
}
