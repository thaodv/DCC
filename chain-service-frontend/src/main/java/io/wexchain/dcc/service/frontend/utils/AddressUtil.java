/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;


import org.apache.commons.lang3.StringUtils;


/**
 * <p>
 * </p>
 * 
 * @author yanyi
 */
public class AddressUtil {

    public static final String START = "0x";

    public static String getAddress(String address){
        //转为小写
        String lowerAddress = StringUtils.lowerCase(address);
        if(StringUtils.startsWith(lowerAddress,START)){
            //是0x开头
            return lowerAddress;
        }else {
            //补上0x
            return START.concat(lowerAddress);
        }
    }

}
