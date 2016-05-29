package com.example.mobilphonesafe.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.receiver.MyWidget;
import com.example.mobilphonesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${"李东宏"} on 2015/11/23.
 */
public class UpdateWidgetService extends Service{
    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;
    private innerUpdateWidgetService receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new innerUpdateWidgetService();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        awm = AppWidgetManager.getInstance(UpdateWidgetService.this);
        startUpdateWidgetService();
    }

    private void startUpdateWidgetService() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                ComponentName provider = new ComponentName(getApplicationContext(), MyWidget.class);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                views.setTextViewText(R.id.process_count, "正在运行的软件：" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()));
                String availStr = Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailRam(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, "可用内存" + availStr);
                Intent intent = new Intent(getApplicationContext(), KillAllReceiver.class);
                intent.setAction("con.example.mobilphonesafe.KILLALLPROCESS");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                awm.updateAppWidget(provider, views);
                Log.i("///////", "计时器运行中");
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        task.cancel();
        timer = null;
        task = null;
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    //优化电量，手机黑屏时关闭计时
    private class innerUpdateWidgetService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                timer.cancel();
                task.cancel();
                timer = null;
                task = null;
                Log.i("///////", "取消计时器");
            }else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                startUpdateWidgetService();
                Log.i("///////", "开启计时器");
            }
        }
    }
}
