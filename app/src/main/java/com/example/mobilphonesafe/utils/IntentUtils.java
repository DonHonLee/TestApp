package com.example.mobilphonesafe.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class IntentUtils {
    /**
     * 开启一个activity
     * @param activity
     * @param cls
     */
    public static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }
    /**
     * 开启一个activity
     * @param activity
     * @param cls
     */
    public static void startActivityAndFinish(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 开启一个activity
     * @param activity
     * @param cls
     * @param delayTime 延迟执行的时间毫秒
     */
    public static void startActivityForDelay(final Activity activity, final Class<?> cls, final int delayTime) {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, cls);
                activity.startActivity(intent);
            }
        }.start();
    }

    public static void startActivityForDelayAndFinish(final Activity activity, final Class<?> cls, final int delayTime) {
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, cls);
                activity.startActivity(intent);
                activity.finish();
            }
        }.start();
    }
}

