package com.holagames.xcds.ac;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/** 
 * Created by tangbin on 16/5/12. 
 */  
public class MyWebChromeClient extends WebChromeClient{  
	
	public static final String TAG = "MyWebClient";
	
	private WebCall webCall; 
	
	public MyWebChromeClient(WebCall webCall){
		this.webCall = webCall;
	}
	
	
    public void setWebCall(WebCall webCall) {  
        this.webCall = webCall;  
    }  
  
    // For Android 3.0+  
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {  
    	Log.e(TAG, "openFileChooser > for 3.0+");
        if (webCall != null)  
            webCall.fileChose(uploadMsg);  
    }  
  
    // For Android < 3.0  
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {  
    	Log.e(TAG, "openFileChooser > < 3.0");
        openFileChooser(uploadMsg, "");  
    }  
  
    // For Android > 4.1.1  
    public void openFileChooser(ValueCallback<Uri> uploadMsg,  
            String acceptType, String capture) { 
    	Log.e(TAG, "openFileChooser > 4.1.1");
        openFileChooser(uploadMsg, acceptType);  
    }  
  
    // For Android > 5.0  
    @SuppressLint("NewApi")
	@Override  
    public boolean onShowFileChooser(WebView webView,  
            ValueCallback<Uri[]> filePathCallback,  
            FileChooserParams fileChooserParams) {  
    	Log.e(TAG, "onShowFileChooser > 5.0");
        if (webCall != null)  
            webCall.fileChose5(filePathCallback);  
        return true;
//        return super.onShowFileChooser(webView, filePathCallback,  fileChooserParams);  
    }  
  
    
    
    
    
  
} 