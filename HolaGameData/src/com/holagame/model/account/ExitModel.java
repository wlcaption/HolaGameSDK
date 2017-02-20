package com.holagame.model.account;


/*
 * 退出游戏模型
 * 对应 数据采集中的 退出exit
 */
public class ExitModel {
	
	private String event = "exit";
	
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

	public ExitModel() {
		super();
	}

	public ExitModel(String accountId, long ts) {
		super();
		this.accountId = accountId;
		this.ts = ts;
	}
	
	
	
	
}
