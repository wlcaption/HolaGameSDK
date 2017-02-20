package com.holagames.xcds.modle;

import java.io.Serializable;

/**
 * 登录之前验证是否显示客服，更新等
 * @author niexiaoqiang
 */
public class PackInfoModel implements Serializable {
	//{"update":0,"kf":0}
	private static final long serialVersionUID = 1L;
	private int update;
	private int kf;
	private String uri;
	private String bbs;
	
	private NoticeModel notice = null;
	
	private ExitBanner exit_banner = null;
	
	private Notice active = null;
	
	private PayActivities recharge_active = null;
	
	private int force ;
	public String update_msg = "";
	
	private String verify_config = "";
	public NoticeModel getNotice(){
		return notice;
	}
	
	public void setNotice(NoticeModel notice){
		this.notice = notice;
	}
	
	public String getUpdate_msg(){
		return update_msg;
	}
	
	public void setUpdate_msg(String update_msg){
		this.update_msg = update_msg;
	}
	
	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public String getBbs() {
		return bbs;
	}

	public void setBbs(String bbs) {
		this.bbs = bbs;
	}

	public int getUpdate() {
		return update;
	}

	public void setUpdate(int update) {
		this.update = update;
	}

	public int getKf() {
		return kf;
	}

	public void setKf(int kf) {
		this.kf = kf;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getVerify_config() {
		return verify_config;
	}

	public void setVerify_config(String verify_config) {
		this.verify_config = verify_config;
	}

	public ExitBanner getExit_banner() {
		return exit_banner;
	}

	public void setExit_banner(ExitBanner exit_banner) {
		this.exit_banner = exit_banner;
	}

	public Notice getActive() {
		return active;
	}

	public void setActive(Notice active) {
		this.active = active;
	}

	public PayActivities getRecharge_active() {
		return recharge_active;
	}

	public void setRecharge_active(PayActivities recharge_active) {
		this.recharge_active = recharge_active;
	}
	
	
}
