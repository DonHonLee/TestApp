package com.example.mobilphonesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.NumberAddressDao;

/**
 * Created by ${"李东宏"} on 2015/11/9.
 */
public class ShowAddressService extends Service {
    private TelephonyManager tm;
    private MyListener listener;
    private InnerOutCallReceiver innerOutCallReceiver;
    /***
     * 窗口管理器
     */
    private WindowManager wm;
    private View view;
    private WindowManager.LayoutParams params;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        innerOutCallReceiver = new InnerOutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(innerOutCallReceiver, filter);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(innerOutCallReceiver);
        innerOutCallReceiver = null;
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        if (wm != null) {
            wm.removeView(view);
        }
        tm = null;
        //赋值null，方便垃圾回收机制回收
        view = null;
        super.onDestroy();
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                    break;
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    if (view != null) {
                        wm.removeView(view);
                        view = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String address = NumberAddressDao.getAddress(incomingNumber);
                    //Toast.makeText(getApplicationContext(),address, Toast.LENGTH_LONG).show();
                    showMyToast(address);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = getResultData();
            String address = NumberAddressDao.getAddress(phone);
            //Toast.makeText(getApplicationContext(),address, Toast.LENGTH_LONG).show();
            showMyToast(address);
        }
    }

    /**
     * 自定义土司
     * @param address
     */
    private void showMyToast(String address) {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int which = getSharedPreferences("config", MODE_PRIVATE).getInt("which", 0);
        int[] bsg = {R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        view = View.inflate(ShowAddressService.this, R.layout.toast_address, null);
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i("++++", "按下");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        params.x += dx;
                        params.y += dy;
                        if (params.x<0)
                            params.x = 0;
                        if (params.y<0)
                            params.y = 0;
                        if (params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);
                        //初始化位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i("++++", "移動");
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        Log.i("++++", "鬆手");
                        break;
                }
                return true;
            }
        });
        view.setBackgroundResource(bsg[which]);
        TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_address.setText(address);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT + Gravity.TOP;
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);
        wm.addView(view,params);
    }
}
