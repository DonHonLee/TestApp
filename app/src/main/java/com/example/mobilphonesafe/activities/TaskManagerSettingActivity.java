package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.services.AutoKillProcessService;
import com.example.mobilphonesafe.services.TimerKillProcessService;
import com.example.mobilphonesafe.ui.SettingCheckView;
import com.example.mobilphonesafe.utils.ServiceStatusUtils;

/**
 * Created by ${"李东宏"} on 2015/11/21.
 */
public class TaskManagerSettingActivity extends Activity {
    private SettingCheckView scv_show_system;
    private SettingCheckView scv_auto_kill_process;
    private SettingCheckView scv_timer_kill_process;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanager_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        scv_show_system = (SettingCheckView) findViewById(R.id.scv_show_system);
        scv_show_system.setChecked(sp.getBoolean("showSystem", true));
        scv_show_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (scv_show_system.isCheck()) {
                    scv_show_system.setChecked(false);
                    editor.putBoolean("showSystem", false);
                } else {
                    scv_show_system.setChecked(true);
                    editor.putBoolean("showSystem", true);
                }
                editor.commit();
            }
        });
        scv_auto_kill_process = (SettingCheckView) findViewById(R.id.scv_auto_kill_process);
        scv_auto_kill_process.setChecked(sp.getBoolean("autoKillProcess", true));
        scv_auto_kill_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (scv_auto_kill_process.isCheck()) {
                    scv_auto_kill_process.setChecked(false);
                    editor.putBoolean("autoKillProcess", false);
                    Intent intent = new Intent(TaskManagerSettingActivity.this, AutoKillProcessService.class);
                    stopService(intent);
                } else {
                    scv_auto_kill_process.setChecked(true);
                    editor.putBoolean("autoKillProcess", true);
                    Intent intent = new Intent(TaskManagerSettingActivity.this, AutoKillProcessService.class);
                    startService(intent);
                }
                editor.commit();
            }
        });
        scv_timer_kill_process = (SettingCheckView) findViewById(R.id.scv_timer_kill_process);
        scv_timer_kill_process.setChecked(sp.getBoolean("timerKillProcess", true));
        scv_timer_kill_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences.Editor editor = sp.edit();
                if (scv_timer_kill_process.isCheck()) {
                    scv_timer_kill_process.setChecked(false);
                    editor.putBoolean("timerKillProcess", false);
                    Intent intent = new Intent(TaskManagerSettingActivity.this, TimerKillProcessService.class);
                    stopService(intent);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerSettingActivity.this);
                    builder.setTitle("警告：")
                            .setMessage("设置次功能在定时结束时可能会影响你的正常操作。是否继续设置定时关闭后台程序")
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scv_timer_kill_process.setChecked(true);
                                    editor.putBoolean("timerKillProcess", true);
                                    Intent intent = new Intent(TaskManagerSettingActivity.this, TimerKillProcessService.class);
                                    startService(intent);
                                }
                            });
                    builder.show();
                }
                editor.commit();
            }
        });
    }

    @Override
    protected void onStart() {
        if (ServiceStatusUtils.isServiceRunning(getApplicationContext(), "com.example.mobilphonesafe.services.AutoKillProcessService")) {
            scv_auto_kill_process.setChecked(true);
        } else {
            scv_auto_kill_process.setChecked(false);
        }
        if (ServiceStatusUtils.isServiceRunning(getApplicationContext(), "com.example.mobilphonesafe.services.TimerKillProcessService")) {
            scv_timer_kill_process.setChecked(true);
        } else {
            scv_timer_kill_process.setChecked(false);
        }
        super.onStart();
    }
}
