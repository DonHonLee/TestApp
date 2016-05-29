package com.example.mobilphonesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.services.GPSService;

/**
 * Created by ${"李东宏"} on 2015/10/27.
 *
 */
public class SmsReceiver extends BroadcastReceiver {
    private static String TAG = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(context, MyAdmin.class);
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String body = smsMessage.getMessageBody();
            if ("#*location*#".equals(body)) {
                Log.i(TAG, "返回手机的位置信息");
                //获取用户的位置信息.
                //后台获取.
                Intent service = new Intent(context, GPSService.class);
                context.startService(service);
                Log.i(TAG, "执行服务");
                abortBroadcast();
            } else if ("#*alarm*#".equals(body)) {
                Log.i(TAG, "播放报警音乐");
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.isLooping();
                player.setVolume(1.0f, 1.0f);
                player.start();
                abortBroadcast();
            } else if ("#*wipedate*#".equals(body)) {
                if (dpm.isAdminActive(who)){
                    //dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    Log.i(TAG, "远程销毁数据");
                }
            } else if ("#*lockscreen*#".equals(body)) {
                Log.i(TAG, "远程锁屏");
                if (dpm.isAdminActive(who)) {
                    dpm.resetPassword("5158",0);
                    dpm.lockNow();
                }
            }
        }
    }
}
