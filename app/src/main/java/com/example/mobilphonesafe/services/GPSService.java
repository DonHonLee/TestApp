package com.example.mobilphonesafe.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by ${"李东宏"} on 2015/10/30.
 */
public class GPSService extends Service {
    LocationManager lm;
    private static String TAG = "SmsReceiver";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "开启服务");
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(provider, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "监听器");
                location.getLongitude(); //经度
                location.getLatitude();  //纬度
                String text = ("J:"+location.getLongitude()+"W:"+location.getLatitude());
                SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, text, null, null);
                lm.removeUpdates(this);
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }
}
