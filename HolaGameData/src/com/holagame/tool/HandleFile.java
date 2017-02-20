package com.holagame.tool;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.alibaba.fastjson.JSON;

public class HandleFile {
	/**
	 * 保存文件信息工具类
	 * 
	 * @param touristInfo 游客信息
	 */
	public static void saveFile(Object object,String filename,Context context) {
		// TODO Auto-generated method stub
		try {
			FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
			outStream.write(JSON.toJSONString(object).getBytes());
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取文件信息工具类
	 * @param context 上下文
	 * @return 获取文件的字符串
	 */
	public static String loadFile(Context context,String filename){
		try {
			FileInputStream inStream = context.openFileInput(filename);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();
			return stream.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
     * 将发送的数据保存到本地SD卡
     * @param sendingData 发送的数据
     */
	public static void saveDataLocal(Context context,String data,String fileName) {
		String filePath = DeviceUtil.getSecurityPath(context,"log");
		File logFile = new File(filePath+File.separatorChar+fileName);
		try {
			if(!logFile.exists()){
			logFile.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true),"UTF-8"));
			out.write(DeviceUtil.getSystemTime()+"\n");  
			out.write(data+"\n");
			out.write("---------------------------分割线-----------------------------"+"\n"+"\n");
			out.flush();
			out.close();
		} catch (Exception e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
}
