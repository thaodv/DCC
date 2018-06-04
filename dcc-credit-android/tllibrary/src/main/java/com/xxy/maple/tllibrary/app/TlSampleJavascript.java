package com.xxy.maple.tllibrary.app;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.xxy.maple.tllibrary.utils.LogUtils;
import com.xxy.maple.tllibrary.widget.TlX5WebView;

import java.util.Locale;

/**
 * Created by Gaoguanqi on 2018/5/24.
 */

public abstract class TlSampleJavascript {


    private Activity mActivity;
    private TlX5WebView mWebView;
    private String mAddress;

    /**
     *
     * @param activity
     * @param webView
     * @param address  放在request  header 里当作token 用
     */
    public TlSampleJavascript(Activity activity, TlX5WebView webView, String address) {
        this.mActivity = activity;
        this.mWebView = webView;
        this.mAddress = address;
    }

    @JavascriptInterface
    public String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if(language.equalsIgnoreCase("zh")){
            return "cn";
        }else {
            return "en";
        }
    }

    @JavascriptInterface
    public void close() {
        mActivity.setResult(mActivity.RESULT_OK);
        mActivity.finish();
    }



    /**
     * 返回给H5的 钱包地址
     * @return
     */
    @JavascriptInterface
    public String getWalletAddress() {
        LogUtils.logGGQ("getWalletAddress:"+mAddress);
        return mAddress;
    }


    //创建合约
    @JavascriptInterface
    public void transaction(String data){
        sendTransactionCreateData(data);
    }

    /**
     * H5操作后返回给app的创建合约的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 创建合约的数据
     */
    public abstract void sendTransactionCreateData(String data);

    //抵押
    @JavascriptInterface
    public void mortgageTransaction(String data){
        sendTransactionMortgageData(data);
    }

    /**
     * H5操作后返回给app的抵押的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 抵押数据
     */
    public abstract void sendTransactionMortgageData(String data);


    //出借
    @JavascriptInterface
    public void lendTransaction(String data){
        sendTransactionLendData(data);
    }

    /**
     * H5操作后返回给app的出借的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 出借数据
     */
    public abstract void sendTransactionLendData(String data);

   //退还
    @JavascriptInterface
    public void revertTransaction(String data){
        sendTransactionRevertData(data);
    }

    /**
     * H5操作后返回给app的退还的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 退还数据
     */
    public abstract void sendTransactionRevertData(String data);

    //赔付
    @JavascriptInterface
    public void ndemnityTransaction(String data){
        sendTransactionIndemnityData(data);
    }

    /**
     * H5操作后返回给app的赔付的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 赔付数据
     */
    public abstract void sendTransactionIndemnityData(String data);

    //取消
    @JavascriptInterface
    public void cancleTransaction(String data){
        sendTransactionCancleData(data);
    }

    /**
     * H5操作后返回给app的取消的数据，你需要拿这些数据在你的APP里进行签名
     * @param data 取消数据
     */
    public abstract void sendTransactionCancleData(String data);

}
