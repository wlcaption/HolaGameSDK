package com.holagames.xcds.modle;

/**
 * 游戏信息
 * @author 邹龙
 *
 */
public class GamerInfo {
	/**游戏区服*/
	private String gameService;
	/**用户ID*/
	private String userId;
	/**手机类型*/
	private String phoneModel;
	/**渠道*/
	private String channel;
	/**角色名称*/
	private String roleName;
	
	public GamerInfo() {
		super();
	}
	public GamerInfo(String gameService, String userId, String phoneModel,
			String channel, String roleName) {
		super();
		this.gameService = gameService;
		this.userId = userId;
		this.phoneModel = phoneModel;
		this.channel = channel;
		this.roleName = roleName;
	}
	public String getGameService() {
		return gameService;
	}
	public void setGameService(String gameService) {
		this.gameService = gameService;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPhoneModel() {
		return phoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
}
