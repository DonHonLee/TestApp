package com.example.mobilphonesafe.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.AntiVirusDao;
import com.example.mobilphonesafe.utils.AppInfoUtils;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.StreamUtils;
import com.example.mobilphonesafe.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final int NEED_UPDATE = 1;
    //private TextView tv_splash_version;
    private String versionName;
    private RelativeLayout rl_splash_root;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEED_UPDATE:
                    showUpdateDialog(msg);
                    break;
            }
        }
    };
    //private ProgressBar pb_splash;
    private TextView tv_splash_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        versionName = AppInfoUtils.getAppVersionName(getApplicationContext());
        //tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        //pb_splash = (ProgressBar) findViewById(R.id.pb_splash);
        tv_splash_download = (TextView) findViewById(R.id.tv_splash_download);
        //tv_splash_version.setText("欢迎使用黑马卫士" + " " + versionName);
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(2000);
        rl_splash_root.startAnimation(aa);
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("update", false)) {
            checkVersion();
        } else {
            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this, HomeActivity.class, 2000);
        }

        copyDB("address.db");
        copyDB("commonnum.db");
        copyDB("antivirus.db");
        updateVirus();

        //创建快捷图标
        createShortCut();
        //开启通知栏通知
        createStatusBarIcon();
    }

    private void createStatusBarIcon() {
        //NotificationCompat.Builder mBuilder =
        //        (NotificationCompat.Builder) new NotificationCompat.Builder(this)
        //                .setAutoCancel(false)
        //                .setSmallIcon(R.drawable.icon)
        //                .setContentTitle("黑马卫士")
        //                .setContentText("黑马卫士保护您的手机安全");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("手机卫士")
                .setContentText("手机卫士保护您的手机安全");
        Intent resultIntent = new Intent(this, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void createShortCut() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean shortCut = sp.getBoolean("shortCut", false);
        if (!shortCut) {
            //快捷方式的图标
            //快捷方式的名称
            //快捷方式干什么事
            //快捷方式其实是显示在桌面上的,让桌面帮我们的应用程序创建快捷图标
            //给桌面发消息
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.main_icon));
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "快捷图标");
            //快捷方式开启的意图
            //Intent shortCut = new Intent(this, HomeActivity.class);显示意图在快捷方式中无效
            Intent shortCutIntent = new Intent();
            shortCutIntent.setAction("com.example.mobilphonesafe.home");
            shortCutIntent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
            //发送创建快捷方式的广播
            sendBroadcast(intent);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("shortCut", true);
            editor.commit();
        }
    }

    /**
     * 联网更新数据
     */
    private void updateVirus() {
        final String path = "http://192.168.199.159:8080/updateVirus.txt";
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream is = connection.getInputStream();
                    String jsonstr = StreamUtils.readStream(is);
                    JSONObject object = new JSONObject(jsonstr);
                    int serviceVersion = object.getInt("version");
                    int oldVersion = AntiVirusDao.getVersion();
                    if (serviceVersion > oldVersion) {
                        String md5 = object.getString("md5");
                        String name = object.getString("name");
                        String type = object.getString("type");
                        String desc = object.getString("desc");
                        AntiVirusDao.addVirusInfo(md5, type, name, desc);
                        AntiVirusDao.setVersion(serviceVersion);
                        Log.i("++++++++", "更新成功");
                    } else {
                        Toast.makeText(SplashActivity.this, "病毒库为最新版本，无需更新", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void copyDB(final String dbName) {
        new Thread() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), dbName);
                if (file.exists() && file.length() > 0) {
                    Log.i("+++++", "数据库已经存在" + getFilesDir().getPath());
                } else {
                    try {
                        Log.i("+++++", "数据库创建成功");
                        InputStream is = getAssets().open(dbName);
                        StreamUtils.readStream(is, file, dbName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void copyVirusDB(final String dbName) {
        new Thread() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), dbName);
                try {
                    Log.i("+++++", "数据库创建成功");
                    InputStream is = getAssets().open(dbName);
                    StreamUtils.readStream(is, file, dbName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getString(R.string.serverUrl));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        String json = StreamUtils.readStream(is);
                        UpdateInfo info = new UpdateInfo();
                        JSONObject jsonObject = new JSONObject(json);
                        info.serviceVersion = jsonObject.getInt("version");
                        info.downloadUrl = jsonObject.getString("downloadurl");
                        info.desc = jsonObject.getString("desc");
                        int appVersion = AppInfoUtils.getAppVersionCode(getApplicationContext());
                        Log.w("+++++++", info.serviceVersion + "," + info.downloadUrl + "," + info.desc);
                        Log.w("-------", appVersion + "");
                        //判断服务器的版本号和本地的版本号是否一致，如果服务器的版本号>本地的版本号，则提醒用户升级
                        if (info.serviceVersion > AppInfoUtils.getAppVersionCode(getApplicationContext())) {
                            Log.w("------", "有新的版本，是否升级");
                            Message msg = Message.obtain();
                            msg.obj = info;
                            msg.what = NEED_UPDATE;
                            mHandler.sendMessageDelayed(msg, 2000);
                        } else {
                            Log.w("------", "最新版本，进入主界面");
                            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this, HomeActivity.class, 2000);
                        }
                    }
                } catch (MalformedURLException e) {
                    ToastUtils.show(SplashActivity.this, "URL路径出现错误");
                    IntentUtils.startActivityAndFinish(SplashActivity.this, HomeActivity.class);
                    e.printStackTrace();
                } catch (IOException e) {
                    ToastUtils.show(SplashActivity.this, "网络错误");
                    IntentUtils.startActivityAndFinish(SplashActivity.this, HomeActivity.class);
                    e.printStackTrace();
                } catch (JSONException e) {
                    ToastUtils.show(SplashActivity.this, "服务器端配置文件出错");
                    IntentUtils.startActivityAndFinish(SplashActivity.this, HomeActivity.class);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class UpdateInfo {
        int serviceVersion;
        String desc;
        String downloadUrl;
    }

    /**
     * 显示升级提醒的对话框
     *
     * @param msg
     */
    private void showUpdateDialog(Message msg) {
        //pb_splash.setVisibility(View.INVISIBLE);
        final UpdateInfo info = (UpdateInfo) msg.obj;
        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                .setTitle("升级提醒");
        builder.setMessage(info.desc);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HttpUtils httpUtils = new HttpUtils();
                final File file = new File(Environment.getExternalStorageDirectory(), "mobilsafe1.0.apk");
                httpUtils.download(info.downloadUrl, file.getAbsolutePath(), true, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        ToastUtils.show(SplashActivity.this, "下载成功");
                        /*
                        系统源码下载安装的intent-filter
                        <intent-filter>
                        <action android:name="android.intent.action.VIEW" />
                        <category android:name="android.intent.category.DEFAULT" />
                        <data android:scheme="content" />
                        <data android:scheme="file" />
                        <data android:mimeType="application/vnd.android.package-archive" />
                        </intent-filter>*/
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtils.show(SplashActivity.this, "下载失败");
                        IntentUtils.startActivityAndFinish(SplashActivity.this, HomeActivity.class);
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        tv_splash_download.setText(current + "/" + total);
                        super.onLoading(total, current, isUploading);
                    }
                });
            }
        });
        builder.setNegativeButton("下次再升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                IntentUtils.startActivityAndFinish(SplashActivity.this, HomeActivity.class);
            }
        });
        builder.show();
    }
}

