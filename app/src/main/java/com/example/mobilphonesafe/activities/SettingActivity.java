package com.example.mobilphonesafe.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.services.CallSmsSafeService;
import com.example.mobilphonesafe.services.ShowAddressService;
import com.example.mobilphonesafe.services.WatchDogService;
import com.example.mobilphonesafe.ui.SettingChangeView;
import com.example.mobilphonesafe.ui.SettingCheckView;
import com.example.mobilphonesafe.utils.ServiceStatusUtils;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class SettingActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private SettingCheckView scv_setting_update;
    private SettingCheckView scv_setting_blacknumber;
    private SettingCheckView scv_setting_showAddress;
    private SettingCheckView scv_setting_watchDog;
    private static final String item[] = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    /**
     * 修改背景的组合控件
     */
    private SettingChangeView scv_change_bg;
    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initActionBar();
        scv_change_bg = (SettingChangeView) findViewById(R.id.scv_change_bg);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        scv_change_bg.setDesc(item[sp.getInt("which",0)]);
        boolean update = sp.getBoolean("update", false);
        //自动更新的逻辑
        scv_setting_update = (SettingCheckView) findViewById(R.id.scv_setting_update);
        scv_setting_update.setChecked(update);
        scv_setting_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (scv_setting_update.isCheck()) {
                    scv_setting_update.setChecked(false);
                    editor.putBoolean("update", false);
                } else {
                    scv_setting_update.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });

        //黑名单拦截的逻辑
        // 不采用SharedPreferences存储状态，会出现一些问题（例如即使勾选，但服务未destroy时手机断电关机，开机之后服务并未开启）
        scv_setting_blacknumber = (SettingCheckView) findViewById(R.id.scv_setting_blacknumber);
        scv_setting_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scv_setting_blacknumber.isCheck()) {
                    scv_setting_blacknumber.setChecked(false);
                    //停止服务，取消广播接收者
                    Intent intent = new Intent(SettingActivity.this, CallSmsSafeService.class);
                    stopService(intent);
                } else {
                    scv_setting_blacknumber.setChecked(true);
                    //开启服务  注册广播接收者
                    Intent intent = new Intent(SettingActivity.this, CallSmsSafeService.class);
                    startService(intent);
                }
            }
        });
        //归属地地址显示
        scv_setting_showAddress = (SettingCheckView) findViewById(R.id.scv_setting_showAddress);
        scv_setting_showAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scv_setting_showAddress.isCheck()) {
                    scv_setting_showAddress.setChecked(false);
                    Intent intent = new Intent(SettingActivity.this, ShowAddressService.class);
                    stopService(intent);
                } else {
                    scv_setting_showAddress.setChecked(true);
                    Intent intent = new Intent(SettingActivity.this, ShowAddressService.class);
                    startService(intent);
                }
            }
        });
        scv_setting_watchDog = (SettingCheckView) findViewById(R.id.scv_setting_watchDog);
        scv_setting_watchDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scv_setting_watchDog.isCheck()) {
                    scv_setting_watchDog.setChecked(false);
                    Intent intent = new Intent(SettingActivity.this, WatchDogService.class);
                    stopService(intent);
                } else {
                    scv_setting_watchDog.setChecked(true);
                    Intent intent = new Intent(SettingActivity.this, WatchDogService.class);
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        CallSmsSafeServiceStatus(editor);
        ShowAddressServiceStatus(editor);
        WatchDogServiceStatus(editor);
        super.onStart();
    }

    /**
     * 号码归属地服务的状态，读取系统的运行状态，看看我的服务是否活着，如果服务活着，就勾选上；如果服务未开启，就取消勾
     * @param editor
     */
    private void ShowAddressServiceStatus(SharedPreferences.Editor editor) {
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.ShowAddressService")) {
            scv_setting_showAddress.setChecked(true);
            editor.putBoolean("ShowAddressServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("ShowAddressServiceStatus", false);
            scv_setting_showAddress.setChecked(false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
    }

    /**
     * 黑名单服务的状态，读取系统的运行状态，看看我的服务是否活着，如果服务活着，就勾选上；如果服务未开启，就取消勾
     * @param editor
     */
    private void CallSmsSafeServiceStatus(SharedPreferences.Editor editor) {
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.CallSmsSafeService")) {
            scv_setting_blacknumber.setChecked(true);
            editor.putBoolean("CallSmsSafeServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("CallSmsSafeServiceStatus", false);
            scv_setting_blacknumber.setChecked(false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
    }

    private void WatchDogServiceStatus(SharedPreferences.Editor editor) {
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.WatchDogService")) {
            scv_setting_watchDog.setChecked(true);
            editor.putBoolean("WatchDogServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("WatchDogServiceStatus", false);
            scv_setting_watchDog.setChecked(false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
    }

    @Override
    protected void onPause() {
        Log.i("++++", "暂停状态");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.CallSmsSafeService")) {
            editor.putBoolean("CallSmsSafeServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("CallSmsSafeServiceStatus", false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("++++", "停止状态");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.CallSmsSafeService")) {
            editor.putBoolean("CallSmsSafeServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("CallSmsSafeServiceStatus", false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("++++", "销毁状态");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (ServiceStatusUtils.isServiceRunning(this, "com.example.mobilphonesafe.services.CallSmsSafeService")) {
            editor.putBoolean("CallSmsSafeServiceStatus", true);
            Log.i("++++", "服务运行中");
        } else {
            editor.putBoolean("CallSmsSafeServiceStatus", false);
            Log.i("++++", "服务停止中");
        }
        editor.commit();
        super.onDestroy();
    }

    /**
     * 更改归属地提示框的北京
     * @param view
     */
    public void changeAddressBg(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");

        builder.setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("which", which);
                editor.commit();
                dialog.dismiss();
                scv_change_bg.setDesc(item[which]);
            }
        });
        builder.show();
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_setting);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

