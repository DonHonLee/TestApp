package com.example.mobilphonesafe.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/27.
 */
public class CleanCacheActivity extends AppCompatActivity {
    private static final int SCAN_FINISH = 1;
    private static final int SCANNING_APP = 2;
    private static final int BUILD_SHOW = 3;
    private ProgressBar pb;
    private TextView tv_clean_status;
    private PackageManager pm;
    private CacheInfo mCacheInfo;
    private List<CacheInfo> mCacheInfos;
    private FrameLayout fl_scan;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_FINISH:
                    fl_scan.setVisibility(View.GONE);
                    if (mCacheInfos.size() > 0) {
                        Toast.makeText(CleanCacheActivity.this, "扫描结束", Toast.LENGTH_SHORT).show();
                        lv_cache_info.setAdapter(new CacheInfoAdapter());
                    } else {
                        Toast.makeText(CleanCacheActivity.this, "您的手机很干净，请放心使用！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SCANNING_APP:
                    String appName = (String) msg.obj;
                    tv_clean_status.setText("正在清理软件：" + appName);
                    break;
                case BUILD_SHOW:
                    final String packName = (String) msg.obj;
                    builder.setTitle("提醒")
                            .setMessage("单独清理该程序缓存需要开启系统设置，是否跳转至该界面？")
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showApplicationInfo(packName);
                                }
                            });
                    builder.show();
                    break;
            }
        }
    };
    private ListView lv_cache_info;
    private AlertDialog.Builder builder;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initActionBar();
        pm = getPackageManager();
        fl_scan = (FrameLayout) findViewById(R.id.fl_scan);
        pb = (ProgressBar) findViewById(R.id.pb);
        tv_clean_status = (TextView) findViewById(R.id.tv_scan_status);
        lv_cache_info = (ListView) findViewById(R.id.lv_cache_info);
        builder = new AlertDialog.Builder(CleanCacheActivity.this);
        scanCache();
    }

    /**
     * 扫描缓存
     */
    private void scanCache() {
        fl_scan.setVisibility(View.VISIBLE);
        mCacheInfos = new ArrayList<CacheInfo>();
        new Thread() {
            @Override
            public void run() {
                //1.扫描全部应用的包名
                List<PackageInfo> infos = pm.getInstalledPackages(0);
                pb.setMax(infos.size());
                int progress = 0;
                for (PackageInfo info : infos) {
                    try {
                        String packName = info.packageName;
                        Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        method.invoke(pm, packName, new MyPackageOberver());
                        progress++;
                        pb.setProgress(progress);
                        sleep(100);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = Message.obtain();
                message.what = SCAN_FINISH;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private class MyPackageOberver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

            try {
                mCacheInfo = new CacheInfo();
                mCacheInfo.packName = pStats.packageName;
                mCacheInfo.appIcon = pm.getPackageInfo(mCacheInfo.packName, 0).applicationInfo.loadIcon(pm);
                mCacheInfo.appName = pm.getPackageInfo(mCacheInfo.packName, 0).applicationInfo.loadLabel(pm).toString();
                Message message = Message.obtain();
                message.what = SCANNING_APP;
                message.obj = mCacheInfo.appName;
                mHandler.sendMessage(message);
                if (pStats.cacheSize > 0) {
                    mCacheInfo.cacheSize = pStats.cacheSize;
                    mCacheInfos.add(mCacheInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class CacheInfo {
        long cacheSize;
        Drawable appIcon;
        String appName;
        String packName;
    }

    private class CacheInfoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mCacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder holder;
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_cache_info, null);
                holder = new ViewHolder();
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                holder.tv_appcachesize = (TextView) view.findViewById(R.id.tv_appcachesize);
                holder.iv_delete_cache = (ImageView) view.findViewById(R.id.iv_delete_cache);
                view.setTag(holder);
            }
            final CacheInfo info = mCacheInfos.get(position);
            holder.iv_appicon.setImageDrawable(info.appIcon);
            holder.tv_appname.setText(info.appName);
            holder.tv_appcachesize.setText(Formatter.formatFileSize(getApplicationContext(), info.cacheSize));
            holder.iv_delete_cache.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mPm.deleteApplicationCacheFiles(packageName, mClearCacheObserver);
                    //只有系统的应用才能获得<uses-permission android:name="android.permission.DELETE_CACHE_FILES"/>的权限
                    /*PackageManager pm = getPackageManager();
                    Method[] methods = PackageManager.class.getDeclaredMethods();
                    for (Method method : methods) {
                        if ("deleteApplicationCacheFiles".equals(method.getName())) {
                            try {
                                method.invoke(pm, info.packName,new ClearCacheObserver());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }*/
                    Message message = Message.obtain();
                    message.what = BUILD_SHOW;
                    message.obj = info.packName;
                    mHandler.sendMessage(message);
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_appcachesize;
        ImageView iv_delete_cache;
    }

    /**
     * 系统应用清理缓存需要的observer类
     */
    class ClearCacheObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            /*final Message msg = mHandler.obtainMessage(CLEAR_CACHE);
            msg.arg1 = succeeded ? OP_SUCCESSFUL:OP_FAILED;
            mHandler.sendMessage(msg);*/
            ToastUtils.show(CleanCacheActivity.this,"清除成功");
        }
    }
    private void showApplicationInfo(String packName) {
        /*<action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + packName));
        startActivity(intent);
    }

    public void cleanAllCache() {
        Method[] methods = PackageManager.class.getDeclaredMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Long.MAX_VALUE, new ClearAllCacheObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                scanCache();
                return;
            }
        }
    }

    private class ClearAllCacheObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_clear);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.clean_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.clear_cache:
                cleanAllCache();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
