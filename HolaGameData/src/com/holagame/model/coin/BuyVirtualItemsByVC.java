package com.holagame.model.coin;

import java.util.ArrayList;


/**
 * event	是	String	buyVirtualItemsByVC
accountId	是	string	账户ID
virtualCurrencyName	是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
amount	是	int	虚拟币数量
gainItems	是	json数组	购买的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
ts	是	long	时间戳
 * */
public class BuyVirtualItemsByVC {
	
	public String event = "buyVirtualItemsByVC";//	是	String	buyVirtualItemsByVC
	
	public String accountId = "";//	是	string	账户ID
	
	public String virtualCurrencyName = "";//	是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	
	public int amount;//	是	int	虚拟币数量
	
	//	是	json数组	购买的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> gainItems = new ArrayList<VirtualItemType>();
	
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

	public ArrayList<VirtualItemType> getGainItems() {
		return gainItems;
	}

	public void setGainItems(ArrayList<VirtualItemType> gainItems) {
		this.gainItems = gainItems;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public BuyVirtualItemsByVC(String accountId, String virtualCurrencyName,
			int amount, ArrayList<VirtualItemType> gainItems, long ts) {
		super();
		this.accountId = accountId;
		this.virtualCurrencyName = virtualCurrencyName;
		this.amount = amount;
		this.gainItems = gainItems;
		this.ts = ts;
	}
	
	public static boolean isValid(String accountId, String virtualCurrencyName,
			int amount, ArrayList<VirtualItemType> gainItems, long ts){
		try {
			if(accountId == null || virtualCurrencyName == null || gainItems == null){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
