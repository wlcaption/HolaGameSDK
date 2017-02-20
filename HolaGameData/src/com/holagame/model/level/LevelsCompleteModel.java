package com.holagame.model.level;

public class LevelsCompleteModel {
	
	private String event = "levelsComplete";
	
	private String accountId ;
	
	private String levelId;
	
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

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public LevelsCompleteModel() {
		super();
	}

	public LevelsCompleteModel(String accountId, String levelId,
			long ts) {
		super();
	
		this.accountId = accountId;
		this.levelId = levelId;
		this.ts = ts;
	}
	
	
}
