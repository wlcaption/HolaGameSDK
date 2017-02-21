package com.qianhuan.yxgsd.holagames.modle;
/**
 * 公告配置
 * @author zoulong
 *
 */
public class Notice {
	private String id = "";//id
	private String title = "";//标题
	private String content = "";//正文
	private String url = "";;//点击跳转地址
	private String imgUrl = "";;//显示图片地址
	private String status = "";;
	public Notice() {
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
