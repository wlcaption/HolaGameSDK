package com.holagame.model.operate;

public class SDKButtonClickModel {
	/**账户 id*/
	public String accountId ;
	/**按钮名*/
	public String button ;
	public String event = "button";
	public long ts ;
	
	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public String getAccountId() {
		return accountId;
	}
	
	public String getButton() {
		return button;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setButton(String button) {
		this.button = button;
	}

	public SDKButtonClickModel(String accountId, String button,long ts) {
		super();
		this.accountId = accountId;
		this.button = button;
		this.ts = ts;
	}
	public SDKButtonClickModel(){
		super();
	}

	@Override
	public String toString() {
		return "SDKButtonClickModel [accountId=" + accountId + ", button="
				+ button + ", event=" + event + ", ts=" + ts + "]";
	}


	
	
	
	
}
