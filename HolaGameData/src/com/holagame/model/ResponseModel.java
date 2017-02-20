package com.holagame.model;
/**
 * 服务器模
 * @author 邹龙
 *
 */
public class ResponseModel {
	/**返回*/
	private int code;
	/**返回消息*/
	private String message;
	
	public ResponseModel() {
		super();
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	

	public ResponseModel(int code, String message) {
		super();
		this.code = code;
		this.message = message;

	}

	
	
	
}
