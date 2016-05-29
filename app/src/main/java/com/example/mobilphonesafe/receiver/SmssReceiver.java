package com.example.mobilphonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ${"李东宏"} on 2015/11/19.
 * 将短信应用设为默认应用（第一步）
 * 1、接收SMS_DELIVER_ACTION("android.provider.Telephony.SMS_DELIVER")的broadcast receiver，
 *    这个broadcast receiver需要有BROADCAST_SMS权限。这些是为了让你的应用能接收到SMS messages。
 */
public class SmssReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
