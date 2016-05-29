package com.example.mobilphonesafe;

import android.app.Application;
import android.os.Build;

import com.example.mobilphonesafe.utils.LoggerUtils;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by ${"李东宏"} on 2015/11/30.
 */
public class MobilSafeApplication extends Application {
    @Override
    public void onCreate() {
        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHandler());
        super.onCreate();
    }

    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            LoggerUtils.i("MobileSafeApplication", "发生了异常,但是被哥捕获了...");
            //并不能把异常给消化掉. 只是应用程序挂掉之前,来一个留遗嘱的时间
            try {
                Field[] fields = Build.class.getDeclaredFields();
                StringBuffer sb = new StringBuffer();
                for (Field field : fields) {
                    field.setAccessible(true);//AccessibleTest类中的成员变量为private,故必须进行此操作
                    String value = field.get(null).toString();
                    String name = field.getName();
                    sb.append(name);
                    sb.append(":");
                    sb.append(value);
                    sb.append("\n");
                }

                FileOutputStream out = new FileOutputStream("/mnt/sdcard/error.log");
                StringWriter wr = new StringWriter();
                PrintWriter err = new PrintWriter(wr);
                ex.printStackTrace(err);
                String errorLog = wr.toString();
                sb.append(errorLog);
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //专注自杀, 早死早超生
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
