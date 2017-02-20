package com.holagame.model.coin;


/**
 * virtualCurrencyName	string	虚拟币名称，如：baoshi
virtualCurrencyAmount	int	数量
 * */
public class VirtualCoin {
	
	public String virtualCurrencyName = "";//	string	虚拟币名称，如：baoshi
	public int virtualCurrencyAmount;//	int	数量
	
	
	
	public VirtualCoin(String virtualCurrencyName, int virtualCurrencyAmount) {
		super();
		this.virtualCurrencyName = virtualCurrencyName;
		this.virtualCurrencyAmount = virtualCurrencyAmount;
	}
	
	
}
