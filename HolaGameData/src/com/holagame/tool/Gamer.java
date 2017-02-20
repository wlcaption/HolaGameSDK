package com.holagame.tool;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.MessageUtil;
import com.alibaba.fastjson.JSON;
import com.holagame.em.DataType;
import com.holagame.em.TypeVirtualCurrency;
import com.holagame.em.TypeVirtualItem;
import com.holagame.global.Constant;
import com.holagame.model.account.BeatHeartModel;
import com.holagame.model.coin.VirtualCoin;
import com.holagame.model.coin.VirtualItemType;
import com.holagame.model.error.ErrorEvent;
import com.holagame.model.operate.StartUpModel;
import com.holagame.tool.HttpHelper.MHttp;
import com.loopj.android.http.AsyncHttpResponseHandler;
/**
 * 上传信息类
 * @author 邹龙
 *
 */
public abstract class Gamer{
	private static String Tag="Gamer";
	public static final String DEFAULT_VAULE = "unKnown";
	/**数据收集是否初始化*/
	public static boolean isInit=false;
//	public static String ServerID = "";
	
	public static Handler handler;
	public static String sessionId = "";
	
	public static Context mActivity;
	public static String mChannel = "";
	
	/**时间偏移量，用服务器时间-手机时间*/
	public static long OFFSET_TIME = 0;
	/**账号id*/
	public static String DATA_ACCOUNT_ID = "";
	
	public static int PERIOD = 5000;
	private static boolean isRunning;
	
	public static MessageUtil mSecret;
	
	public static GamerCenter gamerCenter=null;
	
	public static SDKCenter sdkCenter = null;
	

