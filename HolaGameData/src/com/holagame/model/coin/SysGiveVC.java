package com.holagame.model.coin;


/**
 * event	是	String	sysGiveVC
accountId	是	string	账户ID
virtualCurrencyName	是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
amount	是	int	虚拟币数量
ts	是	long	时间戳
 * */
public class SysGiveVC {
	
	public String event = "sysGiveVC";//	是	String	sysGiveVC
	public String accountId = "";//	是	string	账户ID
	public String virtualCurrencyName = "";//	是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	public int amount;//	是	int	虚拟币数量
	public long ts;//	是	long	时间戳
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getVirtualCurrencyName() {
		return virtualCurrencyName;
	}
	public void setVirtualCurrencyName(String virtualCurrencyName) {
		this.virtualCurrencyName = virtualCurrencyName;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public SysGiveVC(String accountId, String virtualCurrencyName, int amount,
			long ts) {
		super();
		this.accountId = accountId;
		this.virtualCurrencyName = virtualCurrencyName;
		this.amount = amount;
		this.ts = ts;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	public static boolean isValid(String accountId, String virtualCurrencyName, int amount,
			long ts){
		try {
			if(accountId == null || virtualCurrencyName == null){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
