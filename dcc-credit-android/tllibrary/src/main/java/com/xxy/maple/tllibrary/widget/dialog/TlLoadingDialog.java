package com.xxy.maple.tllibrary.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.xxy.maple.tllibrary.R;


/**
 * Created by Gaoguanqi on 2018/3/30.
 *  使用方法：
 *      LoadingDialog dialog = new LoadingDialog(this);
 *      dialog.setMsg(getString(R.string.loading));
 *      dialog.setNotCancel();//设置dialog不自动消失
 *      dialog.show();
 *
 */
@SuppressWarnings("unused")
public class TlLoadingDialog extends Dialog {

    private String msg;
    private boolean isBackDismiss;

    public TlLoadingDialog(@NonNull Context context) {
        super(context, R.style.loading_dialog);
    }

    public TlLoadingDialog(@NonNull Context context, String msg) {
        super(context, R.style.loading_dialog);
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tlloading);
        setCanceledOnTouchOutside(false);
        ((TextView) findViewById(R.id.progress_tv)).setText(msg);
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TlLoadingDialog notCancel() {
        isBackDismiss = true;
        return this;
    }

    public void setNotCancel() {
        isBackDismiss = true;
    }

    @Override
    public void onBackPressed() {
        if (!isBackDismiss) super.onBackPressed();
    }
}
