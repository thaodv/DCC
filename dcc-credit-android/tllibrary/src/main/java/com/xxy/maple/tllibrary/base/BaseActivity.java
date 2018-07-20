package com.xxy.maple.tllibrary.base;

import android.widget.Toast;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xxy.maple.tllibrary.utils.Utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends RxAppCompatActivity {

    /**
     * 线程调度
     */
    protected <T> ObservableTransformer<T, T> compose(final LifecycleTransformer<T> lifecycle) {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    // 可添加网络连接判断等
                    if (!Utils.isNetworkAvailable(BaseActivity.this)) {
                        Toast.makeText(BaseActivity.this, "网络异常！", Toast.LENGTH_LONG).show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycle);
    }
}
