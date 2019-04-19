package io.wexchain.android.dcc.view.payment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.wexchain.android.common.tools.EditInputFilter;
import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2019/3/25 17:22.
 * usage:
 */
public class PaymentAddAmountStyle extends LinearLayout implements View.OnClickListener {
    
    private View rootView;
    private LinearLayout mLlFix;
    private ImageView mIvFix;
    private EditText mEtAmount;
    private TextView mTvCode;
    private LinearLayout mLlSelf;
    private ImageView mIvSelf;
    private ImageView mIvQuestion;
    private TextView mTvMinAmount;
    
    public PaymentAddAmountStyle(Context context) {
        super(context);
        initView();
    }
    
    public PaymentAddAmountStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public PaymentAddAmountStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    
    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_payment_add_amount_style, null);
        this.addView(rootView);
        
        mLlFix = findViewById(R.id.ll_fix);
        mIvFix = findViewById(R.id.iv_fix);
        mEtAmount = findViewById(R.id.et_amount);
        mTvCode = findViewById(R.id.tv_code);
        mLlSelf = findViewById(R.id.ll_self);
        mIvSelf = findViewById(R.id.iv_self);
        mIvQuestion = findViewById(R.id.iv_question);
        mTvMinAmount = findViewById(R.id.tv_min_amount);
        
        mLlFix.setOnClickListener(this);
        mLlSelf.setOnClickListener(this);
        mIvQuestion.setOnClickListener(this);
        
    }
    
    public void setFixChecked() {
        mIvFix.setBackgroundResource(R.drawable.icon_payment_selected);
    }
    
    public void setFixUnChecked() {
        mIvFix.setBackgroundResource(R.drawable.icon_payment_normal);
    }
    
    public void setSelfChecked() {
        mIvSelf.setBackgroundResource(R.drawable.icon_payment_selected);
    }
    
    public void setSelfUnChecked() {
        mIvSelf.setBackgroundResource(R.drawable.icon_payment_normal);
    }
    
    public String getAmount() {
        return mEtAmount.getText().toString().trim();
    }
    
    @SuppressLint("SetTextI18n")
    public void setParameters(String minAmount, String code) {
        mEtAmount.setHint(getResources().getText(R.string.payment_add_bigger) + minAmount);
        mTvCode.setText(code);
        mTvMinAmount.setText(getResources().getString(R.string.payment_add_smallest) + minAmount + " " +
                code);
    }
    
    public void setDigit(int digit){
        mEtAmount.setFilters(new InputFilter[]{new EditInputFilter(digit)});
    }
    
    public void setEtAmount(String minAmount) {
        mEtAmount.setHint(getResources().getText(R.string.payment_add_bigger) + minAmount);
    }
    
    public String getEtAmount() {
        return mEtAmount.getText().toString().trim();
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fix:
                setFixChecked();
                setSelfUnChecked();
                mOnPaymentStyleClickLinester.fix();
                break;
            case R.id.ll_self:
                setSelfChecked();
                setFixUnChecked();
                mOnPaymentStyleClickLinester.self();
                break;
            case R.id.iv_question:
                mOnPaymentStyleClickLinester.question();
                break;
            default:
                break;
        }
    }
    
    private OnPaymentStyleClickLinester mOnPaymentStyleClickLinester;
    
    public void setOnPaymentStyleClickLinester(OnPaymentStyleClickLinester onPaymentStyleClickLinester) {
        mOnPaymentStyleClickLinester = onPaymentStyleClickLinester;
    }
    
    public interface OnPaymentStyleClickLinester {
        
        void fix();
        
        void self();
        
        void question();
        
    }
}
