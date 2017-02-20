package com.holagame.tool;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.holagame.em.DataType;
import com.holagame.em.TypeVirtualCurrency;
import com.holagame.em.TypeVirtualItem;
import com.holagame.model.PostDateModel;
import com.holagame.model.account.ExitModel;
import com.holagame.model.account.LoginModel;
import com.holagame.model.account.LogoutModel;
import com.holagame.model.account.MobileBindModel;
import com.holagame.model.account.RegisterModel;
import com.holagame.model.account.SetAccountTypeModel;
import com.holagame.model.account.SetAgeModel;
import com.holagame.model.account.SetGameServerModel;
import com.holagame.model.account.SetGenderModel;
import com.holagame.model.account.SetLevelModel;
import com.holagame.model.coin.BuyGift;
import com.holagame.model.coin.BuyVirtualCurrency;
import com.holagame.model.coin.BuyVirtualItemsByRC;
import com.holagame.model.coin.BuyVirtualItemsByVC;
import com.holagame.model.coin.ConsumeVirtualItem;
import com.holagame.model.coin.ExchangeVCbyVC;
import com.holagame.model.coin.ExchangeVCbyVI;
import com.holagame.model.coin.ExchangeVIbyVI;
import com.holagame.model.coin.OpenGift;
import com.holagame.model.coin.SysGiveVC;
import com.holagame.model.coin.SysGiveVI;
import com.holagame.model.coin.VirtualCoin;
import com.holagame.model.coin.VirtualItemType;
import com.holagame.model.level.LevelsBegin;
import com.holagame.model.level.LevelsCompleteModel;
import com.holagame.model.level.LevelsFailModel;
import com.holagame.model.operate.ActivityLifeModel;
import com.holagame.model.operate.ActivityTimeModel;
import com.holagame.model.operate.OtherEvent;
import com.holagame.model.operate.SDKButtonClickModel;

public class GamerCenter extends Gamer{
	private String Tag = this.getClass().getSimpleName();
	/**等待上传的消息队列*/
	public  ArrayList<String> Waiting_Queue_Signal;
	public  ArrayList<String> sending_Queue_Signal;
	public static final String LoclQueueName = "Collection_GamerCenter_Queue";
	private static GamerCenter gamerCenter = null;
	
	
	public GamerCenter() {
		super();
	}

	public static GamerCenter Init(Context context){
		if(gamerCenter == null){
			gamerCenter = new GamerCenter(context);
		}
		return gamerCenter;
	}
	
	public static GamerCenter getInstance(){
		if(gamerCenter == null){
			gamerCenter = new GamerCenter();
			Logd.e("GamerCenter", "请先初始化");
		}
		return gamerCenter;
	}
	
	public GamerCenter(Context context){
		if(gamerCenter ==null){
			Waiting_Queue_Signal = new ArrayList<String>();
			sending_Queue_Signal = new ArrayList<String>();
			loadingWaitingQueueSignal(context);
		}else{
			Logd.e(Tag, "改对象你以初始化过了，请使用GetInit得到该方法的实例对象");
		}
	}
	
