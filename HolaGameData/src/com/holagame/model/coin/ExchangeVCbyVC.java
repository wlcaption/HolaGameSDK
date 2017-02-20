package com.holagame.model.coin;


public class ExchangeVCbyVC {
	
	public String event = "exchangeVCbyVC";//	是	String	exchangeVCbyVC
	
	public String accountId = "";//	是	string	账户ID
	
	public String payVirtualCurrencyName = "";//	是	string	支付的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	
	public int payAmount;//	是	int	支付的虚拟币数量
	
	public String gainVirtualCurrencyName = "";//	是	string	支付的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	
	public int gainAmount;//	是	int	支付的虚拟币数量
	
	public long ts;//	是	long	时间戳

	
	public ExchangeVCbyVC() {
		super();
	}

	public ExchangeVCbyVC(String accountId,
			String payVirtualCurrencyName, int payAmount,
			String gainVirtualCurrencyName, int gainAmount, long ts) {
		super();
		this.accountId = accountId;
		this.payVirtualCurrencyName = payVirtualCurrencyName;
		this.payAmount = payAmount;
		this.gainVirtualCurrencyName = gainVirtualCurrencyName;
		this.gainAmount = gainAmount;
		this.ts = ts;
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

	public String getPayVirtualCurrencyName() {
		return payVirtualCurrencyName;
	}

	public void setPayVirtualCurrencyName(String payVirtualCurrencyName) {
		this.payVirtualCurrencyName = payVirtualCurrencyName;
	}

	public int getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(int payAmount) {
		this.payAmount = payAmount;
	}

	public String getGainVirtualCurrencyName() {
		return gainVirtualCurrencyName;
	}

	public void setGainVirtualCurrencyName(String gainVirtualCurrencyName) {
		this.gainVirtualCurrencyName = gainVirtualCurrencyName;
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
	
	
	
}
