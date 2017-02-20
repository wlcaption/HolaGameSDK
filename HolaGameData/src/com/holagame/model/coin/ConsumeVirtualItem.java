package com.holagame.model.coin;

import com.holagame.em.TypeVirtualItem;


/**
 * event	是	String	consumeVirtualItem
accountId	是	string	账户ID
virtualItemType	是	string	虚拟物品类型，参见虚拟物品类型
virtualItemName	是	string	虚拟物品名
virtualItemAmount	是	int	数量
ts	是	long	时间戳
 * */
public class ConsumeVirtualItem {
	
	public String event = "consumeVirtualItem";//	是	String	consumeVirtualItem
	public String accountId = "";//	是	string	账户ID
	public TypeVirtualItem virtualItemType;//	是	string	虚拟物品类型，参见虚拟物品类型
	public String virtualItemName = "";//	是	string	虚拟物品名
	public int virtualItemAmount;//	是	int	数量
	public long ts;//	是	long	时间戳
	
	public ConsumeVirtualItem() {
		super();
	}

	public ConsumeVirtualItem(String accountId, TypeVirtualItem virtualItemType,
			String virtualItemName, int virtualItemAmount, long ts) {
		super();
		this.accountId = accountId;
		this.virtualItemType = virtualItemType;
		this.virtualItemName = virtualItemName;
		this.virtualItemAmount = virtualItemAmount;
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

	public TypeVirtualItem getVirtualItemType() {
		return virtualItemType;
	}

	public void setVirtualItemType(TypeVirtualItem virtualItemType) {
		this.virtualItemType = virtualItemType;
	}

	public String getVirtualItemName() {
		return virtualItemName;
	}

	public void setVirtualItemName(String virtualItemName) {
		this.virtualItemName = virtualItemName;
	}

	public int getVirtualItemAmount() {
		return virtualItemAmount;
	}

	public void setVirtualItemAmount(int virtualItemAmount) {
		this.virtualItemAmount = virtualItemAmount;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
	
}
