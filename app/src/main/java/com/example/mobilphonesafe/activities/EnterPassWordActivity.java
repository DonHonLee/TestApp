package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/11/26.
 */
public class EnterPassWordActivity extends Activity {
    private PackageManager pm;
    private ImageView iv_appIcon;
    private TextView tv_appName;
    private EditText et_watchDog_password;
    private String packName;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        iv_appIcon = (ImageView) findViewById(R.id.iv_appIcon);
        tv_appName = (TextView) findViewById(R.id.tv_appName);
        et_watchDog_password = (EditText) findViewById(R.id.et_watchDog_password);
        Intent intent = getIntent();
        packName = intent.getStringExtra("packName");
        pm = getPackageManager();
        try {
            String appName = pm.getPackageInfo(packName, 0).applicationInfo.loadLabel(pm)
                    .toString();
            Drawable appIcon = pm.getPackageInfo(packName, 0).applicationInfo.loadIcon(pm);
            tv_appName.setText(appName);
            iv_appIcon.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        //        <action android:name="android.intent.action.MAIN" />
        //        <category android:name="android.intent.category.HOME" />
        //        <category android:name="android.intent.category.DEFAULT" />
        //        <category android:name="android.intent.category.MONKEY"/>
        //返回到桌面设置
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void enter(View view) {
        String password = et_watchDog_password.getText().toString().trim();
        if ("123".equals(password)) {
            //通知看门狗,这个应用程序临时的取消保护
            //在Activity给服务一个消息.
            //发送一个自定义的广播,这个广播只有看门狗能够识别.
            Intent intent = new Intent();
            intent.setAction("com.example.mobilphonesafe.cancelwatchdog");
            intent.putExtra("packName", packName);
            sendBroadcast(intent);
            EnterPassWordActivity.this.finish();
        } else {
            ToastUtils.show(EnterPassWordActivity.this,"密码错误");
        }
    }
}
