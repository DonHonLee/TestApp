package com.example.mobilphonesafe.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 五秒后自动清理后台程序
 * Created by ${"李东宏"} on 2015/11/21.
 */
public class TimerKillProcessService extends Service {
    private Timer timer;
    private TimerTask task;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //每隔5秒执行一次
                Log.i("+++++", "五秒自动清理程序");
                /*ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> infos =  am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : infos) {
                    am.killBackgroundProcesses(info.processName);
                }*/
            }
        };
        timer.schedule(task, 0, 5000);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        task.cancel();
        timer = null;
        task = null;
        super.onDestroy();
    }
}
