package io.wexchain.android.dcc.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.wexchain.dcc.R;


/**
 * Created by Wangpeng on 2017/8/9 20:13.
 * Usage:
 */
public class ToastView {
    
    private Toast mToast;
    
    public ToastView(Context context, int dwIdres, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_drawable_view, null);
        RelativeLayout rlContent = view.findViewById(R.id.rl_content);
        ImageView mIvImg = view.findViewById(R.id.iv_img);
        TextView mTvMsg = view.findViewById(R.id.tv_msg);
        
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        rlContent.setLayoutParams(layoutParams);
        
        mIvImg.setBackgroundResource(dwIdres);
        mTvMsg.setText(msg);
        
        if (null != mToast) {
            mToast.cancel();
        }
        mToast = new Toast(context);
        mToast.setGravity(Gravity.FILL, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(view);
        mToast.show();
    }
    
}
