package com.holagame.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.holagames.xcds.IlongSDK;
import com.holagames.xcds.modle.UserXML;
import com.holagames.xcds.tools.IdcardValidatorTool;
import com.holagames.xcds.tools.UserNameValidatorTool;
import com.holagames.xcds.tools.http.Constant;

public class DeviceUtil {
	public static final String DATA_FIELD = "filed_user";
	public static final String KEY_UID = "key_uid";
	public static final String KEY_UPWD = "key_upwd";
	public static final String KEY_LOGOUT = "key_logout";
	public static final String KEY_USER_LIST = "key_user_list";
	
	
	/**这个 key是 游戏公告id 
	 */
	public static final String KEY_NOTICE_ID = "notice_id";
	
	public static final String SDK_VERSION="3.9.1";
	public static final String TAG = "DeviceUtil";
	
	public static final String USERINFONAME="data";
	private static final String USEREQUIPMENTCODE="EquipmentCode";
	private static final String APPLICATIONUSERINFO="applicationuserinfo";
	
	public static void saveData(Context c, String key, String value){
		try {
			//实例化SharedPreferences对象（第一步） 
			SharedPreferences mySharedPreferences= c.getSharedPreferences(DATA_FIELD, Activity.MODE_PRIVATE); 
			//实例化SharedPreferences.Editor对象（第二步） 
			SharedPreferences.Editor editor = mySharedPreferences.edit(); 
			//用putString的方法保存数据 
			editor.putString(key, value); 
			//提交当前数据 
			editor.commit();
			Logd.d(TAG, "Save data:" + key + ", " + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	public static int dip2px(Context context, float dipValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    } 
	
	public static boolean isLogout(Context c){
		if(getData(c, KEY_LOGOUT).equals("true")){
			return true;
		}
		return false;
	}
	
	public static void setLogout(Context mActivity, boolean isLogout){
		DeviceUtil.saveData(mActivity, DeviceUtil.KEY_LOGOUT, isLogout + "");
	}
	
	public static String getData(Context c, String key){
		try {
			//同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象 
			SharedPreferences sharedPreferences= c.getSharedPreferences(DATA_FIELD, Activity.MODE_PRIVATE); 
			// 使用getString方法获得value，注意第2个参数是value的默认值 
			String value =sharedPreferences.getString(key, ""); 
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取设备码流程，先试去longyuan文件夹下EquipmentCode.cr获取保存的设备码。
	 * 如果存在则不用使用该设备码，这个是防止设备码改变的情况下用户不能正常登录的问题。
	 * 如果没有获取到设备码，则系统产生设备码
	 * @param activity 当前的Activity 
	 * @return 设备码
	 */
	public static String getUniqueCode(Activity activity){
		String uniquecodename = "uniquecode";
		String Suffix=".cr";
		String uniqueCode="";
		File file=new File(getSecurityPath((Context)activity, USEREQUIPMENTCODE)+"/"+USEREQUIPMENTCODE+Suffix);
		try {
			if(! file.exists()){
				uniqueCode=produceUniqueCode(activity);
				file.createNewFile();
				PrintWriter pw = new PrintWriter(new FileWriter(file));  
				saveData(activity, uniquecodename, uniqueCode);
				pw.println(uniqueCode);
				pw.flush();
				pw.close();
			}
			else{
				 BufferedReader br = new BufferedReader(new FileReader(file)); 
			     uniqueCode=br.readLine();
			     br.close();
			     if(uniqueCode==null || uniqueCode.equals("")){
			    	 file.delete();
			    	 uniqueCode = getData(activity, uniquecodename);
			    	 Log.d(TAG, "get code from application");
			     }
			     if(uniqueCode==null || uniqueCode.equals("")){
			    	 uniqueCode=produceUniqueCode(activity);
			    	 Log.d(TAG, "create code");
			     }
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			uniqueCode=produceUniqueCode(activity);
		} 
		
        Log.d(TAG,"uniquecode:"+uniqueCode);
		return uniqueCode;
		
	}
	//产生设备码
	private static String produceUniqueCode(Activity activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	        String imei=tm.getDeviceId();

	        String simSerialNumber = "";
	        if(tm.getSimSerialNumber()!=null){
	            simSerialNumber=tm.getSimSerialNumber();
	        }

//	        String androidId =android.provider.Settings.Secure.getString(activity.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	        if(imei == null) imei = "";
	        String wifiInfo = getWifiInfo(activity);
	        if(wifiInfo == null) wifiInfo = "";
	        if(simSerialNumber == null) simSerialNumber = "";
	        
	        Logd.d("info", "wifi: " + wifiInfo + ", imei: " + imei + ", simSweianumber: " + simSerialNumber);
	        UUID deviceUuid = new UUID((long)wifiInfo.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());
	        Logd.d("uuid", deviceUuid.toString());
	        String uniqueId= deviceUuid.toString();
	        uniqueId = MD5(uniqueId);
	        if(IlongSDK.getInstance().getDebugMode()){
				DeviceUtil.appendToDebug("unique : " + uniqueId.toString());
			}
	        return uniqueId;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}

	public static String getSign(Context context) {
		  PackageManager pm = context.getPackageManager();
		  List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		  Iterator<PackageInfo> iter = apps.iterator();
		  while(iter.hasNext()) {
		       PackageInfo packageinfo = iter.next();
		       String packageName = packageinfo.packageName;
		       if (packageName.equals(context.getPackageName())) {
		         
		          return packageinfo.signatures[0].toCharsString();
		       }
		}
		  return "";
		}

	
	public static String getWifiInfo(Activity a){
		try {

			WifiManager wifi = (WifiManager) a.getSystemService(Context.WIFI_SERVICE); 
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getBasePath(Context context){
		//判断是否有存储卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String pathBase = Environment.getExternalStorageDirectory().getAbsolutePath() + "/longyuan";
			File file = new File(pathBase);
			if(! file.exists()) file.mkdirs();
			return pathBase;
		}else{
			String pathBase = context.getFilesDir().getAbsolutePath() + "/longyuan";
			File file = new File(pathBase);
			if(!file.exists()){
				file.mkdirs();
			}
			return pathBase;
		}
	}
	
	/**
	 * 在longyuan文件夹下创建文件夹
	 * @param context 上下文
	 * @param filename 文件夹名称
	 * @return 路径
	 */
	public static String getSecurityPath(Context context,String filename){
		String path = getBasePath(context) + "/"+filename;
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;		
	}
	
	private static String currentPath(Context context){
		return getSecurityPath(context,USERINFONAME) + "/" + IlongSDK.getInstance().getAppId();
	}
	
	public static String encodeData(String content){
		int MaxBlockSize=88;
		int datalength=content.length();
		int byteread = 0;
		StringBuilder data=new StringBuilder();
		if(datalength>MaxBlockSize )
		{
			for(int i=0;i<datalength/MaxBlockSize ;i++)
			{
				data.append(getencodeData(content.substring(byteread, (i+1)*MaxBlockSize))+"|");
				byteread=(i+1)*MaxBlockSize ;
			}
			data.append(getencodeData(content.substring(byteread, datalength)));
		}
		else
		return getencodeData(content);
		return data.toString();
	}
	public static String getencodeData(String content)
	{
		try {
			Cipher pkCipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			PublicKey publicKey = RsaUtilLoad.loadPublicKey(Constant.USER_PUBLIC_KEY);
	        pkCipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        byte[] yy = it.sauronsoftware.base64.Base64.encode(pkCipher.doFinal(content.getBytes("UTF-8")));
	        return new String(yy);
		} catch (Exception e) {
			e.printStackTrace();
			Logd.e(TAG, "encode data failed !!");
			return "";
		}
	}
	public static void deleteUserInfof(Context context){
		try {
			String path = currentPath(context);
			File file = new File(path);
			if(file.exists()){
				file.delete();
				if(IlongSDK.getInstance().getDebugMode())
					showToast((Activity)context, "debug 提示:注销成功，删除成功");
			}else{
				Logd.e(TAG, "file is not exit!!");
				if(IlongSDK.getInstance().getDebugMode())
					showToast((Activity)context, "debug提示：注销成功，文件不存在，不需要删除");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从文件读取用户信息
	 * @param context 上下文
	 * @return 用户信息
	 */
	private static HashMap<String, String> readUserFromFile(Context context,String filePath){
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			File file = new File(filePath);
			if(! file.exists()){
				Logd.e(TAG, "userinfo data is not exit !!");
				return map;
			}
			
	        BufferedReader br = new BufferedReader(new FileReader(file)); 
	        map.put(Constant.KEY_DATA_TYPE, br.readLine());
	        map.put(Constant.KEY_DATA_USERNAME, br.readLine());
	        map.put(Constant.KEY_DATA_CONTENT, br.readLine());
	        map.put(Constant.KEY_DATA_PWD, getPwdFromUser(context, map.get(Constant.KEY_DATA_USERNAME)));
	        br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	/**
	 * 读取用户步骤：
	 * 1.读取火拉文件夹下appid文件
	 * 2.从应用空间读取用户信息
	 * 3.从应用空间读取游客用户信息，如果以上三部得到的都为空。判定该用户是第一次安装该款游戏
	 * 4.从火拉文件夹下读取其它本公司用户信息。
	 * @param context 上下文
	 * @return 用户信息map
	 */
	public static HashMap<String, String> readUserFromFiles(Activity activity){
		HashMap<String, String> target = readUserFromFile(activity,currentPath(activity));
		if(target.size() != 0 && target.get(Constant.KEY_DATA_TYPE).equals(Constant.TYPE_USER_NORMAL)){
			return target;
		}
		target = readUserToApplication(activity);
		if(target.size()!=0){
			return target;
		}
		if(!IsTourist(activity)){
			return new HashMap<String, String>();
		}
		try {
			List<File> files = readFileFromLocal(activity);
			//文件夹不存在
			if(files ==null){
				target = new HashMap<String, String>();
				return target;
			} 
			
			for(int i = 0; i < files.size(); i++){
				File file = files.get(i);		
				if(file.getName().length() > 16){
					file.delete();
					continue;
				}
//				if(file.getName().length() != 16){
//					Logd.e("", file.getName() + ", " + file.getName().length());
//					continue;
//				}
				HashMap<String, String> map = readUserFromFile(activity, file.getPath());
		        String type = map.get(Constant.KEY_DATA_TYPE);
		        if(type == null || !type.equals(Constant.TYPE_USER_NORMAL)){
		        	Log.e("", "type is guest, continue");
		        	continue;
		        }
		        target = map;
		     // 文件内容的每一项，都不能为空
		        if(target!=null 
		        		&& !map.get(Constant.KEY_DATA_TYPE).equals("")
		        		&& !map.get(Constant.KEY_DATA_USERNAME).equals("")
		        		&& !map.get(Constant.KEY_DATA_CONTENT).equals(""))
		        	return target;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}
	/**
	 * 从火拉文件夹下读取所有的appid文件并且按照修改时间排序
	 * @param context 上下文
	 * @return 所有的用户信息
	 */
	private static List<File> readFileFromLocal(Activity context){
		String path = getSecurityPath(context,USERINFONAME);
		File root = new File(path);
		if(! root.exists()){
			Logd.e(TAG, "userinfo data is not exit !!");
			return null;
		}
		
		//排序  将日期较大的排前面
		List<File> files = Arrays.asList(root.listFiles());
		Collections.sort(files, new Comparator<File>() {

			@Override
			public int compare(File lhs, File rhs) {
				
				return (int) (rhs.lastModified() - lhs.lastModified());
			}
		});
		
		return files;
	}
    /**
     * 同步更新火拉文件夹下appid中的游客用户信息
     * @param userName 游客用户名
     * @param userInfo 用户信息
     * @param context 上下文
     */
	public static void updataUserInfoToLocal(String userName,HashMap<String, String> userInfo, Activity activity){
		List<File> files = readFileFromLocal(activity);
		for(File file:files){
			HashMap<String, String> temp = readUserFromFile(activity, file.getPath());
			if(temp.get(Constant.KEY_DATA_USERNAME).equals(userName)){
				writeInfoToLocal(userInfo, file.getPath());
			}
		}
	}
	
	/**
	 * 从应用控件读取数据
	 * @param context 上下文
	 */
	private static HashMap<String, String> readUserToApplication(Context context) {
		String userinfoString = getData(context, APPLICATIONUSERINFO);
		HashMap<String, String> userinfo = new HashMap<String, String>();
		if(userinfoString.equals("")){
			return userinfo;
		}else{
			userinfo = JSON.parseObject(userinfoString, new HashMap<String, String>().getClass());
			return userinfo;
		}
			
	}

	/**
	 * 从应用空间去读取游客信息
	 * @param context 上下文
	 */
	private static boolean IsTourist(Context context) {
		// TODO Auto-generated method stub
		String target = getData(context, IlongSDK.getInstance().getAppId());
		return target.equals("");
	}

	/**
	 * 路径为基础路径 + appid
	 * */
	public static void writeUserToFile(HashMap<String, String> map, Context context){
		try {
			if(map == null || !map.containsKey(Constant.KEY_DATA_TYPE) || 
					! map.containsKey(Constant.KEY_DATA_USERNAME) ||
					! map.containsKey(Constant.KEY_DATA_CONTENT)){
				Logd.e("lysdk", "error ! save map is error");
				Toast.makeText(context, "save map is error !", Toast.LENGTH_LONG).show();
				return;
			}
			String type = map.get(Constant.KEY_DATA_TYPE);
			String username = map.get(Constant.KEY_DATA_USERNAME);
			String content = map.get(Constant.KEY_DATA_CONTENT);
			String pwd = "";
			if(map.containsKey(Constant.KEY_DATA_PWD)) {
				pwd = map.get(Constant.KEY_DATA_PWD);
			}
			//写信息到本地文件
			writeInfoToLocal(map, currentPath(context));
			saveData(context, KEY_UID, username);
			saveUserToPrivate(context, username, pwd, type);
			/**将用户信息写入到应用空间*/
			saveData(context,APPLICATIONUSERINFO ,JSON.toJSONString(map));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 写信息到本地
	 * @param map 用户信息
	 * @param filepath 文件地址
	 */
	private static void writeInfoToLocal(HashMap<String, String> map,String filepath){
		try {
			String type = map.get(Constant.KEY_DATA_TYPE);
			String username = map.get(Constant.KEY_DATA_USERNAME);
			String content = map.get(Constant.KEY_DATA_CONTENT);	
			File file = new File(filepath);
			if(! file.exists()){
				file.createNewFile();
			}
			PrintWriter pw = new PrintWriter(new FileWriter(file));  
			pw.println(type);
			pw.println(username);
			pw.println(content);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**本地创建一个用户列表，并维护*/
	private static void saveUserToPrivate(Context context, String userName, String pwd, String type){
		try {
			//是否有相同的数据
			boolean hasData = false;
			ArrayList<UserXML> arr;
			String data = getData(context, KEY_USER_LIST);
			if(data == null || data.length() == 0){
				data = new JSONArray().toString();
			}
			//序列化本地用户信息
			arr = (ArrayList<UserXML>) JSON.parseArray(data, UserXML.class);
			for(int i = 0; i < arr.size(); i++){
				if(arr.get(i).getUserName().equals(userName)){
					if(pwd!=null&&!pwd.equals("")){
						arr.get(i).setPwd(pwd);
						arr.get(i).setType(type);
						hasData = true;
					}
					break;
				}
			}
			if(!hasData){
				arr.add(new UserXML(userName, pwd, type));
			}
			saveData(context, KEY_USER_LIST, JSON.toJSONString(arr));
		} catch (Exception e) {
			saveData(context, KEY_USER_LIST, "");
			e.printStackTrace();
		}
	}
	
	public static String getPwdFromUser(Context context, String userName){
		String pwd = "";
		try {
			String data = getData(context, KEY_USER_LIST);
			if(data == null || data.length() == 0) return "";
			JSONArray arr = new JSONArray(data);
			for(int i = 0; i < arr.length(); i++){
				org.json.JSONObject json = new JSONObject(arr.getString(i));
				if(json.getString("userName").equals(userName)){
					pwd = json.getString("pwd");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pwd;
	}
	
	//判断是否有用户存储信息
	public static boolean isDataEnable(Context context){
		File file = new File(currentPath(context));
		if(! file.exists()){
			return false;
		}else{
			return true;
		}
	}
	
	public static void showToast(final Activity context, final String msg){
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	public static void initDebug(){
		try {
			//如果不是debug模式 则退出
			if(! IlongSDK.getInstance().getDebugMode()){
				return;
			}
			File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/debug.text");
			if(file.exists()){
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void appendToDebug(String text){
		try {
			IlongSDK.debugInfo = IlongSDK.debugInfo + text + "\n\n";
			File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/debug.text");
			if(!file.exists()){
				file.createNewFile();
			}
			PrintWriter pw = new PrintWriter(new FileWriter(file));  
			pw.write(IlongSDK.debugInfo);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 清除应用控件目录
     * @param context 上下文
     */
	public static void clearApplicationData(Context context){
		//实例化SharedPreferences对象（第一步） 
		SharedPreferences mySharedPreferences= context.getSharedPreferences(DATA_FIELD, Activity.MODE_PRIVATE); 
		//实例化SharedPreferences.Editor对象（第二步） 
		SharedPreferences.Editor editor = mySharedPreferences.edit(); 
		editor.clear().commit();
	}
	
	/** 
     * 使用递归删除文件目录
     *  
     * @param deleteThisPath 文佳夹目录
     * @param filepath 是否删除
     * @return 
     */  
    public static void deleteFolderFile(String filePath, boolean deleteThisPath){  
        try {
			if (!TextUtils.isEmpty(filePath)) {  
			    File file = new File(filePath);  
  
			    if (file.isDirectory()) {// 处理目录  
			        File files[] = file.listFiles();  
			        for (int i = 0; i < files.length; i++) {  
			            deleteFolderFile(files[i].getAbsolutePath(), true);  
			        }  
			    }  
			    if (deleteThisPath) {  
			        if (!file.isDirectory()) {// 如果是文件，删除  
			            file.delete();  
			        } else {// 目录  
			            if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除  
			                file.delete();  
			            }  
			        }  
			    }  
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }  
 
//	public static void putUserName(Context context, String userName){
//		SharedPreferences mySharedPreferences = context.getSharedPreferences("longyuan_username",
//				Activity.MODE_PRIVATE);
//		// 实例化SharedPreferences.Editor对象（第二步）
//		SharedPreferences.Editor editor = mySharedPreferences.edit();
//		// 用putString的方法保存数据
//		editor.putString("username", userName);
//		// 提交当前数据
//		editor.commit();
//	}
//	
//	public static String getUserName(Context context){
//		//同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象 
//		SharedPreferences sharedPreferences= context.getSharedPreferences("longyuan_username", 
//		 Activity.MODE_PRIVATE); 
//		 // 使用getString方法获得value，注意第2个参数是value的默认值 
//		String name =sharedPreferences.getString("username", ""); 
//		return name;
//	}
    
    /**获取正在运行的Activity 的页面名
     * 要求 权限：android.permission.GET_TASKS
     * 返回 activity的全路径名
     * */
    public static String getRunningActivityName(Activity activity){	
    	if(activity != null){
    		Context	context = activity.getApplicationContext();
            ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
            String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName(); 
            if(!TextUtils.isEmpty(runningActivity)){
            	return runningActivity ;
            }else{
            	 return "unknown";  
            }
    	}else{
    		 return "unknown"; 
    	}                    
    } 
    
    public static String getIMEI(Context context){
   	 TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
   	 if(tm != null && !tm.getDeviceId().isEmpty()){
   	   	 return tm.getDeviceId();
   	 }else{
   		 return "unknown";
   	 }
   }
    
    
	/**获取手机设备生产商
	 * Build.MANUFACTURE 要求API是4.不影响*/
    @SuppressLint("NewApi")
	public static String getPhoneManufacturer(){
    	String manufacturer = Build.MANUFACTURER;;
    	if(!TextUtils.isEmpty(manufacturer)){
    		return manufacturer ;
    	}else{
    		return "unknown";
    	}
    }
    /**获取手机型号的参数*/
    public static String getPhoneBrand(){
    	String brand = Build.MODEL;
    	if(!TextUtils.isEmpty(brand)){
    		return brand ;
    	}else{
    		return "unknown";
    	}
    }
    
    /**获取手机真实的像素*/
	@SuppressLint("NewApi")
	public String getPx(Activity activity){
    	DisplayMetrics metric = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getRealMetrics(metric);  
    	int width = metric.widthPixels;
    	int height = metric.heightPixels;
    	String px =  height +"*"+width ;
    	if(!TextUtils.isEmpty(px)){
    		return px ;
    	}else{
    		return "unknown";
    	}
    }
		
	/**版本号*/
	public static String getVersionCode(Activity activity) {
		String osVersionCode = android.os.Build.VERSION.RELEASE ;
		if(!TextUtils.isEmpty(osVersionCode)){
			return osVersionCode ;
		}else{
			return  "unknown";
		}
	}
	
	 /** 
     * 得到当前的手机网络类型 
     *  方法中代码：  info.getSubtype(); 要求API最低是3。不影响SDK使用。
     * @param context 
     * @return 
     */  
    @SuppressLint("NewApi")
	public static String getPhoneNetWork(Context context) {  
        String type = "";  
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = cm.getActiveNetworkInfo();  
        if (info == null) {  
            type = "null";  
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {  
            type = "wifi";  
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {  
            int subType = info.getSubtype();  
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS  
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {  
                type = "2g";  
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA  
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0  
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {  
                type = "3g";  
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准  
                type = "4g";  
            }  
        }
        if(!TextUtils.isEmpty(type)){
        	Logd.d(TAG,"网络类型：-->"+type);
        	return type ;
        }else{
        	Logd.d(TAG,"网络类型：-->"+type);
        	return "unknown";
        }
        
    }  
    /**
     * 强px转化成当前屏幕的dip
     * @param context
     * @param pxvalue
     * @return
     */
    public static float convertpxTodip(Activity activity,float pxvalue)
    {
    	DisplayMetrics metric = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
    	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxvalue, metric);
     
    }
    /**
     * 将dip转化成px
     * @param context
     * @param dipvalue
     * @return
     */
    public static float convertdipTopx(Activity activity,float dipvalue)
    {
    	DisplayMetrics metric = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
    	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dipvalue, metric);
    }
    /**
     * 将sp转化成dp
     * @param activity 
     * @param value 要转化的值
     * @return 转化后的值
     */
    public static float convertspTopx(Activity activity,float value){
    	DisplayMetrics metric = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
    	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metric);
    }
    
    /**
     * 验证身份证是否合法
     * @param card 身份证
     * @return true合法 false不合法
     */
    public static boolean isValidatedAllIdcard(String card){
    	return new IdcardValidatorTool().isValidatedAllIdcard(card);
    }
    /**
     * 验证姓名的合法新
     * @param userName 姓名
     * @return true 合法姓名 false不合法姓名
     */
    public static boolean isValidateAllUserName(String userName){
    	return new UserNameValidatorTool().isValidatedAllUserName(userName);
    }
    
	/**
     * 获取状态栏高度
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity){
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

}




