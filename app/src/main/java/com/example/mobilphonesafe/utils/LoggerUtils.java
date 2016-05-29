package com.example.mobilphonesafe.utils;

import android.util.Log;

/**
 * 通过控制LOG_LEVEL大小控制打印日志的等级
 * Created by ${"李东宏"} on 2015/11/30.
 */
public class LoggerUtils {
    private static int VERBOSE = 1;
    private static int DEBUG = 2;
    private static int INFO = 3;
    private static int WARN = 4;
    private static int ERROR = 5;
    private static int LOG_LEVEL = 0;

    public static void v(String tag, String msg) {
        if (VERBOSE > LOG_LEVEL) {
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if (DEBUG > LOG_LEVEL) {
            Log.d(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (INFO > LOG_LEVEL) {
            Log.i(tag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if (WARN > LOG_LEVEL) {
            Log.w(tag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if (ERROR > LOG_LEVEL) {
            Log.e(tag, msg);
        }
    }
}