	@Override
	public void Register(String accountId, String accountType, String gender,
			String email, String phone) {
		Logd.d(Tag, "register calling..");
		try {
			if(getIsInit()){
				RegisterModel register = new RegisterModel(accountId, accountType, gender, email, phone, Gamer.getTime());
				String[] notValid = {"gender","email","phone"};
				if(!DeviceUtil.isValidObject(register, notValid)) return;
				String queueItem = getQueueItem(register);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建Register数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void Login(String accountId) {
		try {
			Logd.d(Tag, "login calling..");
			DATA_ACCOUNT_ID = accountId;
			if(getIsInit()){
				LoginModel loginModel = new LoginModel(accountId, Gamer.getTime());
				if(!DeviceUtil.isValidObject(loginModel, null)) return;
				String queueItem = getQueueItem(loginModel);
				addData(queueItem);
				
			}else{
				Logd.d(Tag, "创建Login数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void setAccountType(String accountId, String accountType) {
		Logd.d(Tag, "setAccountType calling...");
		try {
			if(getIsInit()){
				SetAccountTypeModel setAccountType = new SetAccountTypeModel(accountId,accountType, Gamer.getTime());
				if(!DeviceUtil.isValidObject(setAccountType, null)) return;
				String queueItem = getQueueItem(setAccountType);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建setAccountType数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void setGender(String accountId, int gender) {
		Logd.d(Tag, "setGender calling..");
		try {
			if(getIsInit()){
				SetGenderModel setGender = new SetGenderModel(accountId, gender, Gamer.getTime());
				if(!DeviceUtil.isValidObject(setGender, null)) return;
				String queueItem = getQueueItem(setGender);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建setGender数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void setAge(String accountId, int age) {
		Logd.d(Tag, "setAge calling...");
		try {
			if(getIsInit()){
				SetAgeModel setage = new SetAgeModel(accountId, age, Gamer.getTime());
				if(!DeviceUtil.isValidObject(setage, null)) return;
				String queueItem = getQueueItem(setage);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建setAge数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void setLevel(String accountId, int level) {
		Logd.d(Tag, "setLevel calling...");
		try {
			if(getIsInit()){
				SetLevelModel setlevel = new SetLevelModel(accountId, level, Gamer.getTime());
				if(!DeviceUtil.isValidObject(setlevel, null)) return;
				String queueItem = getQueueItem(setlevel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建setLevel数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void setGameServer(String accountId, String gameServer) {
		Logd.d(Tag, "setGameServer calling...");
		try {
			if(accountId == null || gameServer == null) return;
			PostDateModel.gameArea = gameServer;
			if(getIsInit()){
				SetGameServerModel gameServerModel = new SetGameServerModel(accountId, gameServer,  Gamer.getTime()); 
				if(!DeviceUtil.isValidObject(gameServerModel, null)) return;
				String queueItem = getQueueItem(gameServerModel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建setGameServer数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void MobileBind(String accountId, String mobile, boolean isok) {
		Logd.d(Tag, "mobileBind calling..");
		try {
			if(getIsInit()){
				MobileBindModel mobilebind = new MobileBindModel(accountId, mobile,  Gamer.getTime(), isok);
				if(!DeviceUtil.isValidObject(mobilebind, null)) return;
				String queueItem = getQueueItem(mobilebind);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建mobileBind数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void logout(String accountId) {
		Logd.d(Tag, "logout calling...");
		try {
			if(getIsInit()){
				LogoutModel logoutModel = new LogoutModel(accountId, Gamer.getTime());
				if(!DeviceUtil.isValidObject(logoutModel, null)) return;
				String queueItem = getQueueItem(logoutModel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建logout数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void ExitEvent(String accountId) {
		Logd.d(Tag, "exitEvent calling...");
		try {
			if(getIsInit()){
				ExitModel exit = new ExitModel(accountId, Gamer.getTime());
				if(!DeviceUtil.isValidObject(exit, null)) return;
				String queueItem = getQueueItem(exit);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建ExitEvent数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void gameBegin(String accountId, String levelId) {
		Logd.d(Tag, "gameBegin calling...");
		try {
			if(getIsInit()){
				LevelsBegin gameLevelModel = new LevelsBegin(accountId, levelId, Gamer.getTime());
				if(!DeviceUtil.isValidObject(gameLevelModel, null)) return;
				String queueItem = getQueueItem(gameLevelModel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建gameBegin数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void gameComplete(String accountId, String levelId) {
		Logd.d(Tag, "gameComplete calling...");
		try {
			if(getIsInit()){
				LevelsCompleteModel gameLevelModel = new LevelsCompleteModel(accountId, levelId, Gamer.getTime());
				if(!DeviceUtil.isValidObject(gameLevelModel, null)) return;
				String queueItem = getQueueItem(gameLevelModel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建gameComplete数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void gameFail(String accountId, String levelId, String reason) {
		Logd.d(Tag, "gameFail calling...");
		try {
			if(getIsInit()){
				LevelsFailModel gameLevelFailModel = new LevelsFailModel(accountId, levelId, reason, Gamer.getTime());
				if(!DeviceUtil.isValidObject(gameLevelFailModel, null)) return;
				String queueItem = getQueueItem(gameLevelFailModel);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建gameFail数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void ButtonClick(String accountId, String buttonname) {
		Logd.d(Tag, "SDKButtonClick calling...");
		try {
			if (getIsInit()) {
				SDKButtonClickModel buttonClickModel = new SDKButtonClickModel(accountId, buttonname,Gamer.getTime());
				if(!DeviceUtil.isValidObject(buttonClickModel, null)) return;
				String queueItem = getQueueItem(buttonClickModel);
				addData(queueItem);
			} else {
				Logd.d(Tag, "创建--SDKButtonClick--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void ActivityLife(String accountId, String page, String action) {
		Logd.d(Tag, "ActivityLife calling...");
		try {
			if (getIsInit()) {
				ActivityLifeModel activityDateModel = new ActivityLifeModel(accountId, page, action,Gamer.getTime());
				if(!DeviceUtil.isValidObject(activityDateModel, null)) return;
				String queueItem = getQueueItem(activityDateModel);
				addData(queueItem);
			} else {
				Logd.d(Tag, "创建--ActivityLife--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void ActivityUserRunningTime(String accountId, int time, String name) {
		Logd.d(Tag, "ActivityUserRunningTime calling...");
		try {
			if(getIsInit()){
				ActivityTimeModel activityTime = new ActivityTimeModel(accountId, time, name,Gamer.getTime());
				if(!DeviceUtil.isValidObject(activityTime, null)) return;
				String queueItem = getQueueItem(activityTime);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--ActivityUserRunningTime--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void buyVirtualItemsByVC(String accountId, String virtualCurrencyName,
			int amount, ArrayList<VirtualItemType> gainItems){
		Logd.d(Tag, "buyVirtualItemsByVC calling...");
		try {
			if(getIsInit()){
				BuyVirtualItemsByVC bvc = new BuyVirtualItemsByVC(accountId, virtualCurrencyName, amount, gainItems, getTime());
				if(!DeviceUtil.isValidObject(bvc, null)) return;
				String queueItem = getQueueItem(bvc);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--getGoinByCurrency--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void buyVirtualCurrency(String accountId,String virtualCurrencyName, int amount,
			TypeVirtualCurrency currencyType, int payAmount) {
		Logd.d(Tag, "buyVirtualCurrency calling...");
		try {
			if(getIsInit()){
				BuyVirtualCurrency bvc = new BuyVirtualCurrency(accountId, virtualCurrencyName, amount, currencyType, payAmount, getTime());
				if(!DeviceUtil.isValidObject(bvc, null)) return;
				String queueItem = getQueueItem(bvc);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--getGoinByCurrency--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void buyGift(String accountId, String giftId,
			TypeVirtualCurrency currencyType, int payAmount) {
		Logd.d(Tag, "buyGift calling...");
		try {
			if(getIsInit()){
				BuyGift bg = new BuyGift(accountId, giftId, currencyType, payAmount, getTime());
				String queueItem = getQueueItem(bg);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--getGiftByCurrency--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void openGift(String accountId, String giftId,ArrayList<VirtualItemType> gainItems,
			ArrayList<VirtualCoin> gainVirtualCurrencies) {
		Logd.d(Tag, "openGift calling...");
		try {
			if(getIsInit()){
				OpenGift bg = new OpenGift(accountId, giftId, gainItems, gainVirtualCurrencies,getTime());
				if(!DeviceUtil.isValidObject(bg, null)) return;
				String queueItem = getQueueItem(bg);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--openGift--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sysGiveVC(String accountId, String virtualCurrencyName,
			int amount) {
		// TODO Auto-generated method stub
		Logd.d(Tag, "sysGiveVC calling...");
		try {
			if(getIsInit()){
				SysGiveVC sgv = new SysGiveVC(accountId, virtualCurrencyName, amount, getTime());
				if(!DeviceUtil.isValidObject(sgv, null)) return;
				String queueItem = getQueueItem(sgv);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--sysGiveVC--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void exchangeVCbyVI(String accountId,ArrayList<VirtualItemType> payVirtualItems,
			String virtualCurrencyName, int gainAmount) {
		Logd.d(Tag, "exchangeVCbyVI calling...");
		try {
			if(getIsInit()){
				ExchangeVCbyVI ev = new ExchangeVCbyVI(accountId, payVirtualItems, virtualCurrencyName, gainAmount, getTime());
				if(!DeviceUtil.isValidObject(ev, null)) return;
				String queueItem = getQueueItem(ev);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--exchangeVCbyVI--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void exchangeVIbyVI(String accountId,ArrayList<VirtualItemType> payVirtualItems,
			ArrayList<VirtualItemType> gainVirtualItems) {
		Logd.d(Tag, "exchangeVIbyVI calling...");
		try {
			if(getIsInit()){
				ExchangeVIbyVI ev = new ExchangeVIbyVI(accountId, payVirtualItems, gainVirtualItems, getTime());
				if(!DeviceUtil.isValidObject(ev, null)) return;
				String queueItem = getQueueItem(ev);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--exchangeVIbyVI--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sysGiveVI(String accountId, ArrayList<VirtualItemType> gainItems) {
		Logd.d(Tag, "sysGiveVI calling...");
		try {
			if(getIsInit()){
				SysGiveVI sv = new SysGiveVI(accountId, gainItems,getTime());
				if(!DeviceUtil.isValidObject(sv, null)) return;
				String queueItem = getQueueItem(sv);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--sysGiveVI--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void buyVirtualItemsByRC(String accountId,
			TypeVirtualCurrency currencyType, int payAmount,
			ArrayList<VirtualItemType> gainItems) {
		Logd.d(Tag, "buyVirtualItemsByRC calling...");
		try {
			if(getIsInit()){
				BuyVirtualItemsByRC bb = new BuyVirtualItemsByRC(accountId, currencyType, payAmount, gainItems,getTime());
				if(!DeviceUtil.isValidObject(bb, null)) return;
				String queueItem = getQueueItem(bb);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--buyVirtualItemsByRC--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void consumeVirtualItem(String accountId, TypeVirtualItem virtualItemType,
			String virtualItemName, int virtualItemAmount) {
		Logd.d(Tag, "consumeVirtualItem calling...");
		try {
			if(getIsInit()){
				ConsumeVirtualItem cvi = new ConsumeVirtualItem(accountId, virtualItemType, virtualItemName, virtualItemAmount, getTime());
				if(!DeviceUtil.isValidObject(cvi, null)) return;
				String queueItem = getQueueItem(cvi);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--consumeVirtualItem--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void exchangeVCbyVC(String accountId, String payVirtualCurrencyName,
			int payAmount, String gainVirtualCurrencyName, int gainAmount) {
		// TODO Auto-generated method stub
		Logd.d(Tag, "exchangeVCbyVC calling...");
		try {
			if(getIsInit()){
				ExchangeVCbyVC evc = new ExchangeVCbyVC(accountId, payVirtualCurrencyName, payAmount, gainVirtualCurrencyName, gainAmount, getTime());
				if(!DeviceUtil.isValidObject(evc, null)) return;
				String queueItem = getQueueItem(evc);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--exchangeVCbyVC--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void OtherEvent(String accountId, String otherEvent, String data) {
		// TODO Auto-generated method stub
		Logd.d(Tag, "OtherEvent calling...");
		try {
			if(getIsInit()){
				OtherEvent other = new OtherEvent(accountId, otherEvent, data, getTime());
				String queueItem = getQueueItem(other);
				addData(queueItem);
			}else{
				Logd.d(Tag, "创建--OtherEvent--数据模型失败，init没有初始化");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void sendSuccess(){
		sending_Queue_Signal = null;
	}
	@Override
	public void sendFail(){
		Waiting_Queue_Signal.addAll(0,sending_Queue_Signal);
		sending_Queue_Signal = null;
	}
	
	@Override
	public void addData(String data){
		Waiting_Queue_Signal.add(data);
		addCollection();
	}
	@Override
	public void loadingWaitingQueueSignal(Context context) {
		String localQueue = DeviceUtil.getData(context, LoclQueueName);
		if(TextUtils.isEmpty(localQueue)){
			return;
		}
		else{
			try{
				Waiting_Queue_Signal = (ArrayList<String>) JSON.parseArray(localQueue, new String().getClass());
			}catch(Exception e){
				e.printStackTrace();
				DeviceUtil.saveData(context, LoclQueueName, "");
				Waiting_Queue_Signal = new ArrayList<String>();
			}
			
		}
	}
	
	@Override
	public void Send() {
		if(Waiting_Queue_Signal.size() == 0) return;
		sending_Queue_Signal = Waiting_Queue_Signal;
		Waiting_Queue_Signal = new ArrayList<String>();
		sendData(sending_Queue_Signal, mActivity,DataType.GAME);
	}

	@Override
	public void saveLocal() {
		if(Waiting_Queue_Signal == null) return;
		DeviceUtil.saveData(mActivity, LoclQueueName, JSON.toJSONString(Waiting_Queue_Signal));
	}
	
	public int getQueueCount(){
		if(Waiting_Queue_Signal==null) return 0;
		return Waiting_Queue_Signal.size();
	}
	
}
