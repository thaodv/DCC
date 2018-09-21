package io.wexchain.android.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import io.wexchain.android.common.R;

public class MyDialog extends Dialog {

    private View contentView;

    public MyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 自已定义Dialog的布局
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fullscreen_loading,null);
        ImageView ivg =contentView.findViewById(R.id.iv_gear);


        final Animation anim =AnimationUtils.loadAnimation(context, R.anim.rotate);
        ivg.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                v.startAnimation(anim);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //v.startAnimation(anim);
                v.clearAnimation();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);
    }

   //提供外部获取View的方法
    public View getContentView() {
        return contentView;
    }
}