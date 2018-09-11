package io.wexchain.android.dcc.tl;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.xxy.maple.tllibrary.activity.TlBrowserActivity;
import com.xxy.maple.tllibrary.app.TlSampleJavascript;
import com.xxy.maple.tllibrary.widget.TlX5WebView;

/**
 * Created by Gaoguanqi on 2018/5/24.
 */

public class TlWexchainJavascript extends TlSampleJavascript {
    
    private TlBrowserActivity mActivity;
    private TlX5WebView mWebView;
    private String mAddress;
    
    public TlWexchainJavascript(TlBrowserActivity activity, TlX5WebView webView, String address) {
        super(activity, webView, address);
        this.mActivity = activity;
        this.mWebView = webView;
        this.mAddress = address;
    }
    
    
    @NonNull
    private Intent createLaunchSignPayIntent(String data) {
        return new Intent().setPackage(mActivity.getPackageName()).setAction(TlPayActivity.ACTION_SIGN_TX)
                .putExtra("data", data);
    }
    
    @Override
    public void sendTransactionCreateData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity
                .CODE_H5_GOBORROWLOADING);
    }
    
    @Override
    public void sendTransactionMortgageData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity
                .CODE_H5_GOBORROWLOADING);
    }
    
    
    @Override
    public void sendTransactionLendData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity
                .CODE_H5_GOBORROWLOADING);
    }
    
    @Override
    public void sendTransactionRevertData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity
                .CODE_H5_GOBORROWLOADING);
    }
    
    @Override
    public void sendTransactionIndemnityData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity
                .CODE_H5_GOBORROWLOADING);
    }
    
    @Override
    public void sendTransactionCancleData(String data) {
        mActivity.launchActivityForResult(createLaunchSignPayIntent(data), TlBrowserActivity.CODE_H5_GOBORROWLOADING);
    }
}
