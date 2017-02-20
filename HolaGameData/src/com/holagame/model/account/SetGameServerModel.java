package com.holagame.model.account;

/**
 * 游戏区服 模型
 * 对应 数据采集中的 setGameServer 设置游戏区服
 * 
 */
public class SetGameServerModel {
	
	private String event = "setGameServer";
	//账户id
	private String accountId ;
	//区服名称
	private String gameServer ;
	//ts时间戳
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
	public String getGameServer() {
		return gameServer;
	}
	public void setGameServer(String gameServer) {
		this.gameServer = gameServer;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public SetGameServerModel() {
		super();
	}
	public SetGameServerModel(String accountId,String gameServer, long ts) {
		super();
		this.accountId = accountId;
		this.gameServer = gameServer;
		this.ts = ts;
	}
	
	
	
	
	
}
