package com.example.mobilphonesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/2.
 */
public class ServiceStatusUtils {
    /**
     * 判断服务是否处于运行状态
     * @param context 上下文
     * @param classname    服务的全路径类名
     * @return true 服务运行中 ；false 服务已经停止
     */
    public static boolean isServiceRunning(Context context,String classname) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String runningClassName = info.service.getClassName();
            if (classname.equals(runningClassName)) {
                return true;
            }
        }
        return false;
    }
}
