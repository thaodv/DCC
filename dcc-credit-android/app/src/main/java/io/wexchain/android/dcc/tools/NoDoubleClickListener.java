package io.wexchain.android.dcc.tools;

import android.view.View;

/**
 * Created by Wangpeng on 2017/6/5 15:11.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    
    private long lastClickTime = 0;
    
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
    
    protected abstract void onNoDoubleClick(View v);
    
}
