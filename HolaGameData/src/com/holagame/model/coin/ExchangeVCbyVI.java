package com.holagame.model.coin;

import java.util.ArrayList;


/**
 * event	是	String	exchangeVCbyVI
accountId	是	string	账户ID
payVirtualItems	是	json数组	要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
virtualCurrencyName	是	string	获得的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
gainAmount	是	int	获得的虚拟币数量
ts	是	long	时间戳
 * */
public class ExchangeVCbyVI {
	
	public String event = "exchangeVCbyVI";//	是	String	exchangeVCbyVI
	
	public String accountId = "";//	是	string	账户ID
	
	//	是	json数组	要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> payVirtualItems = new ArrayList<VirtualItemType>();
	
	public String virtualCurrencyName = "";//	是	string	获得的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	
	public int gainAmount;//	是	int	获得的虚拟币数量
	
	public long ts;//	是	long	时间戳

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

	public ArrayList<VirtualItemType> getPayVirtualItems() {
		return payVirtualItems;
	}

	public void setPayVirtualItems(ArrayList<VirtualItemType> payVirtualItems) {
		this.payVirtualItems = payVirtualItems;
	}

	public String getVirtualCurrencyName() {
		return virtualCurrencyName;
	}

	public void setVirtualCurrencyName(String virtualCurrencyName) {
		this.virtualCurrencyName = virtualCurrencyName;
	}

	public int getGainAmount() {
		return gainAmount;
	}

	public void setGainAmount(int gainAmount) {
		this.gainAmount = gainAmount;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public ExchangeVCbyVI(String accountId,
			ArrayList<VirtualItemType> payVirtualItems, String virtualCurrencyName,
			int gainAmount, long ts) {
		super();
		this.accountId = accountId;
		this.payVirtualItems = payVirtualItems;
		this.virtualCurrencyName = virtualCurrencyName;
		this.gainAmount = gainAmount;
		this.ts = ts;
	}
	
	public static boolean isValid(String accountId,
			ArrayList<VirtualItemType> payVirtualItems, String virtualCurrencyName,
			int gainAmount, long ts){
		try {
			if(accountId == null || payVirtualItems == null || virtualCurrencyName == null){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
