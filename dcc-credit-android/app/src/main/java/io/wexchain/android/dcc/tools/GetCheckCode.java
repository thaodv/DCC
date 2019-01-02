package io.wexchain.android.dcc.tools;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * @author Created by Wangpeng
 * Usage:
 */
public class GetCheckCode extends CountDownTimer {
    
    private Button mButton;
    
    public GetCheckCode(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    
    public GetCheckCode(long millisInFuture, long countDownInterval, Button button) {
        this(millisInFuture, countDownInterval);
        this.mButton = button;
    }
    
    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setText(millisUntilFinished / 1000 + "S");
        mButton.setEnabled(false);
    }
    
    @Override
    public void onFinish() {
        mButton.setText("重新发送");
        mButton.setEnabled(true);
    }
}
