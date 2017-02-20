package com.holagame.model.account;
/**
 *设置账号类型 模型
 * 对应 数据采集 中setAccountType
 *
 */
public class SetAccountTypeModel {
	
	/**事件名称*/
	private String event = "setAccountType";
	/**账户id*/
	private String accountId ;
	/**账户类型*/
	private String  accountType;
	/**时间戳*/
	private Long  ts;
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
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
	public SetAccountTypeModel() {
		super();
	}
	public SetAccountTypeModel(String accountId,
			String accountType, Long ts) {
		super();
		this.accountId = accountId;
		this.accountType = accountType;
		this.ts = ts;
	}

}
