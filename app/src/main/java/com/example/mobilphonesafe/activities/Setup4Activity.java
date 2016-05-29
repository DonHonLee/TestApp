package com.example.mobilphonesafe.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.receiver.MyAdmin;
import com.example.mobilphonesafe.ui.SettingCheckView;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class Setup4Activity extends SetupBaseActivity {
    private SettingCheckView tv_setup4_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        tv_setup4_status = (SettingCheckView) findViewById(R.id.tv_setup4_status);
        tv_setup4_status.setChecked(sp.getBoolean("protecting", false));
        tv_setup4_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (tv_setup4_status.isCheck()) {
                    tv_setup4_status.setChecked(false);
                    editor.putBoolean("protecting", false);
                } else {
                    tv_setup4_status.setChecked(true);
                    editor.putBoolean("protecting", true);
                }
                editor.commit();
            }
        });
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("finishSetup", true);
        editor.commit();
        Intent intent = new Intent();
        intent.setAction("com.example.mobilephonesafe.openSmsService");
       sendBroadcast(intent);
        ToastUtils.show(this, "进入配置界面");
        IntentUtils.startActivityAndFinish(Setup4Activity.this, LostFindActivity.class);
    }


    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinish(this, Setup3Activity.class);
    }

    /**
     * 点击激活设备的超级管理员
     * @param view
     */
    public void activeAdmin(View view) {
        ComponentName who = new ComponentName(this, MyAdmin.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "开启管理员权限");
        startActivity(intent);
    }
}
