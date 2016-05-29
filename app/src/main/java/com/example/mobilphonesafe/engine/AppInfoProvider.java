package com.example.mobilphonesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mobilphonesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务类，获取系统应用程序的信息
 * Created by ${"李东宏"} on 2015/11/17.
 */
public class AppInfoProvider {

    /**
     * 获取手机里面所有安装的应用程序信息
     *
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            String packageName = packageInfo.packageName;
            appInfo.setPackName(packageName);
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(appName);
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            appInfo.setAppIcon(icon);
            String path = packageInfo.applicationInfo.sourceDir;
            File file = new File(path);
            long size = file.length();
            appInfo.setApkSize(size);
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户程序
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //手机系统内部
                appInfo.setInRom(true);
            } else {
                //SD卡
                appInfo.setInRom(false);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
