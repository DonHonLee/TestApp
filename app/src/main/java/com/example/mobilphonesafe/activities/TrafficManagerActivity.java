package com.example.mobilphonesafe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.domain.AppInfo;
import com.example.mobilphonesafe.engine.AppInfoProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/23.
 */
public class TrafficManagerActivity extends AppCompatActivity {

    private TextView tv_disk_avail;
    private TextView tv_sd_avail;
    private LinearLayout ll_loading;
    private ListView lv_appManager;
    private TextView tv_app_count;
    private ActionBar mActionBar;
    private int mUID;
    /**
     * 全部应用的信息
     */
    private List<AppInfo> appInfos;
    /**
     * 用户应用的信息
     */
    private List<AppInfo> userAppInfos;
    /**
     * 系统应用的信息
     */
    private List<AppInfo> systemAppInfos;
    /**
     * 根据位置信息，确定出来那个条目被点击了
     * 被点击的app信息
     */
    private AppInfo appinfo;
    /**
     * 整个界面的悬浮窗体，需求是整个activity只有一个窗体
     */
    private PopupWindow popupWindow;
    /**
     * listView里面的布局
     */
    private View view;
    private AppInfoAdapter adapter;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            lv_appManager.setAdapter(adapter);
        }
    };

    private InnerUninstallReceiver receiver;
    private long uploadBytes;
    private long downloadBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        TrafficStats.getMobileRxBytes();//获取手机2G/3G/4G下载的数据信息，单位byte
        TrafficStats.getMobileTxBytes();//获取手机2G/3G/4G上传的数据信息
        TrafficStats.getTotalRxBytes();//获取手机全部端口下载的流量数据，包括WIFI和2G/3G/4G
        TrafficStats.getTotalTxBytes();//获取手机全部端口上传的流量数据，包括WIFI和2G/3G/4G
        TrafficStats.getUidRxBytes(1000);//获取指定应用下载的流量数据
        TrafficStats.getUidTxBytes(1000);//获取指定应用上传的流量数据


        initActionBar();
        adapter = new AppInfoAdapter();
        /**
         * 注册广播接收着
         */
        receiver = new InnerUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction( Intent.ACTION_MANAGE_NETWORK_USAGE);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
        tv_disk_avail = (TextView) findViewById(R.id.tv_disk_avail);
        tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
        File dataFile = Environment.getDataDirectory();
        File sdFile = Environment.getExternalStorageDirectory();
        long dataSize = dataFile.getFreeSpace();
        long sdSize = sdFile.getFreeSpace();
        tv_disk_avail.setText("内存可用：" + Formatter.formatFileSize(TrafficManagerActivity.this, dataSize));
        tv_sd_avail.setText("SD可用内存：" + Formatter.formatFileSize(TrafficManagerActivity.this, sdSize));

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_app_count = (TextView) findViewById(R.id.tv_app_count);
        lv_appManager = (ListView) findViewById(R.id.appManager);
        getAppInfo();
        lv_appManager.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_app_count.setText("系统程序" + systemAppInfos.size() + "个");
                    } else {
                        tv_app_count.setText("用户程序" + userAppInfos.size() + "个");
                    }
                }
            }
        });
    }

    private int getUID(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取系统应用的信息
     */
    private void getAppInfo() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {

            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo1 : appInfos) {
                    if (appInfo1.isUserApp()) {
                        //用户的应用
                        userAppInfos.add(appInfo1);
                    } else {
                        systemAppInfos.add(appInfo1);
                    }
                }
                //通知界面更新
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    private class AppInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + systemAppInfos.size() + 2;
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
            AppInfo appInfo;
            if (position == 0) {
                TextView tv = new TextView(TrafficManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户程序" + userAppInfos.size() + "个");
                return tv;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(TrafficManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统程序" + systemAppInfos.size() + "个");
                return tv;
            } else if (position <= userAppInfos.size()) {
                //用户程序
                appInfo = userAppInfos.get(position - 1);
            } else {
                //系统占据的应用
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_app_info, null);
                holder = new ViewHolder();
                holder.iv_appIcon = (ImageView) view.findViewById(R.id.iv_appIcon);
                holder.tv_appName = (TextView) view.findViewById(R.id.tv_appName);
                holder.tv_appLocation = (TextView) view.findViewById(R.id.tv_appLocation);
                holder.tv_appSize = (TextView) view.findViewById(R.id.tv_appSize);
                view.setTag(holder);
            }
            holder.iv_appIcon.setImageDrawable(appInfo.getAppIcon());
            holder.tv_appName.setText(appInfo.getAppName());
            holder.tv_appSize.setText("总流量："+Formatter.formatFileSize(getApplicationContext(),uploadBytes + downloadBytes));
            mUID = getUID(appInfo.getPackName());
            String upload = Formatter.formatFileSize(TrafficManagerActivity.this,uploadBytes);//获取指定应用下载的流量数据
            String download = Formatter.formatFileSize(TrafficManagerActivity.this, downloadBytes);//获取指定应用上传的流量数据
            if (appInfo.isInRom()) {
                holder.tv_appLocation.setText("上传 ：" + upload + "       " + "下载 ： " + download);
            } else {
                holder.tv_appLocation.setText("上传 ：" + upload + "       " + "下载 ： " + download);
            }
            return view;
        }
    }

    static class ViewHolder {

        ImageView iv_appIcon;
        TextView tv_appName;
        TextView tv_appLocation;
        TextView tv_appSize;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    private class InnerUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (appinfo.isUserApp()) {
                userAppInfos.remove(appinfo);
            } else {
                systemAppInfos.remove(appinfo);
            }
            long uidRxBytes = TrafficStats.getUidRxBytes(mUID);
            long uidTxBytes = TrafficStats.getUidTxBytes(mUID);
            if (uidRxBytes != 0 || uidTxBytes != 0) {
                uploadBytes = uploadBytes + uidRxBytes;
                downloadBytes = downloadBytes + uidTxBytes;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_liuliang);
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
