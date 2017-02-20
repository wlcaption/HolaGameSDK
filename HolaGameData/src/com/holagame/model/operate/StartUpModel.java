package com.holagame.model.operate;

public class StartUpModel {

	private String event = "startup";
	
	private long ts ;

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
	
	public StartUpModel() {
		super();
	}

	public StartUpModel(long ts) {
		super();
		this.ts = ts;
	}
	
	
	
	
}
