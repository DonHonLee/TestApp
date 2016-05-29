package com.example.mobilphonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mobilphonesafe.services.CallSmsSafeService;
import com.example.mobilphonesafe.services.ShowAddressService;

/**
 * Created by ${"李东宏"} on 2015/10/27.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SimChangListener(context, sp);
        IntentCallSmsSafeService(context, sp);
        IntentShowAddressService(context, sp);
    }

    /**
     * 开机检查是否需要开启号码归属地服务
     * @param context
     * @param sp
     */
    private void IntentShowAddressService(Context context, SharedPreferences sp) {
        boolean showAddressServiceStatus = sp.getBoolean("ShowAddressServiceStatus", false);
        if (showAddressServiceStatus) {
            Intent intent = new Intent(context, ShowAddressService.class);
            context.startService(intent);
        }
    }

    /**
     * 开机检查是否开启黑名单拦截
     * @param context
     * @param sp
     */
    private void IntentCallSmsSafeService(Context context, SharedPreferences sp) {
        boolean CallSmsSafeServiceStatus = sp.getBoolean("CallSmsSafeServiceStatus", false);
        if (CallSmsSafeServiceStatus) {
            Intent service = new Intent(context, CallSmsSafeService.class);
            context.startService(service);
            Log.v("TAG", "开机自动服务自动启动.....");
        }
    }

    /**
     * 开机检查SIM卡的串号是否发生变化
     * @param context
     * @param sp
     */
    private void SimChangListener(Context context, SharedPreferences sp) {
        boolean protecting = sp.getBoolean("protecting", false);

        if (protecting) {
            //1.取出当前SIM卡的串号
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String realSim = tm.getSimSerialNumber()+"123";
            //2.取出原来绑定的SIM卡串号
            String bindSim = sp.getString("sim", "");
            //3.检查两次串号是否一致
            if (realSim.equals(bindSim)) {
                //SIM卡没有发生变化
            } else {
                //SIM卡变化了，可能被盗
                //偷偷的在后台安全号码发送一个短信
                SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changed", null,null);
            }
        } else {
            Log.w("+++++++", "没有开启保护");
        }
    }
}
