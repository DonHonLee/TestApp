package com.example.mobilphonesafe.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.AntiVirusDao;
import com.example.mobilphonesafe.utils.Md5Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/28.
 */
public class AntiVirusActivity extends AppCompatActivity {
    private ImageView iv_scan;
    private ProgressBar pb;
    private LinearLayout ll_info;
    private TextView tv_scan_status;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        initActionBar();
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        pb = (ProgressBar) findViewById(R.id.pb);
        ll_info = (LinearLayout) findViewById(R.id.ll_info);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);
        scanVirus();
    }

    /**
     * 扫描手机全部文件
     */
    private void scanVirus() {
        new Thread(){
            @Override
            public void run() {
                //遍历手机里面的每一个应用程序的信息，查询他的特征码在病毒库是否存在
                final PackageManager pm = getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_SIGNATURES);
                pb.setMax(packageInfos.size());
                int process = 0;
                for (final PackageInfo packageInfo : packageInfos) {
                    try {
                        String apkPath = packageInfo.applicationInfo.sourceDir;
                        //获取签名的MD5值，检测是否为病毒签名。
                        String apkSignature = packageInfo.signatures[0].toCharsString();
                        String md5Signature = Md5Utils.encode(apkSignature);
                        File file = new File(apkPath);
                        MessageDigest digest = MessageDigest.getInstance("md5");
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buffer)) != -1) {
                            digest.update(buffer, 0, len);
                        }
                        byte[] result = digest.digest();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (byte b : result) {
                            String str = Integer.toHexString(b & 0xff);
                            if (str.length() == 1) {
                                stringBuffer.append("0");
                            }
                            stringBuffer.append(str);
                        }
                        fis.close();
                        final String info = stringBuffer.toString();
                        final String desc = AntiVirusDao.isVirus(info);
                        final String appname = packageInfo.applicationInfo.loadLabel(pm).toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textInfo = new TextView(getApplicationContext());
                                if (desc != null) {
                                    //病毒文件
                                    textInfo.setText("病毒文件" + appname + desc);
                                    textInfo.setTextColor(Color.RED);
                                } else {
                                    //安全文件
                                    textInfo.setText("安全文件" + appname);
                                    textInfo.setTextColor(R.color.safeFile);
                                }
                                ll_info.addView(textInfo,0);
                            }
                        });
                        process++;
                        pb.setProgress(process);
                        sleep(50);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_scan.clearAnimation();
                        iv_scan.setVisibility(View.INVISIBLE);
                        tv_scan_status.setText("扫描结束");
                        pb.setProgress(0);
                    }
                });
            }
        }.start();
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_safe);
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
