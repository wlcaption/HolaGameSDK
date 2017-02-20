package com.holagame.model.coin;

import java.util.ArrayList;


/**
 * event	是	String	sysGiveVI
accountId	是	string	账户ID
gainItems	是	json数组	获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
ts	是	long	时间戳
 * */
public class SysGiveVI {
	
	public String event = "sysGiveVI";//	是	String	sysGiveVI
	
	public String accountId = "";//	是	string	账户ID
	
	//	是	json数组	获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> gainItems = new ArrayList<VirtualItemType>();
	
	public long ts ;//	是	long	时间戳

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

	public SysGiveVI(String accountId, ArrayList<VirtualItemType> gainItems, long ts) {
		super();
		this.accountId = accountId;
		this.gainItems = gainItems;
		this.ts = ts;
	}
	
	public static boolean isValid(String accountId, ArrayList<VirtualItemType> gainItems, long ts){
		if(accountId == null || gainItems == null){
			return false;
		}
		return true;
	}
	
}
