package com.xxy.maple.tllibrary.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xxy.maple.tllibrary.R;
import com.xxy.maple.tllibrary.app.AppConstant;
import com.xxy.maple.tllibrary.app.TlSampleJavascript;
import com.xxy.maple.tllibrary.utils.LogUtils;
import com.xxy.maple.tllibrary.utils.Utils;
import com.xxy.maple.tllibrary.widget.TlX5WebView;

public abstract class TlBrowserActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibtBack;
    private TextView tvClose, tvTitle;
    private TlX5WebView mWebView;
    public static String address;

    public static final String ADDRESS = "address";
    public static final int CODE_H5_GOBORROWLOADING = 0x0051;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlbrowser);
        initView();
        initWebView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar!=null){
            supportActionBar.setDisplayShowHomeEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        ibtBack = (ImageButton) findViewById(R.id.ibt_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvClose = (TextView) findViewById(R.id.tv_close);
        mWebView = (TlX5WebView) findViewById(R.id.tlWebView);
        tvClose.setOnClickListener(this);
        ibtBack.setOnClickListener(this);
    }

    protected  abstract TlSampleJavascript getSampleJavascript(TlBrowserActivity activity, TlX5WebView webView, String address);
    private void initWebView() {

        address = getIntent().getStringExtra(ADDRESS);

        if(TextUtils.isEmpty(address)){
            Toast.makeText(this,"address empty",Toast.LENGTH_LONG).show();
            this.finish();
        }

        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.addJavascriptInterface(getSampleJavascript(this, mWebView, address), "obj");
        mWebView.loadUrl(AppConstant.WebPageUrl.LOAN_INDEX);
    }


    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
            super.onPageStarted(webView, url, bitmap);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            mWebView.hideProgress();
        }

        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webView, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            sslErrorHandler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (url.startsWith("https") || url.startsWith("http") || url.startsWith("file")) {
                webView.loadUrl(url);
            } else {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    //将功能Scheme以URI的方式传入data
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView webView, String title) {
            super.onReceivedTitle(webView, title);
            if (!title.startsWith("http")) {
                tvTitle.setText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            super.onProgressChanged(webView, progress);
            if (progress >= 100) {
                mWebView.hideProgress();
            } else {
                mWebView.showProgress();
            }
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibt_back) {
            onBackKeyPressed();
        } else if (id == R.id.tv_close) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public boolean onBackKeyPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(RESULT_OK);
            finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onBackKeyPressed() || super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==CODE_H5_GOBORROWLOADING) {
            String type = data.getExtras().getString("type");
            if(TextUtils.equals(type,"0")){
                String order_no = data.getExtras().getString("order_no");
                 mWebView.loadUrl("javascript:goBorrowLoading('"+order_no+"')");
            }else if(TextUtils.equals(type,"1")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }else if(TextUtils.equals(type,"2")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }else if(TextUtils.equals(type,"3")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }else if(TextUtils.equals(type,"4")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }else if(TextUtils.equals(type,"5")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }else if(TextUtils.equals(type,"6")){
                mWebView.loadUrl("javascript:goIndex('"+type+"')");
            }
        }
    }

    public  void launchActivityForResult(@NonNull Intent intent, int REQUESTCODE){
        //防止重复启动
        if(!Utils.isFastDoubleClick()){
            startActivityForResult(intent,REQUESTCODE);
        }
    }
}
