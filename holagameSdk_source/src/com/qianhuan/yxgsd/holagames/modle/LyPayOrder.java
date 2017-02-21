package com.qianhuan.yxgsd.holagames.modle;

import java.io.Serializable;

/**
 * 支付订单类
 * @author ly
 */
public class LyPayOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 支付宝订单
	 */
	public static final int ORDER_ALIY = 0;
	/**
	 * 银联订单
	 */
	public static final int ORDER_UPBANK = 1;
	/**
	 * 财付通订单
	 */
	public static final int ORDER_CF = 2;
    private String pack_key;//分包key
    private String user_coupon_id;//是否使用玩家的代金券. 传入代金券的ID. 游客账号不能使用代金券 不予显示此项
	private String amount;//充值金额(分)
	private String app_order_id;//(应用内订单号)
	private String app_uid;//(应用内用户ID)
	private String notify_uri;//(应用服务器回调地址)
	private String product_name;//(应用内商品名称)
	private String product_id;//(应用内商品ID)
	private String app_username;//(应用内用户名称)
	private String access_token;//

/**
 * 
 * @param pack_key 分包key
 * @param user_coupon_id 是否使用玩家的代金券. 传入代金券的ID. 游客账号不能使用代金券 不予显示此项
 * @param uid 火拉平台用户UID
 * @param amount 充值金额(分)
 * @param app_order_id (应用内订单号)
 * @param app_uid (应用内用户ID)
 * @param notify_uri (应用服务器回调地址)
 * @param product_name (应用内商品名称)
 * @param product_id (应用内商品ID)
 * @param app_username (应用内用户名称)
 */
	public LyPayOrder(String pack_key,String amount,
			String app_order_id, String app_uid, String notify_uri,
			String product_name, String product_id, String app_username,
			String access_token) {
		super();
		this.pack_key = pack_key;
		this.amount = amount;
		this.app_order_id = app_order_id;
		this.app_uid = app_uid;
		this.notify_uri = notify_uri;
		this.product_name = product_name;
		this.product_id = product_id;
		this.app_username = app_username;
		this.access_token = access_token;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getApp_order_id() {
		return app_order_id;
	}

	public void setApp_order_id(String app_order_id) {
		this.app_order_id = app_order_id;
	}

	public String getApp_uid() {
		return app_uid;
	}

	public void setApp_uid(String app_uid) {
		this.app_uid = app_uid;
	}

	public String getNotify_uri() {
		return notify_uri;
	}

	public void setNotify_uri(String notify_uri) {
		this.notify_uri = notify_uri;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getApp_username() {
		return app_username;
	}

	public void setApp_username(String app_username) {
		this.app_username = app_username;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getPack_key() {
		return pack_key;
	}

	public void setPack_key(String pack_key) {
		this.pack_key = pack_key;
	}

	public String getUser_coupon_id() {
		return user_coupon_id;
	}

	public void setUser_coupon_id(String user_coupon_id) {
		this.user_coupon_id = user_coupon_id;
	}
	
}
