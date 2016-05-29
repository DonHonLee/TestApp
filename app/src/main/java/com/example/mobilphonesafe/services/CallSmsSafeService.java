package com.example.mobilphonesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.mobilphonesafe.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 需求：广播接受的存活周期和服务保持一致
 * Created by ${"李东宏"} on 2015/11/2.
 */
public class CallSmsSafeService extends Service {
    private BlackNumberDao dao;
    //电话管理者
    private TelephonyManager tm;

    /**
     * 内部类短信广播接收者的实例
     */
    private InnerSmsReceiver receiver;
    private MyPhoneStatusListener listener;
    /**
     * 设置来电静音
     */
    private AudioManager am;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("+++++", "开启黑名单拦截服务");
        //注册音频管理的服务
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        dao = new BlackNumberDao(this);
        //得到电话管理的服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //注册电话呼叫状态的监听器
        listener = new MyPhoneStatusListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("+++++", "关闭黑名单拦截服务");
        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String address = smsMessage.getOriginatingAddress();
                String result = dao.find(address);
                if("2".equals(result)||"3".equals(result)){
                    Log.i("++++","黑名单短信,拦截...");
                    abortBroadcast();
                }
                String body = smsMessage.getMessageBody();
                if (body.equals("fapiao")) {//luncence分词算法，开源项目
                    Log.i("++++","发票短信,拦截...");
                    abortBroadcast();
                }
            }
        }
    }

    public class MyPhoneStatusListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    String result = dao.find(incomingNumber);
                    if ("1".equals(result) || "3".equals(result)) {
                        Log.i("++++", "挂断电话");
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        endCall();
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        //监视呼叫记录的数据库，看什么时候生成了记录，就把他删掉
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                deleteCallLog(incomingNumber);
                                super.onChange(selfChange);
                            }
                        });
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 删除呼叫记录
     * @param incomingNumber
     */
    public void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingNumber});
        Log.i("++++++", "删除成功");
    }

    public void endCall() {
        //IBinder iBinder = ServiceManager.getService(DROPBOX_SERVICE);
        //利用反射调用系统隐藏的api挂断电话
        try {
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

