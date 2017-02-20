package com.holagame.model.coin;

import com.holagame.em.TypeVirtualItem;


/**
 * virtualItemType	string	虚拟物品类型，参见虚拟物品类型
virtualItemName	string	虚拟物品名，视具体游戏而定，如：大药水、头像
virtualItemAmount	int	数量
 * */
public class VirtualItemType {
	public TypeVirtualItem virtualItemType;//	string	虚拟物品类型，参见虚拟物品类型
	public String virtualItemName = "";//	string	虚拟物品名，视具体游戏而定，如：大药水、头像
	public int virtualItemAmount;//	int	数量
	
//	有以下几种类型：
//	OneConsumeType：一次就消耗完的类型, 如：一次性药水
//	MultiConsumeType：多次消耗才能消耗完的类型，如：dota中的吃树可消耗5次
//	PermanentConsumeType：不会被消耗的类型，如：装备类，但可以卖掉/扔掉
//	AgingConsumeType：有时效的物品类型，如：限时时装
//	OtherType: 不能归到以上四类的类型
	
	
	
	public VirtualItemType(TypeVirtualItem virtualItemType, String virtualItemName,
			int virtualItemAmount) {
		super();
		
		
		
		this.virtualItemType = virtualItemType;
		this.virtualItemName = virtualItemName;
		this.virtualItemAmount = virtualItemAmount;
	}
	
	
	
	
}
