package com.holagame.model.level;
/*
 * 游戏关卡 开始
 * 对应数据采集事件 中的 levelsBegin
 *
 */
public class LevelsBegin {
	//事件名
	private String event = "levelsBegin";
	//账户id
	private String accountId ;
	//关卡 id
	private String levelId;
	//时间戳
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
	public LevelsBegin() {
		super();
	}
	public LevelsBegin(String accountId, String levelId, long ts) {
		super();
		this.accountId = accountId;
		this.levelId = levelId;
		this.ts = ts;
	}
	
	
	
	
}
