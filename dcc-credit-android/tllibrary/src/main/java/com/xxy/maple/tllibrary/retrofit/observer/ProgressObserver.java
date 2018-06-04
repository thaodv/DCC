package com.xxy.maple.tllibrary.retrofit.observer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xxy.maple.tllibrary.R;
import com.xxy.maple.tllibrary.entity.BaseEntity;
import com.xxy.maple.tllibrary.widget.dialog.TlLoadingDialog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Gaoguanqi on 2018/5/30.
 */

public abstract class ProgressObserver<T extends BaseEntity> implements Observer<T> {

    private static final String TAG = "ProgressObserver";

    private TlLoadingDialog loadingDialog;
    private String mLoadingText;
    private Context mContext;

    protected ProgressObserver(Context context) {
        this(context, null);
        showLoading();
    }

    public ProgressObserver(Context context, String loadingText) {
        mContext = context;
        mLoadingText = loadingText;
        showLoading();
    }

    private void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new TlLoadingDialog(mContext);
        }
        if (TextUtils.isEmpty(mLoadingText)) mLoadingText = mContext.getString(R.string.loading);
        loadingDialog.setMsg(mLoadingText);
        loadingDialog.setNotCancel();//设置dialog不自动消失
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }


    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T response) {
        if (response.isSuccess()) {
            onSuccess(response);
        } else {
            onFail(response.getErrmsg());
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError:" + e.toString());
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    protected abstract void onSuccess(T t);

    protected void onFail(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
