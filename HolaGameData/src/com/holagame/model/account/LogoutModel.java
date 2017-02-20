package com.holagame.model.account;


/**
 * 注销 模型
 * 
 * 对应 数据采集中的 登出，游戏注销当前账号事件
 * 
 */
public class LogoutModel {
	
	private String event = "logout";
	
	private String accountId ;
	
	
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

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public LogoutModel() {
		super();
	}

	public LogoutModel(String accountId, long ts) {
		super();
		this.accountId = accountId;
		this.ts = ts;
	}
	
	
	
}
