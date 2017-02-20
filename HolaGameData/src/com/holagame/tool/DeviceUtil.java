package com.holagame.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.holagame.global.Constant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

public class DeviceUtil {
	
	
	public static final String DATA_FIELD = "filed_user";
	public static final String KEY_UID = "key_uid";
	public static final String KEY_UTYPE = "key_utype";
	public static final String KEY_UPWD = "key_upwd";
	
	public static final String KEY_FOULD_LONGYUAN = "/longyuan";
	public static final String KEY_FOULD_DATA = "/gamedata";
	public static final String KEY_FOULD_SDK = "/sdk";
	
	public static String KEY_FOULD_IN = KEY_FOULD_DATA;
	
	static{
		if(Constant.MODEL_DATA){
			KEY_FOULD_IN = KEY_FOULD_DATA;
		}else{
			KEY_FOULD_IN = KEY_FOULD_SDK;
		}
	}
	
	/**
	 * 这个 key是 游戏公告id
	 */
	public static final String KEY_NOTICE_ID = "notice_id";
	
	public static final String SDK_VERSION="3.7.0";
	public static final String TAG = "DeviceUtil";
	public static String DevId="";
	private static final String USEREQUIPMENTCODE="EquipmentCode";
	
	public static void saveData(Context c, String key, String value){
		try {
			// 实例化SharedPreferences对象（第一步）
			SharedPreferences mySharedPreferences= c.getSharedPreferences(DATA_FIELD, Activity.MODE_PRIVATE); 
			// 实例化SharedPreferences.Editor对象（第二步）
			SharedPreferences.Editor editor = mySharedPreferences.edit(); 
			// 用putString的方法保存数据
			editor.putString(key, value); 
			// 提交当前数据
			editor.commit();
			Logd.d(TAG, key + ", " + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	

	public static String getData(Context c, String key){
		try {
			// 同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
			SharedPreferences sharedPreferences= c.getSharedPreferences(DATA_FIELD, Activity.MODE_PRIVATE); 
			// 使用getString方法获得value，注意第2个参数是value的默认值
			String value =sharedPreferences.getString(key, ""); 
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getIMEI(Context activity){
		String imei = "";
		try {
			TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

	        imei=tm.getDeviceId();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}
	
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
			       if(uniqueCode==null || uniqueCode.equals("")){
			    	   file.delete();
			    	   uniqueCode=getData(activity, uniquecodename);
			       }else if(uniqueCode==null || uniqueCode.equals("")){
			    	   uniqueCode=produceUniqueCode(activity);
			       }
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uniqueCode=produceUniqueCode(activity);
		} 
		
        Logd.d(TAG,"uniquecode:"+uniqueCode);
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
	        
	        UUID deviceUuid = new UUID((long)wifiInfo.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());
	        String uniqueId= deviceUuid.toString();
	        uniqueId = MD5(uniqueId);
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
		// 判断是否有存储卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String pathBase = Environment.getExternalStorageDirectory().getAbsolutePath() + KEY_FOULD_LONGYUAN;
			File file = new File(pathBase);
			if(! file.exists()) file.mkdirs();
			return pathBase;
		}else{
			String pathBase = context.getFilesDir().getAbsolutePath() + KEY_FOULD_LONGYUAN;
			File file = new File(pathBase);
			if(!file.exists()){
				file.mkdirs();
			}
			return pathBase;
		}
	}
	
	public static String getSecurityPath(Context context){
		String path = getBasePath(context) + KEY_FOULD_IN;
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;		
	}
	
	public static String getSecurityPath(Context context,String filename){
		String path = getBasePath(context) + "/"+filename;
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;		
	}
	
	
	/**
	 * 获取系统时间
	 */
	public static String getSystemTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date(Gamer.getTime());// 获取当前时间
		return formatter.format(curDate);
	}
	
	public static void showToast(final Activity context, final String msg){
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	


	/** 
	 * 获取手机ip地址 
	 *  
	 * @return 
	 */  
	public static String getPhoneIp() {  
	    try {  
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
	            NetworkInterface intf = en.nextElement();  
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                InetAddress inetAddress = enumIpAddr.nextElement();  
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
	                	String ip = inetAddress.getHostAddress().toString();
	                	if(!TextUtils.isEmpty(ip)){
	                		Logd.d(TAG,"获取ip-->"+ip);
	                		return ip ;
	                	}else{
	                		return "unknown";
	                	}
	                     
	                }  
	            }  
	        }  
	    } catch (Exception e) {
	    	Logd.d(TAG,"getPhoneIp()中发生异常");
	    	 return "unknown"; 
	    }  
	    return "unknown"; 
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
	/**获取手机设备生产商
	 * Build.MANUFACTURE 要求API是4.不影响*/
    @SuppressLint("NewApi")
	public static String getPhoneManufacturer(){
    	String manufacturer = Build.MANUFACTURER;;
    	if(!TextUtils.isEmpty(manufacturer)){
    		Logd.d(TAG,"厂商信息：-->"+manufacturer);
    		return manufacturer ;
    	}else{
    		return "unknown";
    	}
    }
    /**获取手机型号的参数*/
    public static String getPhoneBrand(){
    	String brand = Build.MODEL;
    	if(!TextUtils.isEmpty(brand)){
    		Logd.d(TAG,"品牌信息：-->"+brand);
    		return brand ;
    	}else{
    		return "unknown";
    	}
    }
    
    /**获取用户设备运营商*/
    /** 
     * Role:Telecom service providers获取手机服务商信息 <BR> 
     * 需要加入权限<uses-permission 
     * android:name="android.permission.READ_PHONE_STATE"/> <BR> 
     * Date:2012-3-12 <BR> 
     *  
     * @author CODYY)peijiangping 
     */  
    public static String getSIMResolution(Context context) {  
        String ProvidersName = null;  
        TelephonyManager telephonyManager = (TelephonyManager) context  
                .getSystemService(Context.TELEPHONY_SERVICE);  
        // 返回唯一的用户ID;就是这张卡的编号神马的  
        String IMSI = telephonyManager.getSubscriberId();
        if(!TextUtils.isEmpty(IMSI)){
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。  
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {  
                ProvidersName = "中国移动";  
            } else if (IMSI.startsWith("46001")) {  
                ProvidersName = "中国联通";  
            } else if (IMSI.startsWith("46003")) {  
                ProvidersName = "中国电信";  
            } 
        }else{
        	return "unknown";
        }
        if(!TextUtils.isEmpty(ProvidersName)){
        	Logd.d(TAG,"运营商信息：-->"+ProvidersName);
        	return ProvidersName;
        }else{
        	return "unknown"; 
        }
         
    }  
    
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
    
    /**
     * 利用反射的方法判断对象中的属性是否为空
     * @param obj 被判断对象
     * @param notValid 过滤数组
     * @return false 有属性为空 true无属性为空
     */
    public static boolean isValidObject(Object obj, String[] notValid) {
    	if(obj == null){
    		Logd.e(TAG, "反射判断对象为空被判断对象为："+obj.getClass().getSimpleName());
    		return false;
    	}
		try {
			Class<?> cls = obj.getClass();
			Method methods[] = cls.getDeclaredMethods();
			Field fields[] = cls.getDeclaredFields();
			for (Field field : fields) {
				//属性类型
				String fldtype = field.getType().getSimpleName();
				//方法名称
				String getMetName = pareGetName(field.getName());
				String result = "";
				if (!checkMethod(methods, getMetName)) {
					continue;
				}
				Method method = cls.getMethod(getMetName, null);
				Object object = method.invoke(obj, new Object[] {});
				if (null == object) {
					if(notValid!=null && isValue(notValid, field.getName())){
						continue;
					}
					Logd.e(TAG, "数据上传添加数据失败："+obj.getClass().getSimpleName()+"类属性"+field.getName()+"值为空");
					return false;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logd.e(TAG, "反射判断对象值是否为空失败判断对象为："+obj.getClass().getSimpleName());
		} 
		return true;
	}

	public static boolean isValue(String[] values,String value){
		for(String temp:values){
			if(temp.equals(value)) return true;
		}
		return false;
	}
	/**
	 * 判断该方法是否存在
	 * 
	 * @param methods
	 * @param met
	 * @return
	 */
	public static boolean checkMethod(Method methods[], String met) {
		if (null != methods) {
			for (Method method : methods) {
				if (met.equals(method.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 拼接某属性get 方法
	 * 
	 * @param fldname
	 * @return
	 */
	public static String pareGetName(String fldname) {
		if (null == fldname || "".equals(fldname)) {
			return null;
		}
		String pro = "get" + fldname.substring(0, 1).toUpperCase()
				+ fldname.substring(1);
		return pro;
	}
}




