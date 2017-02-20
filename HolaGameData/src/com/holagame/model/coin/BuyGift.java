package com.holagame.model.coin;

import com.holagame.em.TypeVirtualCurrency;



/**
 * event	是	String	buyGift
accountId	是	string	账户ID
giftId	是	string	礼包ID
currencyType	是	string	支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
payAmount	是	int	支付总金额
ts	是	long	时间戳
 * */
public class BuyGift {
	
	public String event = "buyGift";//	是	String	buyGift
	public String accountId = "";//	是	string	账户ID
	public String giftId = "";//	是	string	礼包ID
	public TypeVirtualCurrency currencyType;//	是	string	支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	public int payAmount;//	是	int	支付总金额
	public long ts;//	是	long	时间戳
	
	
	public static boolean isValid(String accountId, String giftId, TypeVirtualCurrency currencyType,
			int payAmount, long ts){
		try {
			if(accountId == null || giftId == null || currencyType == null){
				return false;
			}
			
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


	public String getGiftId() {
		return giftId;
	}


	public void setGiftId(String giftId) {
		this.giftId = giftId;
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


	public BuyGift(String accountId, String giftId,
			TypeVirtualCurrency currencyType, int payAmount, long ts) {
		super();
		this.accountId = accountId;
		this.giftId = giftId;
		this.currencyType = currencyType;
		this.payAmount = payAmount;
		this.ts = ts;
	}
	
	
	
	
	
}
