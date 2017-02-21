package com.qianhuan.yxgsd.holagames.ac;

import android.net.Uri;
import android.webkit.ValueCallback;

public interface WebCall {  
    void fileChose(ValueCallback<Uri> uploadMsg);  

    void fileChose5(ValueCallback<Uri[]> uploadMsg);  
} 