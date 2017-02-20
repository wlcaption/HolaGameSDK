package com.holagame.model.coin;

import java.util.ArrayList;



/**
 * event	是	String	exchangeVIbyVI
accountId	是	string	账户ID
payVirtualItems	是	json数组	要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
gainVirtualItems	是	json数组	获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
ts	是	long	时间戳
 * */
public class ExchangeVIbyVI {
	
	public String event = "exchangeVIbyVI";//	是	String	exchangeVIbyVI
	
	public String accountId = "";//	是	string	账户ID
	
	//	是	json数组	要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> payVirtualItems = new ArrayList<VirtualItemType>();
	
	//	是	json数组	获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> gainVirtualItems = new ArrayList<VirtualItemType>();
	
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

	public ArrayList<VirtualItemType> getGainVirtualItems() {
		return gainVirtualItems;
	}

	public void setGainVirtualItems(ArrayList<VirtualItemType> gainVirtualItems) {
		this.gainVirtualItems = gainVirtualItems;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public ExchangeVIbyVI(String accountId,
			ArrayList<VirtualItemType> payVirtualItems,
			ArrayList<VirtualItemType> gainVirtualItems, long ts) {
		super();
		this.accountId = accountId;
		this.payVirtualItems = payVirtualItems;
		this.gainVirtualItems = gainVirtualItems;
		this.ts = ts;
	}
	
	public static boolean isValid(String accountId,
			ArrayList<VirtualItemType> payVirtualItems,
			ArrayList<VirtualItemType> gainVirtualItems, long ts){
		if(accountId == null || payVirtualItems == null || gainVirtualItems == null){
			return false;
		}
		return true;
	}
	
	
}
