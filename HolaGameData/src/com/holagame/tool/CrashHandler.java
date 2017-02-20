package com.holagame.tool;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.holagame.global.Constant;
import com.holagame.model.ResponseModel;
import com.holagame.model.error.ErrorEvent;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 异常处理控制器,捕获没有被处理的未发现异常
 * 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler{
	/** 文件保存名称 */
	private static final String ERRORLOG_NAME = "ErrorLogs.cr";
	/** 错误日志记录类 */
	private ErrorEvent errorLog;
	/** 错误日志队列 */
	private ArrayList<String> logs;
	/** 网络上传类 */
	private NetWork network;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** 类名标志 */
	private static final String TAG = "CrashHandler";
	private static Activity mActivity;
	
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}
	
	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler init(Context context) {
		if (INSTANCE == null){
			INSTANCE = new CrashHandler();
			INSTANCE.initLog(context);
		}else{
			INSTANCE.sendPreviousReportsToServer();
		}
		return INSTANCE;
	}

	/**
	 * 加载本地错误日志 并且初始化错误队列logs--ArrayList<Object>
	 * @param ctx 上下文
	 */
	public void InitLogs(Context ctx) {
		String temp = HandleFile.loadFile(mContext, ERRORLOG_NAME);
		// 初始化消息队列
		if(TextUtils.isEmpty(temp)){
			logs = new ArrayList<String>();
		}else{
			try{
				logs = (ArrayList<String>) JSON.parseArray(temp, new String().getClass());
			}catch(Exception e){
				deletelog();
				logs = new ArrayList<String>();
			}
			
		}  
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置 该CrashHandler为程序的默认处理器
	 * 初始化消息队列和应用信息
	 * @param ctx 上下文
	 */
	public void initLog(Context ctx) {
		mContext = ctx.getApplicationContext();
		/** 初始化消息队列 */
		if (isLoadLogs()){
			InitLogs(mContext);
		}else{
			logs = new ArrayList<String>();
		}
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(ex == null){
			Logd.d(TAG, "异常捕获到数据为空，无法处理");
			ex.printStackTrace();
		}else if (!handleException(ex) && mDefaultHandler!= null) {  
			// 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);  
        }
		
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告
	 * @param ex 异常对象
	 * @return true 如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		saveCrashInfoToFile(ex);
		return false;
	}

	/**
	 * 保存错误信息到队列中，并且保存到文件
	 * 
	 * @param ex 异常对象
	 * 
	 */
	private void saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		// printStackTrace(PrintWriter s)
		// 将此 throwable 及其追踪输出到指定的 PrintWriter
		ex.printStackTrace(printWriter);
		// getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
	    printWriter.flush();
		printWriter.close();
		errorLog = new ErrorEvent();
//		errorLog.setPhoneModle(android.os.Build.MODEL);
//		errorLog.setPhoneVersion(android.os.Build.VERSION.SDK + ","+ android.os.Build.VERSION.RELEASE);
//		errorLog.setTitle(ex.toString().substring(0, ex.toString().indexOf(":")));
//		errorLog.setError(info.toString());
//		errorLog.setTs(System.currentTimeMillis()/1000);
		errorLog.setAccountId(Gamer.DATA_ACCOUNT_ID);
		errorLog.setEvent("error");
		errorLog.setEvent(info.toString());
		logs.add(JSON.toJSONString(errorLog));
		HandleFile.saveFile(logs, ERRORLOG_NAME, mContext);
	}
	
	/**
	 * 删除整个日志文件
	 * @param ctx 上下文
	 * @param name 文件名
	 * @return 删除是否成功
	 */
	public boolean deletelog() {
		try {
			File cr = new File(mContext.getFilesDir(), ERRORLOG_NAME);
			cr.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告,并且坚持是否
	 * 有要删除的数据
	 */
	public void sendPreviousReportsToServer() {
		//如果没有初始化，则先初始化
		if(!Gamer.getIsInit()){
			Gamer.init();
			return;
		}
		/** 先检查网络是否可用然后上传没有未上传的log */
		if(isNetworkAvailable(mContext) && logs.size()>0)
		{	
			String errlog = DataEvent.signalProduction.getSignal(logs);
			//如果没有sessionId，则清除记录
			if(!errlog.contains("sessionId")){
				HandleFile.saveDataLocal(Gamer.mActivity, "", "sendlogs.txt");
				return;
			}
			errlog = "data=" + Base64.encodeToString(errlog.getBytes(), Base64.DEFAULT);
			errlog.replaceAll("[+]", "%2B");
		    NetWork.getInstance(errlog, Constant.URL_ADD_BASE64, new AsyncHttpResponseHandler() {
			
		    @Override
			public void onSuccess(int arg0, @SuppressWarnings("deprecation") Header[] arg1, byte[] arg2) {
		    	Logd.e(TAG, "sendPreviousReportsToServer success");
				try {
					if(arg2!=null&&JSON.parseObject(new String(arg2),ResponseModel.class).getCode()==0)
					{
						logs = new ArrayList<String>();
						HandleFile.saveFile(logs, ERRORLOG_NAME, mContext);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		    @Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				try {
					Logd.d(TAG, "上传错误失败,statuscode"+arg0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		}
	}

	/**
	 * 判断是否要加载日志如果文件大于100K或者不存在将不加载 如果大于100k直接删除本地日志
	 * @return 是否要加载本地日志false不要加载 true加载
	 */
	private boolean isLoadLogs() {
		try{
			File file = new File(mContext.getFilesDir() + "/"+ERRORLOG_NAME);
			if (!file.exists())
				return false;
			else if (file.length() <= 0)
				return false;
			else if (file.length() > 102400)
				deletelog();
			return true;
		}catch(Exception e){
			Log.d("IlongSDK","isLoadLogs()发生异常");
			return false ;
		}

	}
	
	/**
	 * 网络是否可用
	 * @param context 上下文
	 * @return 网卡是否可用 true可用 false不可用
	 */  
    public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo[] info = mgr.getAllNetworkInfo();  
        if (info != null) {  
            for (int i = 0; i < info.length; i++) {  
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
                    return true;  
                }  
            }  
        }  
        return false;  
    }  
}