	/**
	 * 初始化
	 * @param context applicationcontext
	 * @param channel 渠道
	 * @param isDebug 是否是调试模式
	 */
	public static void init(Context context, String channel, boolean isDebug){
		try {
			Logd.d(Tag, "init call.....");
			mSecret = new MessageUtil();
			Constant.isDebug = isDebug;
//			if(isDebug) showToast("统计SDK为debug模式");
			mActivity = context;
			mChannel = channel;
			gamerCenter =  GamerCenter.Init(mActivity);
			sdkCenter = SDKCenter.Init(mActivity);
			if(isInit) return;
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public static void showToast(final String msg){
//		if(mActivity == null || msg == null)
//			return;
//		try {
//			mActivity.runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void onPause(){
		saveQueue();
		isRunning = false;
	}
	
	public static void onResume(){
		isRunning = true;
	}
	
	public static void init(){
		//初始化上传队列
//		Waiting_Queue_Signal = new ArrayList<String>();
		GameInit.getConfig(mActivity, new MHttp(){
			
			@Override
			public void onSuccess(final String msg) {
				try {
					Logd.e(Tag, msg);
					JSONObject json = new JSONObject(msg);
					sessionId = json.getString("sessionId");
					OFFSET_TIME = json.getLong("serverTime") - System.currentTimeMillis();
					PERIOD = json.getInt("heartbeatInterval") * 1000;
					//开始初始化
					//加载本地缓存消息
					//开启上传缓存
					handQueue(mActivity);
					NetWork.setContext(mActivity);
					DataEvent.init(mActivity, mChannel);
					CrashHandler.init(mActivity);
					//开始startup事件
					startUp();
					//开始心跳
					initBeatHeart();
					isInit = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailed() {
				
			}
		});
	}
	
	private static void initBeatHeart(){
		Logd.e(Tag, "bearHear init...");
		isRunning = true;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(! isRunning || !isInit || (DATA_ACCOUNT_ID.length() == 0)){
					Logd.e(Tag, "isRunning: " + isRunning + ", isInit: " + isInit + ", account.length: " + DATA_ACCOUNT_ID.length());
					return;
				}
				//添加心跳包的数据
				BeatHeartModel loginModel = new BeatHeartModel(Gamer.DATA_ACCOUNT_ID, Gamer.getTime());
				String queueItem = JSON.toJSONString(loginModel);
				gamerCenter.addData(queueItem);
				Logd.e(Tag, "BeatHeart add...");
			}
		}, 5000, PERIOD);
	}
	
	private static  void handMsg(Context context, Message msg){
		gamerCenter.Send();
		sdkCenter.Send();
	    //移除按时发送的消息，防止条数达到后重复添加消息。也重置时间，重置缓存发送。
		handler.removeMessages(1);
	    Message message=handler.obtainMessage(1); 
	    handler.sendMessageDelayed(message, Constant.Submit_Time_Span);
	}
	/**
	 * 将数据发送出去
	 * @param data 发送数据
	 * @param context 上下文
	 */
	public static void sendData(ArrayList<String> data, Context context,DataType type){
		String origData = DataEvent.getSignal(data,type);
		Logd.d(Tag, "准备上传的数据appid:" + origData);
		String sendingData = mSecret.encryptcbc(origData); //将要发送的数据
		sendingData = "data=" + sendingData + "&origData=" + origData;
		sendingData = sendingData.replaceAll("[+]", "%2B");
		NetWork.getInstance(sendingData, Constant.URL_ADD_BASE64,new HttpResponseHandler(type)).start(); //http发送请求
		if(Constant.isDebug){
			HandleFile.saveDataLocal(context, sendingData, "sendlogs.txt");
		}
	}
	
	private static void handQueue(final Context context) {
		handler = new Handler(context.getMainLooper()){  
				public void handleMessage(Message msg){         // handle message  
					try {
						handMsg(context, msg);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
	
		};  
		Message message=handler.obtainMessage(1); 
		handler.sendMessageDelayed(message, Constant.Submit_Time_Span);
	}
	
	protected static byte[] decryptcbc(byte[] key, byte[] in) throws Exception {
        SecretKeySpec aeskey = new SecretKeySpec(key, 0, key.length, "AES");
        IvParameterSpec cbcIv = new IvParameterSpec(hex2byte("00000000000000000000000000000000"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aeskey, cbcIv);
        return cipher.doFinal(in);
    }
	
	protected static byte[] hex2byte(String hex) {
        int len = hex.length() / 2;
        if (len % 2 != 0) {
            throw new IllegalArgumentException();
        }
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hex.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }
	
	public static long getTime(){
		long ts = System.currentTimeMillis() + OFFSET_TIME;
		return ts;
	}
	
//	public static String getServerId(){
//		
//		return ServerID;
//	}
//	
	/**
	 * 加载本地没有上传的数据到 等待队列中
	 * @param context 上下文
	 */
	public abstract void loadingWaitingQueueSignal(Context context);
	
	
	public static void startUp(){
		Logd.d(Tag, "startUp calling..");
		try {
			if(isInit){
				StartUpModel statup = new StartUpModel(Gamer.getTime());
				if(!DeviceUtil.isValidObject(statup, null)) return;
				String queueItem = getQueueItem(statup);
				sdkCenter.addData(queueItem);
			}else{
				Logd.d(Tag, "创建StartUp数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 注册事件 转成Json格式的String ，添加到 等待队列中.
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 *@param accountId 账户id，
	 *@param accountType 账户类型
	 *@param gender 性别
	 *@param email 邮箱
	 *@param phone 手机号 
	 */
	public static void beatHeart(String accountId){
		Logd.d(Tag, "beatHeart calling...");
		try {
			if(isInit){
				long ts = Gamer.getTime();
				BeatHeartModel bhModel = new BeatHeartModel(accountId, ts);
				if(!DeviceUtil.isValidObject(bhModel, null)) return;
				String queueItem = getQueueItem(bhModel);
				sdkCenter.addData(queueItem);
			}else{
				Logd.d(Tag, "创建Register数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 注册事件 转成Json格式的String ，添加到 等待队列中
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 *@param accountId 账户id，
	 *@param accountType 账户类型
	 *@param gender 性别
	 *@param email 邮箱
	 *@param phone 手机号 
	 */
	public abstract void Register(String accountId, String accountType, String gender, String email, String phone);
	
	/**
	 * 登录事件 抽象成 JsonObject 对象，添加到 等待队列中
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param accountid 玩家的id,如果玩家是 游客则传SDK中用于标识用户的设备码，如果是正式用户，就传accountId

	 */
	public abstract void Login(String accountId);
	
	/**
	 * setAccountType 设置账户类型事件,转成String ，加入 等待队列中
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param accountType 账户类型
	 * @param accountId 账号ID
	 * 
	 */
	public abstract void setAccountType(String accountId,String accountType);
	
	/**setGender 设置性别事件 ,转成String ，加入 等待队列中,
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param gender int gender 1 男，2 女
	 * @param accountId 账号ID
	 * */
	public abstract void setGender(String accountId,int gender);
	
	
	/**
	 * setAge 设置玩家年龄事件  ,转成String ，加入 等待队列中.
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param age 年龄
	 * @param accountId 账号ID 
	 * */
	public abstract void setAge(String accountId ,int age);
	
	
	/**
	 * 设置玩家等级，将事件转成 String,然后加入到消息队列中.
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param level int 玩家等级
	 * @param accountId 账号ID 
	 */
	public abstract void setLevel(String accountId,int level);

	/**
	 * 设置游戏服务器，将事件转成 String,然后加入到消息队列中。
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param gameServer 游戏区服
	 * @param accountId 账号ID 
	 */
	public abstract void setGameServer(String accountId,String gameServer);
	
	/** 手机号绑定事件，将事件转成 String,然后加入到消息队列中。
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param mobile 手机号
	 * @param isok boolean 是否绑定成功
	 * @param accountId 账号ID 
	 */
	public abstract void MobileBind(String accountId ,String mobile,boolean isok);
	

	/**
	 *  logout 用户退出事件，将事件转成 String,然后加入到消息队列中
	 *  如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 *  @param accountId 账户名
	 */
	public abstract void logout(String accountId);
	
	/** 退出游戏或者应用事件 将事件转成 String,然后加入到消息队列中.
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param accountId 账户id
	 */
	public abstract void ExitEvent(String accountId);
	

	/**
	 * 游戏关卡开始事件,将事件转成 String,然后加入到消息队列中。
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param levelId 设置游戏关卡编号
	 * @param accountId 账户id
	 */
	public void gameBegin(String accountId ,String levelId){};
	
	/**
	 * 游戏关卡 完成事件，将事件转成 String,然后加入到消息队列中。
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param levelId 设置游戏关卡编号
	 * @param accountId 账户id
	 */
	public void gameComplete(String accountId,String levelId){};
	
	/**
	 * 游戏关卡 失败事件,将事件转成 String,然后加入到消息队列中。
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * @param levelId 游戏关卡编号
	 * @param reason 失败的原因
	 * @param accountId 账户id
	 */
	public void gameFail(String accountId ,String levelId,String reason){};
	
	
	
	
	
	/**
	 * 错误上报
	 * @param title 类别
	 * @param error 错误详细数据
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 */
//	public static void reportError(String title, String error){
//		if(isInit){
//			ErrorLogModel errmodel =new ErrorLogModel(title, error, Gamer.getTime()/1000,
//				android.os.Build.MODEL,android.os.Build.VERSION.RELEASE,"reportError");
//			String queueItem = getQueueItem(errmodel);
//			addCollection(queueItem);
//		}else{
//			LogGame.log(Tag, "创建reportError数据模型失败，init没有初始化");
//		}
//		
//	}
	
	
	/**错误上报事件 error
	 * @param accountId 账户id
	 * @param msg 错误信息
	 * */
	public static void errorReport(String accountId,String msg){
		Logd.d(Tag, "errorReport calling...");
		try {
			if(isInit){
				ErrorEvent error = new ErrorEvent(accountId, msg,Gamer.getTime());
				if(!DeviceUtil.isValidObject(error, null)) return;
				String queueItem = getQueueItem(error);
				sdkCenter.addData(queueItem);
			}else{
				Logd.d(Tag, "创建errorReport数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 将要上传的数据打包成队列
	 * @param value 上传数据模型
	 * @param head 上传接口
	 * @return 队列项
	 * 直接 格式化返回 String object的对象值
	 */
	protected static String getQueueItem(Object value) {
		Logd.d(Tag, "getQueueItem calling...");
		if(value != null){
			return JSON.toJSONString(value);
		}else{
			return JSON.toJSONString(new ErrorEvent("unKnown", "getQueueItem发送错误",Gamer.getTime()));
		}
	
	}
	
	public static void saveQueue(){
		if(getQueueTotal()>0){
			gamerCenter.saveLocal();
			sdkCenter.saveLocal();
		}
	}
	/**
	 * 添加数据到队列,回调给主gamer,同时监控子gamer队列数量
	 * 
	 */
	public void addCollection() {
		if(getQueueTotal() >= Constant.Queue_Max_Siz){
		   Message message=handler.obtainMessage(2); 
		   handler.sendMessage(message);
		}
	}
	
	/**
	 * 获取发送队列的条数
	 * @return 游戏数据收集和SDK数据收集的条数
	 */
	public static int getQueueTotal(){
		return sdkCenter.getQueueCount()+gamerCenter.getQueueCount();
	}

	/**
	 * 回到接口
	 * @author LY
	 *
	 */
	public static class HttpResponseHandler extends AsyncHttpResponseHandler{
		 
		private DataType type;
		public HttpResponseHandler(DataType type) {
			 this.type = type;
		}
		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				if(type == DataType.LONGYUAN){
					sdkCenter.sendSuccess();
				}else if(type == DataType.GAME){
					gamerCenter.sendSuccess();
				}
				Logd.d(Tag, "数据收集上传成功："+new String(arg2));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			try {
				if(type == DataType.LONGYUAN){
					sdkCenter.sendFail();
				}else if(type == DataType.GAME){
					gamerCenter.sendFail();
				}
				if(arg3!=null && arg2!=null){
					Logd.d(Tag, "数据收集上传失败："+new String(arg2)+"/n"+arg3.toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	}
	
	public static void setGameArea(String gameArea){
		if(DataEvent.postDateModel !=null){
			DataEvent.postDateModel.setGameArea(gameArea);
		}else{
			Logd.e(Tag, "设置区服失败，因为数据模型为空，请检查是否初始化");
		}
	}
	/**
	 * 将队列保存到本地
	 */
	public abstract void saveLocal();
	/**
	 * 发送消息
	 */
	public abstract void Send();
	/**
	 * 添加数据到队列
	 */
	public abstract void addData(String data);
	/**
	 * 发送失败
	 */
	public abstract void sendFail();
	
	/**
	 * 发送成功
	 */
	public abstract void sendSuccess();
	/**
	 * 按钮点击模型上传到服务器
	 * 
	 * @param 按钮类型
	 * @param 点击时的时间戳
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * */
	public abstract void ButtonClick(String accountId, String buttonname);

	/**
	 * 当前页面的所处生命周期的上传方法
	 * @param ActivityName 
	 * @param ActivityLifeTime activity 所处的生命周期
	 * @param page 页面名
	 * 如果参数值无法得到，String 传递unknown，数值类型 传递-1.
	 * 
	 * */
	public abstract void ActivityLife(String accountId,String page,String action);
	
	
	/**activity 用户停留时间事件
	 * @param accountId 账户id
	 * @param time 停留时间
	 * @param name 页面名
	 * */
	public abstract void ActivityUserRunningTime(String accountId,int time,String name);
	
	/**
	 * 用真实货币购买虚拟币
	 * @param accountId 账号ID
	 * @param virtualCurrencyName 虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param amount 购买的虚拟币总量
	 * @param currencyType 支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	 * @param payAmount 支付总金额
	 */
	public void buyVirtualCurrency(String accountId, String virtualCurrencyName, int amount, TypeVirtualCurrency currencyType, 
			int payAmount){};
	
	/**
	 * 用现实货币购买礼包
	 * @param accountId 账号ID
	 * @param giftId 礼包ID
	 * @param currencyType 支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	 * @param payAmount 支付总金额
	 */
	public void buyGift(String accountId, String giftId, TypeVirtualCurrency currencyType, int payAmount){};
	
	/**
	 * 解开礼包
	 * @param accountId 账号ID
	 * @param giftId 礼包ID
	 * @param gainItems 礼包中包含的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 * @param gainVirtualCurrencies 礼包中包含的虚拟货币，每一个item为一个虚拟货币的JSONObject, 参见虚拟币对象
	 */
	public void openGift(String accountId, String giftId,ArrayList<VirtualItemType> gainItems,
			ArrayList<VirtualCoin> gainVirtualCurrencies){};
	
	/**
	 * 系统赠送虚拟币
	 * @param accountId 账号ID
	 * @param virtualCurrencyName 虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param amount 虚拟币数量
	 */
	public void sysGiveVC(String accountId, String virtualCurrencyName, int amount){};
	
	/**
	 * 用虚拟币购买虚拟物品
	 * @param accountId 账号ID
	 * @param virtualCurrencyName 虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param amount 虚拟币数量
	 * @param gainItems 购买的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 */
	public void buyVirtualItemsByVC(String accountId, String virtualCurrencyName,
			int amount, ArrayList<VirtualItemType> gainItems){};
	
	/**
	 * 用虚拟物品兑换虚拟币
	 * @param accountId 账号ID
	 * @param payVirtualItems 要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 * @param virtualCurrencyName 获得的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param gainAmount 获得的虚拟币数量
	 */
	public void exchangeVCbyVI(String accountId,ArrayList<VirtualItemType> payVirtualItems, String virtualCurrencyName,
			int gainAmount){};
	
	/**
	 * 用虚拟物品兑换取虚拟物品
	 * @param accountId 账号ID
	 * @param payVirtualItems 要兑换的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 * @param gainVirtualItems 获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 */
	public void exchangeVIbyVI(String accountId,ArrayList<VirtualItemType> payVirtualItems,ArrayList<VirtualItemType> gainVirtualItems){};
	
	/**
	 * 系统赠送虚拟物品
	 * @param accountId 账号ID 
	 * @param gainItems 获得的虚拟物品,每一个item为一个虚拟物品的JSONObject, 参见虚拟物品对象
	 */
	public void sysGiveVI(String accountId, ArrayList<VirtualItemType> gainItems){};
	
	/**
	 * 用现实货币购买虚拟物品
	 * @param accountId 账号ID 
	 * @param currencyType 支付币种，如：CNY(人民币)、USD(美元)、GBP(英镑)、EUR(欧元)等
	 * @param payAmount 支付总金额
	 * @param gainItems 获得的虚拟物品,每一个item为一个虚拟物品的JSONObject，参见虚拟物品对象
	 */
	public void buyVirtualItemsByRC(String accountId, TypeVirtualCurrency currencyType,
			int payAmount, ArrayList<VirtualItemType> gainItems){};
    /**
     * 虚拟物品消耗
     * @param accountId 账号ID 
     * @param virtualItemType 虚拟物品类型，参见虚拟物品类型
     * @param virtualItemName  虚拟物品名
     * @param virtualItemAmount 数量
     */
	public void consumeVirtualItem(String accountId, TypeVirtualItem virtualItemType,String virtualItemName, int virtualItemAmount){};
	
	/**
	 * 虚拟币兑换虚拟币
	 * @param accountId 账户ID
	 * @param payVirtualCurrencyName 支付的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param payAmount 支付的虚拟币数量
	 * @param gainVirtualCurrencyName 兑换得到的虚拟币名, 与具体游戏有关，如：宝石、月卡、金币（请采用拼音,不要使用中文）
	 * @param gainAmount 兑换得到的虚拟币数量
	 */
	public void exchangeVCbyVC(String accountId,String payVirtualCurrencyName, int payAmount,
			String gainVirtualCurrencyName, int gainAmount){};
	
	/**other 事件*/
	public void OtherEvent(String accountId,String otherEvent,String data){};
	
	public static boolean getIsInit(){
		if(!isInit){
			if(mActivity == null || mChannel == null){
				Log.e("Gamer", "请先初始化");
				return false;
			} 
			init(Gamer.mActivity, Gamer.mChannel, false);
		}
		return isInit;
	}
}