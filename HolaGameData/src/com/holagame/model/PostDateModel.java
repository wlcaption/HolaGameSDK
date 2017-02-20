package com.holagame.model;

import java.util.ArrayList;

import android.util.Log;

import com.holagame.tool.DataEvent;
import com.holagame.tool.Gamer;
/**
 * 上传信号模型
 * @author 邹龙
 *
 */
public class PostDateModel {
	/**应用id*/
	private String appId;
	/**平台*/
	private int platform;
	/**渠道id*/
	private String channelId;
	/**分包id*/
	private String packageId;
	/**应用包版*/
	private String apkVersion;
	/**sdk版本*/
	private String sdkVersion;
	/**post上传时间*/
	private Long ts;
	/**用户 的ip*/
	private String ip ;
	/**用户的设备id*/
	private String deviceId ;
	/**用户的手机厂商*/
	private String manufacturer ;
	/**用户手机 的品牌*/
	private String brand ;
	/**用户现在的网络类型*/
	private String network ;
	/**用户手机的运营商信息*/
	private String resolution ;
	/**上传数据*/
	private ArrayList<String> data;
	
	public static String gameArea = "unknown";
	
	/**游戏appid*/
	private String gameAPPID = DataEvent.GAME_APPID;
	/**sessionId*/
	public String sessionId = Gamer.sessionId;
	
	
	
	
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getGameAPPID() {
		return gameAPPID;
	}
	public void setGameAPPID(String gameAPPID) {
		this.gameAPPID = gameAPPID;
	}
	public PostDateModel() {
		super();
	}
	/**
	 * 
	 * @param appId 应用id
	 * @param platform 平台
	 * @param channelId 渠道id
	 * @param packageId 分包id
	 * @param apkVersion 应用包版 
	 * @param sdkVersion sdk版本
	 * @param ts post上传时间，需要加上时间偏移量
	 * @param data 上传数据
	 * @param manufacturer 手机生产商，例如：小米，三星华为 
	 * @param brand 品牌 
	 * @param network 网络类型
	 * @param resolution 设备运营商信息
	 * 
	 */
	public PostDateModel(String appId, int platform, String channelId,
			String packageId, String apkVersion, String sdkVersion, Long ts,
			String ip, String deviceId, String manufacturer, String brand,
			String network, String resolution, ArrayList<String> data) {
		super();
		
		this.appId = appId;
		this.platform = platform;
		this.channelId = channelId;
		this.packageId = packageId;
		this.apkVersion = apkVersion;
		this.sdkVersion = sdkVersion;
		this.ts = ts + Gamer.OFFSET_TIME;
		this.ip = ip;
		this.deviceId = deviceId;
		this.manufacturer = manufacturer;
		this.brand = brand;
		this.network = network;
		this.resolution = resolution;
		this.data = data;
	}


	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public int getPlatform() {
		return platform;
	}
	
	public void setPlatform(int platform) {
		this.platform = platform;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public String getPackageId() {
		return packageId;
	}
	
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	
	public Long getTs() {
		return ts;
	}
	
	public void setTs(Long ts) {
		this.ts = ts;
	}
	
	public ArrayList<String> getData() {
		return data;
	}
	
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	
	public String getApkVersion() {
		return apkVersion;
	}
	
	public void setApkVersion(String apkVersion) {
		this.apkVersion = apkVersion;
	}
	
	public String getSdkVersion() {
		return sdkVersion;
	}
	
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getGameArea() {
		return gameArea;
	}
	public void setGameArea(String gameArea) {
		this.gameArea = gameArea;
	}
	
}
