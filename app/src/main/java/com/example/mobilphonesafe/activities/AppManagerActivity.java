package com.example.mobilphonesafe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.example.mobilphonesafe.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/17.
 */
public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_disk_avail;
    private TextView tv_sd_avail;
    private LinearLayout ll_loading;
    private ListView lv_appManager;
    private TextView tv_app_count;
    private ActionBar mActionBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        initActionBar();
        adapter = new AppInfoAdapter();
        /**
         * 注册广播接收着
         */
        receiver = new InnerUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
        tv_disk_avail = (TextView) findViewById(R.id.tv_disk_avail);
        tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
        File dataFile = Environment.getDataDirectory();
        File sdFile = Environment.getExternalStorageDirectory();
        long dataSize = dataFile.getFreeSpace();
        long sdSize = sdFile.getFreeSpace();
        tv_disk_avail.setText("内存可用：" + Formatter.formatFileSize(AppManagerActivity.this, dataSize));
        tv_sd_avail.setText("SD可用内存：" + Formatter.formatFileSize(AppManagerActivity.this, sdSize));

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
                dismissPopupWindow();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_app_count.setText("系统程序" + systemAppInfos.size() + "个");
                    } else {
                        tv_app_count.setText("用户程序" + userAppInfos.size() + "个");
                    }
                }
            }
        });
        lv_appManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                } else if (position == (userAppInfos.size() + 1)) {
                    return;
                } else if (position <= userAppInfos.size()) {
                    // 用户程序
                    int location = position - 1;// 减去最前面的第一个textview占据的位置
                    appinfo = userAppInfos.get(location);
                } else {
                    // 系统程序
                    int location = position - 1 - userAppInfos.size() - 1;
                    appinfo = systemAppInfos.get(location);
                }
                dismissPopupWindow();
                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
                popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, view.findViewById(R.id.iv_appIcon).getWidth() + 5, location[1]);

                Animation aa = new AlphaAnimation(0.2f, 1.0f);
                aa.setDuration(500);
                Animation sa = new ScaleAnimation(0.2f, 1.0f, 1.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(500);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                contentView.startAnimation(set);
                LinearLayout ll_uninstall = (LinearLayout) contentView
                        .findViewById(R.id.ll_uninstall);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_share = (LinearLayout) contentView
                        .findViewById(R.id.ll_share);
                ll_share.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_start = (LinearLayout) contentView
                        .findViewById(R.id.ll_start);
                ll_start.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_info = (LinearLayout) contentView
                        .findViewById(R.id.ll_info);
                ll_info.setOnClickListener(AppManagerActivity.this);
            }
        });
    }



    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
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
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户程序" + userAppInfos.size() + "个");
                return tv;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(AppManagerActivity.this);
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
            holder.tv_appSize.setText(Formatter.formatFileSize(getApplicationContext(), appInfo.getApkSize()));
            if (appInfo.isInRom()) {
                holder.tv_appLocation.setText("手机内存");
            } else {
                holder.tv_appLocation.setText("外储存卡");
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
        dismissPopupWindow();
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_uninstall:
                uninstallApp();
                break;
            case R.id.ll_share:
                shareApplication();
                break;
            case R.id.ll_start:
                startApplication();
                break;
            case R.id.ll_info:
                showApplicationInfo();
                break;
        }
        dismissPopupWindow();
    }

    /**
     * 显示应用程序的信息
     */
    private void showApplicationInfo() {
        /*<action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appinfo.getPackName()));
        startActivity(intent);
    }

    /**
     * 分享应用程序
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "推荐你使用一款软件,软件的名称为:" + appinfo.getAppName() + ",我用的很爽.");
        startActivity(intent);
    }

    /**
     * 开启应用程序
     */
    private void startApplication() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appinfo.getPackName());
        if (intent != null) {
            startActivity(intent);
        } else {
            ToastUtils.show(AppManagerActivity.this, "无法启动应用");
        }
    }

    /**
     * 卸载应用程序
     */
    private void uninstallApp() {
        /*<action android:name="android.intent.action.VIEW" />
        <action android:name="android.intent.action.DELETE" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/
        if (appinfo.isUserApp()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + appinfo.getPackName()));
            startActivity(intent);
        } else {
            ToastUtils.show(AppManagerActivity.this, "系统软件需要root才能卸载");
        }
    }

    private class InnerUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (appinfo.isUserApp()) {
                userAppInfos.remove(appinfo);
            } else {
                systemAppInfos.remove(appinfo);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_manager);
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
