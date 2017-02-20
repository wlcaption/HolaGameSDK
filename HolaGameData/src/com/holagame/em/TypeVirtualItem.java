package com.holagame.em;



/**
 * 虚拟物品类型

有以下几种类型： <Br>
OneConsumeType：一次就消耗完的类型, 如：一次性药水<Br>
MultiConsumeType：多次消耗才能消耗完的类型，如：dota中的吃树可消耗5次<Br>
PermanentConsumeType：不会被消耗的类型，如：装备类，但可以卖掉/扔掉<Br>
AgingConsumeType：随时间自动消耗的类型，如：时装<Br>
OtherType: 不能归到以上四类的类型<Br>
 * */
public enum TypeVirtualItem {
	/**一次就消耗完的类型, 如：一次性药水*/
	OneConsumeType, 
	
	/**多次消耗才能消耗完的类型，如：dota中的吃树可消耗5次*/
	MultiConsumeType, 
	
	/**不会被消耗的类型，如：装备类，但可以卖掉/扔掉*/
	PermanentConsumeType, 
	
	/**随时间自动消耗的类型，如：时装*/
	AgingConsumeType, 
	
	/**不能归到以上四类的类型*/
	OtherType;
	
//	/**一次就消耗完的类型, 如：一次性药水*/
//	public static final String TYPE_ONCE = "OneConsumeType";//：一次就消耗完的类型, 如：一次性药水
//	
//	/**多次消耗才能消耗完的类型，如：dota中的吃树可消耗5次*/
//	public static final String TYPE_MULTI = "MultiConsumeType";//
//	
//	/**不会被消耗的类型，如：装备类，但可以卖掉/扔掉*/
//	public static final String TYPE_EXIST = "PermanentConsumeType";//
//	
//	/**随时间自动消耗的类型，如：时装*/
//	public static final String TYPE_LIMITED = "AgingConsumeType";//：
//	
//	/**不能归到以上四类的类型*/
//	public static final String TYPE_OTHER = "OtherType";
//	
//	
//	public static boolean isValid(String type){
//		if(type == null || type.length() == 0){
//			return false;
//		}
//		
//		Set<String> set = new HashSet<String>();
//		set.add(TYPE_ONCE);
//		set.add(TYPE_MULTI);
//		set.add(TYPE_EXIST);
//		set.add(TYPE_LIMITED);
//		set.add(TYPE_OTHER);
//		
//		return set.contains(type);
//	}
	
}
