package io.wexchain.android.common.tools;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @author Created by Wangpeng on 2019/3/21 19:50.
 * usage:
 */
public class EditInputFilter implements InputFilter {
    
    private final int decimalDigits = 8;
    
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String reg = "^[0-9|.]*$";
        if (!source.toString().matches(reg)) {
            return "";
        }
        
        
        int dotPos = -1;
        int len = dest.length();
        for (int i = 0; i < len; i++) {
            char c = dest.charAt(i);
            if (c == '.' || c == ',') {
                dotPos = i;
                break;
            }
        }
        
        //之前没有小数点，要在第一位添加小数点，而且添加位置后面没有指定位以上字符，添加0.
        if (dotPos < 0 && (source.equals(".") || source.equals(",")) && dstart == 0 && len - dend <=
                decimalDigits) {
            return "0.";
        }
        
        //添加小数点的后面有指定位数以上数字，不添加字符
        if ((source.equals(".") || source.equals(",")) && len - dend > decimalDigits) {
            return "";
        }
        
        //已经包含小数点
        if (dotPos >= 0) {
            
            //新输入的内容为小数点
            if (source.equals(".") || source.equals(",")) {
                //已经有小数点，不添加小数点了返回空的
                return "";
            }
            // 小数点之前添加则正常添加字符
            if (dend <= dotPos) {
                return null;
            }
            //小数点后面的位数大于指定的位数则返回空的
            if (len - dotPos > decimalDigits) {
                return "";
            }
        }
        return null;
    }
    
}
