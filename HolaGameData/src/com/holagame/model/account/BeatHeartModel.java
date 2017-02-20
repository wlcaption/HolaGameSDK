package com.holagame.model.account;


/**
 * 对应 数据采集的 BeatHeart登录事件
 *
 */
public class BeatHeartModel {
	/**账户id*/
	private String accountId ;
	
	private String event = "heartbeat";
	
	private long ts ;
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public BeatHeartModel() {
		super();
	}

	public BeatHeartModel(String accountId, long ts) {
		super();
		this.accountId = accountId;
		this.ts = ts;
	}

	@Override
	public String toString() {
		return "LoginModel [accountId=" + accountId + ", event=" + event
				+ ", ts=" + ts + "]";
	}
	
	
	
	
}
