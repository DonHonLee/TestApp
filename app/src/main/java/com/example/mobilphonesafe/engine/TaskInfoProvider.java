package com.example.mobilphonesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.domain.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/20.
 */
public class TaskInfoProvider {
    /**
     * 获取正在运行的进程的所有进程信息
     * @param context 上下文
     * @return 进程信息集合
     */
    public static List<ProcessInfo> getRunningProcessInfos(Context context) {
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            ProcessInfo processInfo = new ProcessInfo();
            String packName = info.processName;
            processInfo.setPackName(packName);
            long memSize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
            processInfo.setMemSize(memSize);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                processInfo.setAppName(appName);
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                processInfo.setIcon(icon);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //系统进程
                    processInfo.setUserTask(false);
                } else {
                    //用户进程
                    processInfo.setUserTask(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                processInfo.setAppName(packName);
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }
}
