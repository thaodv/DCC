package com.xxy.maple.tllibrary.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xxy.maple.tllibrary.R;
import com.xxy.maple.tllibrary.widget.tlprogressbar.TlSmoothProgressBar;


public class TlX5WebView extends WebView {

    private TlSmoothProgressBar progressBar;

    public TlX5WebView(Context context) {
        this(context, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public TlX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBar = (TlSmoothProgressBar) LayoutInflater.from(context)
                .inflate(R.layout.layout_tlprogress_webview, this, false);
        addView(progressBar);
        initWebViewSettings();
        //this.getView().setClickable(true);
        this.setDownloadListener(new MyWebViewDownLoadListener());
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
            Uri uri = Uri.parse(s);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webSetting.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSetting.setSupportZoom(true);//是否可以缩放，默认true
        webSetting.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webSetting.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webSetting.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webSetting.setAppCacheEnabled(true);//是否使用缓存
        webSetting.setDomStorageEnabled(true);//DOM Storage

//        WebSettings webSetting = this.getSettings();
//        webSetting.setJavaScriptEnabled(true); //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
//        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        webSetting.setAllowFileAccess(true);
//        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//        webSetting.setSupportZoom(true);//是否可以缩放，默认true
//        webSetting.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        webSetting.setDisplayZoomControls(false);
//        webSetting.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        webSetting.setSupportMultipleWindows(true);
//        webSetting.setAppCacheEnabled(true);//是否使用缓存
//        webSetting.setDomStorageEnabled(true);//DOM Storage
//        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
//        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setGeolocationEnabled(true);
//        String userAgent = webSetting.getUserAgentString();
//        webSetting.setUserAgentString(userAgent);
//        webSetting.setAppCacheEnabled(true);
//        webSetting.setDatabaseEnabled(true);
//        webSetting.setMediaPlaybackRequiresUserGesture(false);
//        if (NetWorkUtil.isConnected(getContext())) {
//            // 根据cache-control决定是否从网络上取数据
//            webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
//        } else {
//            // 没网，离线加载，优先加载缓存(即使已经过期)
//            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
    }

    public void showProgress() {
        if (progressBar != null && progressBar.getVisibility() == GONE) {
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void hideProgress() {
        if (progressBar != null && progressBar.getVisibility() == VISIBLE) {
            progressBar.setVisibility(VISIBLE);
            PropertyValuesHolder alphaToG = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
            ObjectAnimator mFadeOutAnimator = ObjectAnimator.ofPropertyValuesHolder(progressBar, alphaToG);
            mFadeOutAnimator.setDuration(200);
            mFadeOutAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mFadeOutAnimator.start();
        }
    }
}
