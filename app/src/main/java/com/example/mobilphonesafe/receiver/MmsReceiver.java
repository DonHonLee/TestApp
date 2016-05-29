package com.example.mobilphonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ${"李东宏"} on 2015/11/19.
 * 将短信应用设为默认应用（第二步）
 * 2、接收WAP_PUSH_DELIVER_ACTION("android.provider.Telephony.WAP_PUSH_DELIVER") 的broadcast receiver，
 *    这个需要BROADCAST_WAP_PUSH权限。这些是为了让你的应用能接收到MMS  messages。
 */
public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
