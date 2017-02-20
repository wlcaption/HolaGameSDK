package com.holagame.model.coin;

import java.util.ArrayList;

import com.holagame.em.TypeVirtualCurrency;

public class BuyVirtualItemsByRC {
	public String event = "buyVirtualItemsByRC";//	是	String	buyVirtualItemsByRC
	public String accountId = "";//	是	string	账户ID
	public TypeVirtualCurrency currencyType;//	是	string	支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	public int payAmount;//	是	int	支付总金额
	//	是	json数组	获得的虚拟物品,每一个item为一个虚拟物品的JSONObject，参见虚拟物品对象
	public ArrayList<VirtualItemType> gainItems = new ArrayList<VirtualItemType>();
	public long ts;//	是	long	时间戳
	
	public BuyVirtualItemsByRC(String accountId,
			TypeVirtualCurrency currencyType, int payAmount,
			ArrayList<VirtualItemType> gainItems, long ts) {
		super();
		this.accountId = accountId;
		this.currencyType = currencyType;
		this.payAmount = payAmount;
		this.gainItems = gainItems;
		this.ts = ts;
	}
	public BuyVirtualItemsByRC() {
		super();
	}
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
	public TypeVirtualCurrency getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(TypeVirtualCurrency currencyType) {
		this.currencyType = currencyType;
	}
	public int getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(int payAmount) {
		this.payAmount = payAmount;
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
	
}
