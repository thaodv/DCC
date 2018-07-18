package io.wexchain.android.dcc.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Wangpeng on 2016/5/19.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
    
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }
    
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    
    public static void finishActivity(Class tClass) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(tClass)) {
                activity.finish();
            }
        }
    }
    
    public static void finishActivitys(List<Class> tClass) {
        if (tClass == null) {
            return;
        }
        for (Activity activity : activities) {
            for (Class c : tClass) {
                if (activity.getClass().equals(c)) {
                    activity.finish();
                }
            }
        }
    }
    
}
