package com.example.mobilphonesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by ${"李东宏"} on 2015/11/17.
 */
public class AppInfo {
    /**
     * 应用程序的图标
     */
    private Drawable appIcon;
    /**
     * 应用程序的名称
     */
    private String appName;
    /**
     * 应用程序的包名
     */
    private String packName;
    /**
     * 是否安装在手机内存
     */
    private boolean inRom;
    /**
     * 应用程序的大小
     */
    /**
     * 是否是用户应用程序
     */
    private boolean userApp;
    private long apkSize;

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public long getApkSize() {
        return apkSize;
    }


    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }


}
