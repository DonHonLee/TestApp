package com.example.mobilphonesafe.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ${"李东宏"} on 2015/11/19.
 * 将短信应用设为默认的应用（第四步）
 * 4、实现一个提供intent filter for ACTION_RESPONSE_VIA_MESSAGE("android.intent.action.RESPOND_VIA_MESSAGE")
 *    with schemas, sms:, smsto:, mms:, and mmsto服务。这个服务需要 SEND_RESPOND_VIA_MESSAGE权限。
 *    这允许用户使用您的应用程序提供即时短信回应电话呼入。
 */
public class HeadlessSmsSendService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
