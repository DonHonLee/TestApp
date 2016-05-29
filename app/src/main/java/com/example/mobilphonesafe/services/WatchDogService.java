package com.example.mobilphonesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.mobilphonesafe.activities.EnterPassWordActivity;
import com.example.mobilphonesafe.db.dao.AppLockDao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ${"李东宏"} on 2015/11/26.
 */
public class WatchDogService extends Service {
    /*private boolean flag;
    private ActivityManager am;
    private AppLockDao dao;
    private String tempStopProtectPackname;
    private InnerWatchDogReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDao(WatchDogService.this);
        receiver = new InnerWatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.mobilphonesafe.cancelwatchdog");
        registerReceiver(receiver, filter);
        startWatchDog();
    }

    private void startWatchDog() {
        if (flag) {
            return;
        }
        flag = true;
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(100);
                    String packName = infos.get(0).topActivity.getPackageName();
                    if (dao.query(packName)) {
                        //应用加锁
                        if (packName.equals(tempStopProtectPackname)) {
                        }else {
                            Intent intent = new Intent(WatchDogService.this, EnterPassWordActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packName", packName);
                            startActivity(intent);
                        }
                    } else {
                        //应用没加锁，直接进入界面
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        flag = false;
        super.onDestroy();
    }

    private class InnerWatchDogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname = intent.getStringExtra("packName");
        }
    }*/

    private ActivityManager am;
    private boolean whileStatus;
    private AppLockDao dao;
    private innerWatchDogService innerService;
    private String tempStopProtectName;
    private List<ActivityManager.RunningTaskInfo> info;
    private String packageName;
    private List<String> lockPackNames;
    private Intent intent;
    private WatchDogDBchangObserver observer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("++++", "开启看门狗服务");
        innerService = new innerWatchDogService();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.mobilphonesafe.cancelwatchdog");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(innerService, filter);
        dao = new AppLockDao(this);
        lockPackNames = dao.findAll();
        intent = new Intent(WatchDogService.this, EnterPassWordActivity.class);
        //服务里面没有任务站信息，如果在服务开启activity需要记得添加任务栈的FLAG
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("content://com.example.mobilphonesafe.applockchang");
        observer = new WatchDogDBchangObserver(new Handler());
        this.getContentResolver().registerContentObserver(uri,true,observer);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startWatchDog();
        super.onCreate();
    }

    private void startWatchDog() {
        if (whileStatus) {
            return;
        }
        whileStatus = true;
        new Thread() {
            @Override
            public void run() {
                while (whileStatus) {
                    info = am.getRunningTasks(1);
                    packageName = info.get(0).topActivity.getPackageName();
                    // if (dao.query(packageName)) {//查询内存的效率比查询数据库的效率高很多
                    if (lockPackNames.contains(packageName)) {//查询内存效率更高
                        //应用程序需要被保护，弹出一个输入密码的界面
                        if (packageName.equals(tempStopProtectName)) {

                        } else {
                            intent.putExtra("packName", packageName);
                            startActivity(intent);
                        }
                    } else {
                        //应用程序不需要保护
                    }
                    String[] activePackages;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                        activePackages = getActivePackages();
                    } else {
                        activePackages = getActivePackagesCompat();
                    }
                    if (activePackages != null) {
                        for (String activePackage : activePackages) {
                            if (dao.query(activePackage)) {
                                //如果应用程序输入密码正确，临时取消输入密码界面
                                if (activePackage.equals(tempStopProtectName)) {

                                } else {
                                    //应用程序需要被保护，弹出一个输入密码的界面
                                    intent.putExtra("packName", activePackage);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        whileStatus = false;
        unregisterReceiver(innerService);
        this.getContentResolver().unregisterContentObserver(observer);
        observer = null;
        super.onDestroy();
    }

    String[] getActivePackagesCompat() {
        final List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

    String[] getActivePackages() {
        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    private class innerWatchDogService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.mobilphonesafe.cancelwatchdog".equals(intent.getAction())) {
                tempStopProtectName = intent.getStringExtra("packName");
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                whileStatus = false;
                tempStopProtectName = null;
            }else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                startWatchDog();
            }

        }
    }

    private class WatchDogDBchangObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public WatchDogDBchangObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            lockPackNames = dao.findAll();
        }
    }
}
