package com.example.mobilphonesafe.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.engine.SmsTools;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/11/4.
 */
public class AToolsActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private String myPackageName;
    private String defaultSmsApp;
    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        initActionBar();
        String code = android.os.Build.VERSION.RELEASE.substring(0,3);
        if (Float.parseFloat(code) >= 4.4f) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(AToolsActivity.this);
        }
        builder = new AlertDialog.Builder(AToolsActivity.this);
        myPackageName = getPackageName();
    }

    /**
     * @param view 号码归属地查询
     */
    public void getAddress(View view) {
        IntentUtils.startActivity(AToolsActivity.this, NumberAddressQueryActivity.class);
    }

    /**
     * @param view 常用号码查询
     */
    public void getCommonNumber(View view) {
        IntentUtils.startActivity(AToolsActivity.this, CommonNumberActivity.class);
    }

    /**
     * @param view 短信备份
     */
    public void smsBackup(View view) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("短信备份中...");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                boolean result = SmsTools.SmsBackup(AToolsActivity.this, new SmsTools.BackupSmsCallBack() {
                    @Override
                    public void beforeSmsBackup(int max) {
                        pd.setMax(max);
                    }

                    @Override
                    public void onSmsBackup(int process) {
                        pd.setProgress(process);
                    }
                }, "SmsBackup.xml");
                if (result) {
                    ToastUtils.show(AToolsActivity.this, "备份成功");
                } else {
                    ToastUtils.show(AToolsActivity.this, "备份失败");
                }
                pd.dismiss();
            }
        }.start();
    }

    /**
     * @param view 短信还原
     */
    public void smsReduction(View view) {
        String code = android.os.Build.VERSION.RELEASE.substring(0,3);
        if (Float.parseFloat(code) >= 4.4f) {
            if (!Telephony.Sms.getDefaultSmsPackage(AToolsActivity.this).equals(myPackageName)) {
                builder = new AlertDialog.Builder(AToolsActivity.this);
                builder.setMessage("短信备份需要设置为默认短信应用，" + "\n" + "是否设置？")
                        .setCancelable(false)
                        .setTitle("提醒：")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        } else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("短信还原中...");
            pd.show();
            new Thread() {
                @Override
                public void run() {
                    boolean result = SmsTools.SmsReduction(AToolsActivity.this, new SmsTools.BackupSmsCallBack() {
                        @Override
                        public void beforeSmsBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int process) {
                            pd.setProgress(process);
                        }
                    }, "SmsBackup.xml");
                    if (result) {
                        ToastUtils.show(AToolsActivity.this, "还原成功");
                    } else {
                        ToastUtils.show(AToolsActivity.this, "还原失败");
                    }
                    pd.dismiss();
                    /**
                     * 设置为保存的短信应用
                     */
                    /*Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
                    startActivity(intent);*/
                }
            }.start();
        }
    }

    /**
     * 程序锁
     * @param view
     */
    public void appLock(View view) {
        IntentUtils.startActivity(AToolsActivity.this,AppLockActivity.class);
    }

    @Override
    protected void onStart() {
        String code = android.os.Build.VERSION.RELEASE.substring(0,3);
        if (Float.parseFloat(code) >= 4.4f) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(AToolsActivity.this);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        String code = android.os.Build.VERSION.RELEASE.substring(0,3);
        if (Float.parseFloat(code) >= 4.4f) {
            if (Telephony.Sms.getDefaultSmsPackage(AToolsActivity.this).equals(myPackageName)) {
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMessage("短信还原中...");
                pd.show();
                new Thread() {
                    @Override
                    public void run() {
                        boolean result = SmsTools.SmsReduction(AToolsActivity.this, new SmsTools.BackupSmsCallBack() {
                            @Override
                            public void beforeSmsBackup(int max) {
                                pd.setMax(max);
                            }

                            @Override
                            public void onSmsBackup(int process) {
                                pd.setProgress(process);
                            }
                        }, "SmsBackup.xml");
                        if (result) {
                            ToastUtils.show(AToolsActivity.this, "还原成功");
                        } else {
                            ToastUtils.show(AToolsActivity.this, "还原失败");
                        }
                        pd.dismiss();
                        /**
                         * 设置为保存的短信应用
                         */
                    /*Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
                    startActivity(intent);*/
                    }
                }.start();
            }
        }
        super.onResume();
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_tools);
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
