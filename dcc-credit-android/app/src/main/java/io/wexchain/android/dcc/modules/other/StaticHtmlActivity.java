package io.wexchain.android.dcc.modules.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.wexchain.android.dcc.tools.LogUtils;
import io.wexchain.dcc.R;

public class StaticHtmlActivity extends AppCompatActivity {
    
    private FrameLayout mFrBack;
    private Button mBtClose;
    private TextView mTvTitle;
    private ImageView mIvShare;
    
    private ProgressBar mProgress;
    private WebView mContent;
    
    private String mTitleValue = "";
    private String mUrlValue = "";
    
    public static Intent getResultIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, StaticHtmlActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_html);
        
        mFrBack = findViewById(R.id.fr_back);
        mBtClose = findViewById(R.id.bt_close);
        mTvTitle = findViewById(R.id.tv_title);
        mIvShare = findViewById(R.id.iv_share);
        
        mProgress = findViewById(R.id.pb_progress);
        mContent = findViewById(R.id.wv_content);
        
        mTitleValue = getIntent().getStringExtra("title");
        mUrlValue = getIntent().getStringExtra("url");
        
        LogUtils.i("title:", mTitleValue + " ,url:" + mUrlValue);
        
        mTvTitle.setText(mTitleValue);
        
        mFrBack.setOnClickListener(v -> {
            if (mContent.canGoBack()) {
                mBtClose.setVisibility(View.GONE);
                mContent.goBack();
            } else {
                finish();
            }
        });
        
        /*mIvShare.setVisibility(View.VISIBLE);
        mIvShare.setImageDrawable(getResources().getDrawable(R.drawable.ic_scan));
        mIvShare.setOnClickListener(v -> startActivityForResult(new Intent(this, QrScannerActivity.class).
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS), RequestCodes.SCAN));*/
        
        mBtClose.setOnClickListener(v -> finish());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
    
    public void loadData() {
        WebSettings settings = mContent.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mContent.setDrawingCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        // 取消滚动条
        mContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // 触摸焦点起作用
        mContent.requestFocus();
        // 不弹窗浏览器是否保存密码
        settings.setSavePassword(false);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 自动适应屏幕尺寸
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        
        //支持所有标签
        settings.setDomStorageEnabled(true);
        mContent.addJavascriptInterface(new JavaScriptObjectUtils(this), "BitExpress");
        mContent.loadUrl(mUrlValue);
        mContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgress.setVisibility(View.GONE);
                } else {
                    mProgress.setVisibility(View.VISIBLE);
                }
            }
            
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.i("title", title);
                mTvTitle.setText(title);
            }
            
        });
        
        mContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(mUrlValue);
                return true;
            }
            
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 接受所有网站的证书
                handler.proceed();
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mContent.canGoBack()) {
            mBtClose.setVisibility(View.GONE);
            // 返回前一个页面
            mContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
