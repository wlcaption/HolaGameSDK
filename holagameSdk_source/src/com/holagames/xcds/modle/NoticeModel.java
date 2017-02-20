package com.holagames.xcds.modle;
/**
 * 更新返回公告类模型
 * @author 邹龙
 *
 */
public class NoticeModel {
	private String id;
	private String url;
	private String title;
	
	public NoticeModel() {
		super();
	}
	public NoticeModel(String id, String url, String title) {
		super();
		this.id = id;
		this.url = url;
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
