package com.holagame.model.account;
/**
 * 对应 数据采集的 register注册事件
 *
 */
public class RegisterModel {
	private String event = "register";
	/**账户id*/
	private String accountId;
	/**账户类型*/
	private String accountType;

	private String gender;
	
	private String email ;
	/**电话*/
	private String phone;
	
	private long ts ;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public RegisterModel() {
		super();
	}

	public RegisterModel(String accountId, String accountType,
			String gender, String email, String phone, long ts) {
		super();
		this.accountId = accountId;
		this.accountType = accountType;
		this.gender = gender;
		this.email = email;
		this.phone = phone;
		this.ts = ts;
	}

	@Override
	public String toString() {
		return "RegisterModel [event=" + event + ", accountId=" + accountId
				+ ", accountType=" + accountType + ", gender=" + gender
				+ ", email=" + email + ", phone=" + phone + ", ts=" + ts + "]";
	}
	
	
}	
