package com.holagame.model.coin;

import java.util.ArrayList;


/**
 * event	是	String	openGift
accountId	是	string	账户ID
giftId	是	string	礼包ID
gainItems	是	json数组	礼包中包含的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
gainVirtualCurrencies	是	json数组	礼包中包含的虚拟货币，每一个item为一个虚拟货币的JSONObject, 参见虚拟币对象
ts	是	long	时间戳
 * */
public class OpenGift {
	
	public String event = "openGift";//	是	String	openGift
	
	public String accountId	= "";//是	string	账户ID
	
	public String giftId = "";//	是	string	礼包ID
	
	//	是	json数组	礼包中包含的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	public ArrayList<VirtualItemType> gainItems = new ArrayList<VirtualItemType>();
	
	//	是	json数组	礼包中包含的虚拟货币，每一个item为一个虚拟货币的JSONObject, 参见虚拟币对象
	public ArrayList<VirtualCoin> gainVirtualCurrencies = new ArrayList<VirtualCoin>();
	
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

	public String getGiftId() {
		return giftId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	public ArrayList<VirtualItemType> getGainItems() {
		return gainItems;
	}

	public void setGainItems(ArrayList<VirtualItemType> gainItems) {
		this.gainItems = gainItems;
	}

	public ArrayList<VirtualCoin> getGainVirtualCurrencies() {
		return gainVirtualCurrencies;
	}

	public void setGainVirtualCurrencies(
			ArrayList<VirtualCoin> gainVirtualCurrencies) {
		this.gainVirtualCurrencies = gainVirtualCurrencies;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public OpenGift(String accountId, String giftId,
			ArrayList<VirtualItemType> gainItems,
			ArrayList<VirtualCoin> gainVirtualCurrencies, long ts) {
		super();
		this.accountId = accountId;
		this.giftId = giftId;
		this.gainItems = gainItems;
		this.gainVirtualCurrencies = gainVirtualCurrencies;
		this.ts = ts;
	}
	
	
	public static boolean isValid(String accountId, String giftId,
			ArrayList<VirtualItemType> gainItems,
			ArrayList<VirtualCoin> gainVirtualCurrencies, long ts){
		try {
			if(accountId == null || giftId == null || gainItems == null || gainVirtualCurrencies == null){
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
