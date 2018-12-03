package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/10/17 14:42.
 * usage:
 */
public class CashLoanRepaymentMsgCheckDialog extends Dialog implements View.OnClickListener {
    
    private final ImageView mIvClose;
    private final TextView mTvPhone;
    private final EditText mEtCheckCode;
    private final Button mBtSend;
    private final Button mBtSubmit;
    
    public CashLoanRepaymentMsgCheckDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc);
        setContentView(R.layout.dialog_cashloan_repayment_msg_check);
        mIvClose = findViewById(R.id.iv_close);
        mTvPhone = findViewById(R.id.tv_phone);
        mEtCheckCode = findViewById(R.id.et_check_code);
        mBtSend = findViewById(R.id.bt_send);
        mBtSubmit = findViewById(R.id.bt_submit);
        
        mIvClose.setOnClickListener(this);
        mBtSend.setOnClickListener(this);
        mBtSubmit.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_close:
                
                break;
            case R.id.bt_send:
                
                break;
            case R.id.bt_submit:
                
                break;
            default:
                break;
        }
    }
}
