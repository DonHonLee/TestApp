package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.NumberAddressDao;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/11/5.
 */
public class NumberAddressQueryActivity extends Activity {
    private EditText et_number;
    private TextView address_result;
    private String phone = null;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        et_number = (EditText) findViewById(R.id.et_number);
        address_result = (TextView) findViewById(R.id.address_result);
    }

    public void query(View view) {
        phone = et_number.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_number.startAnimation(shake);
            vibrator.vibrate(1000);
            ToastUtils.show(NumberAddressQueryActivity.this, "号码不能为空");
            return;
        }
        String address = NumberAddressDao.getAddress(phone);
        address_result.setText(address);
    }
}
