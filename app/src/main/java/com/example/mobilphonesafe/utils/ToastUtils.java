package com.example.mobilphonesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class ToastUtils {
    /**
     * 显示Toast的工具类，安全的工具类可以在任意的线程里面显示
     * @param activity
     * @param text
     */
    public static void show(final Activity activity, final String text) {
        if ("main".equalsIgnoreCase(Thread.currentThread().getName())) {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * @param activity
     * @param text
     * @param length 显示的时常
     */
    public static void show(final Activity activity, final String text,final int length) {
        if ("main".equalsIgnoreCase(Thread.currentThread().getName())) {
            Toast.makeText(activity, text, length).show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text,length).show();
                }
            });
        }
    }
}

