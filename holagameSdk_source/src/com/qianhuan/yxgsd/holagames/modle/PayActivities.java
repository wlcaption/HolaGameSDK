package com.qianhuan.yxgsd.holagames.modle;
/**
 * 支付消息栏配置
 * @author zoulong
 *
 */
public class PayActivities {
	private String id;
	private String title;
	private String content;//zhengwen
	private String status;
	public PayActivities() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
