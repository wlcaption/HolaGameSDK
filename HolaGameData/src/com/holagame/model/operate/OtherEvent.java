package com.holagame.model.operate;
/**
 * 对应数据统计的 other事件
 * */
public class OtherEvent {
	/**事件名*/
	private String event = "other";
	/**账户名*/
	private String accountId ;
	/**自定义事件名*/
	private String otherEvent ;
	/** 自定义事件名的json数据*/
	private String data ;
	/**时间戳*/
	private long ts;
	
	
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
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
	public String getOtherEvent() {
		return otherEvent;
	}
	public void setOtherEvent(String otherEvent) {
		this.otherEvent = otherEvent;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public OtherEvent() {
		super();
	}
	/**
	 * @param event 事件名
	 * @param accountId 账户id
	 * @param otherEvent 自定义事件名
	 * @param data 自定义事件名的json数据
	 * */
	public OtherEvent(String accountId, String otherEvent,
			String data, long ts) {
		super();
		this.accountId = accountId;
		this.otherEvent = otherEvent;
		this.data = data;
		this.ts = ts;
	}
	
	
}
