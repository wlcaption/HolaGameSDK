package com.holagame.tool;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.holagame.em.DataType;
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
import com.holagame.model.operate.ActivityLifeModel;
import com.holagame.model.operate.ActivityTimeModel;
import com.holagame.model.operate.SDKButtonClickModel;

public class SDKCenter extends Gamer{

	private String Tag = this.getClass().getSimpleName();
	
	/**等待上传的消息队列*/
	public  ArrayList<String> Waiting_Queue_Signal;
	public  ArrayList<String> sending_Queue_Signal;
	public static final String LoclQueueName = "Collection_SDKCenter_Queue";
	private static SDKCenter sdkCenter = null;
	public SDKCenter(Context context){
		if(sdkCenter == null){
			Waiting_Queue_Signal = new ArrayList<String>();
			sending_Queue_Signal = new ArrayList<String>();
			loadingWaitingQueueSignal(context);
		}else{
			Logd.e(Tag, "该对象以被初始化，请使用GetInit方法得到该对象");
		}
	}
	
	public static SDKCenter Init(Context context){
		if(sdkCenter == null){
			sdkCenter = new SDKCenter(context);
		}
		return sdkCenter;
	}
	
	public static SDKCenter getInstance(){
		if(sdkCenter == null){
			Logd.e("SDKCenter", "请先初始化");
		}
		return sdkCenter;
	}
	@Override
	public void Register(String accountId, String accountType, String gender,
			String email, String phone) {
		// TODO Auto-generated method stub
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
		}else{
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
		sendData(sending_Queue_Signal, mActivity,DataType.LONGYUAN);
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
