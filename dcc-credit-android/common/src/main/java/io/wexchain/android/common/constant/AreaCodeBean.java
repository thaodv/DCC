package io.wexchain.android.common.constant;

import java.util.List;

/**
 * @author Created by Wangpeng on 2019/3/11 16:07.
 * usage:
 */
public class AreaCodeBean {
    
    public List<AreaCodeItemBean> res;
    
    public static class AreaCodeItemBean {
        public String country_name;
        public String country_en;
        public String country_code;
        public String dial_code;
    }
    
}
