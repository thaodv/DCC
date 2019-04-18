package io.wexchain.android.dcc.modules.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * @author Created by Wangpeng on 2019/4/10 10:57.
 * usage:
 */
@RequiresApi(Build.VERSION_CODES.M)
public class JavaScriptObjectUtils {
    private Activity mActivity;
    
    public JavaScriptObjectUtils(Activity context) {
        this.mActivity = context;
    }
    
    /**
     * 打开telegram
     */
    @JavascriptInterface
    public void openTelegram() {
        
        Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage("org.telegram.messenger");
        if (null == intent) {
            Toast.makeText(mActivity, "未安装", Toast.LENGTH_SHORT).show();
        } else {
            mActivity.startActivity(intent);
        }
        mActivity.finish();
    }
    
    @JavascriptInterface
    public void clientPay(String id){
        //new QrScannerPocketActivity().cashierContent(id);
    }
}
