package com.holagame.model.coin;

import com.holagame.em.TypeVirtualCurrency;



/**
 * event	是	String	buyVirtualCurrency
accountId	是	string	账户ID
virtualCurrencyName	是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
amount	是	int	购买的虚拟币总量
currencyType	是	string	支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
payAmount	是	int	支付总金额
ts	是	long	时间戳
 * */
public class BuyVirtualCurrency {
	public String event = "buyVirtualCurrency";//	是	String	buyVirtualCurrency
	public String accountId	= "";//是	string	账户ID
	public String virtualCurrencyName = ""; //是	string	虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	public int amount; //	是	int	购买的虚拟币总量
	public TypeVirtualCurrency currencyType ;//	是	string	支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	public int payAmount;//	是	int	支付总金额
	public long ts ;//	是	long	时间戳
	
	
	public static boolean isValid(String accountId, String virtualCurrencyName,
			int amount, TypeVirtualCurrency currencyType, int payAmount, long ts){
		try {
			//参数为空
			if(accountId == null || virtualCurrencyName == null
					|| currencyType == null)
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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


	public long getTs() {
		return ts;
	}


	public void setTs(long ts) {
		this.ts = ts;
	}


	public BuyVirtualCurrency(String accountId, String virtualCurrencyName,
			int amount, TypeVirtualCurrency currencyType, int payAmount, long ts) {
		super();
		this.accountId = accountId;
		this.virtualCurrencyName = virtualCurrencyName;
		this.amount = amount;
		this.currencyType = currencyType;
		this.payAmount = payAmount;
		this.ts = ts;
	}
}
