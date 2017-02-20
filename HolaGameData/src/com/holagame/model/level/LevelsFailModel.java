package com.holagame.model.level;
/**
 *关卡失败事件：
 *对应于 数据采集事件中 levelsFail
 */
public class LevelsFailModel {
	
	private String event = "levelsFail";
	
	private String accountId ;
	/** 游戏关卡编号 */
	private String levelId;
	/** 失败的原因 */
	private String reason;
	/** 时间戳 */
	private long ts;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public LevelsFailModel() {
		super();
	}
	public LevelsFailModel(String accountId, String levelId,
			String reason, long ts) {
		super();
		this.accountId = accountId;
		this.levelId = levelId;
		this.reason = reason;
		this.ts = ts;
	}
	
	

	
}
