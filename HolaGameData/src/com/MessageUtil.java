package com;

public class MessageUtil {
	public native String encryptcbc(String en_msg);
	
	public native String encryptecb(String en_msg);
	

	static {
		try {
			System.loadLibrary("wbaes");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
}