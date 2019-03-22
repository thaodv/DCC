package io.wexchain.android.dcc.view.dialog.paymentcode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class PaymentCodePayDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public TextView mTvAccount;
    public TextView mTvPaymentTitle;
    public TextView mTvOrderId;
    public TextView mTvPayStyle;
    public TextView mTvTrustRecharge;
    public Button mBtSure;
    
    public PaymentCodePayDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_paymentcode_pay);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mTvAccount = findViewById(R.id.tv_account);
        mTvPaymentTitle = findViewById(R.id.tv_payment_title);
        mTvOrderId = findViewById(R.id.tv_order_id);
        mTvPayStyle = findViewById(R.id.tv_pay_style);
        mTvTrustRecharge = findViewById(R.id.tv_trust_recharge);
        mBtSure = findViewById(R.id.bt_sure);
        
        mIbtClose.setOnClickListener(this);
        mTvTrustRecharge.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
    }
    
    /**
     * @param account  付款金额
     * @param title    收款标题
     * @param orderId  订单编号
     * @param payStyle 付款方式
     * @param coinType 币种类型
     * @param balance  余额
     */
    @SuppressLint("SetTextI18n")
    public void setParameters(String account, String title, String orderId, String payStyle, String
            coinType, String balance) {
        
        BigDecimal accountValue = new BigDecimal(account).setScale(8, RoundingMode.DOWN);
        BigDecimal balanceValue = new BigDecimal(balance).setScale(8, RoundingMode.DOWN);
        
        mTvAccount.setText(accountValue.toPlainString() + " " + coinType);
        mTvPaymentTitle.setText(title);
        mTvOrderId.setText(orderId);
        mTvPayStyle.setText(coinType + "(" + getContext().getResources().getString(R.string
                .payment_balance) + balanceValue.toPlainString() + ")");
        
        if (accountValue.compareTo(balanceValue) < 1) {
            mTvTrustRecharge.setVisibility(View.GONE);
            mBtSure.setText(getContext().getResources().getString(R.string.payment_pay_dialog_pay));
        } else {
            mTvTrustRecharge.setVisibility(View.VISIBLE);
            mBtSure.setText(getContext().getResources().getString(R.string.payment_pay_dialog_close));
        }
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
            case R.id.tv_trust_recharge:
                mOnClickListener.trustRecharge();
                dismiss();
                break;
            case R.id.bt_sure:
                mOnClickListener.sure();
                dismiss();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        
        void trustRecharge();
        
        void sure();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
}
