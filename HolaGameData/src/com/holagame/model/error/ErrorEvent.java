package com.holagame.model.error;

public class ErrorEvent {
	/**账户id*/
	private String accountId ;
	/**错误信息msg*/
	private String msg ;
	/**错误信息event*/
	private String event = "error";
	
	public long ts ;

	
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
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
	public ErrorEvent() {
		super();
	}
	public ErrorEvent(String accountId, String msg, long ts) {
		super();
		this.accountId = accountId;
		this.msg = msg;
		this.ts = ts;
	}
	@Override
	public String toString() {
		return "ErrorEvent [accountId=" + accountId + ", msg=" + msg
				+ ", event=" + event + ", ts=" + ts + "]";
	}

	
	
	
	
}
