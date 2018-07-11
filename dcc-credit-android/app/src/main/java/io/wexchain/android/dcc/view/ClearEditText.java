package io.wexchain.android.dcc.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.wexchain.dcc.R;


/**
 * @author Created by Wangpeng on 2017/11/7 10:33.
 *         Usage:
 */
public class ClearEditText extends AppCompatEditText implements TextWatcher {
    
    private Drawable mDeleteImage;// 删除的按钮
    
    public ClearEditText(Context context) {
        this(context, null);
        init();
    }
    
    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    private void init() {
        addTextChangedListener(this);
    }
    
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    
    }
    
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mDeleteImage = TextUtils.isEmpty(charSequence) ? null : getContext().getResources().getDrawable(R.drawable
                .icon_address_book_query_delete);
        setCompoundDrawablesWithIntrinsicBounds(null, null, mDeleteImage, null);
    }
    
    @Override
    public void afterTextChanged(Editable editable) {
    
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //如果删除图片显示，并且输入框有内容
                if (mDeleteImage != null && !TextUtils.isEmpty(getText())) {
                    if (event.getX() > (getWidth() - getTotalPaddingRight()) && event.getX() < (getWidth() -
                            getPaddingRight())) {
                        //只有在这区域能触发清除内容的效果
                        getText().clear();
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
