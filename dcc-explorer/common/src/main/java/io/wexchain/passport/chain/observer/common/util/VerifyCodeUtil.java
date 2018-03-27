package io.wexchain.passport.chain.observer.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yy on 2017/6/20.
 */
public class VerifyCodeUtil {

   /* private static String[] beforeShuffle = new String[] {"0","1", "2", "3", "4", "5", "6", "7",
            "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };*/

    private static String[] beforeShuffle = new String[] {"0","1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String getVerifyCode(){
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        return afterShuffle.substring(0, 6);
    }

}
