package com.example.mobilphonesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class Setup3Activity extends SetupBaseActivity {
    private EditText et_setup_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_setup_phone = (EditText) findViewById(R.id.et_setup_phone);
        et_setup_phone.setText(sp.getString("safenumber",""));
    }

    @Override
    public void showNext() {
        String phone = et_setup_phone.getText().toString().replace("-", "").replace(" ","").trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(Setup3Activity.this, "电话号码不能为空");
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safenumber",phone);
        editor.commit();
        IntentUtils.startActivityAndFinish(this, Setup4Activity.class);
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinish(this,Setup2Activity.class);
    }

    public void selectContact(View view) {
        //选择联系人，开启一个新的界面
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone").replace("-", "").replace(" ","").trim();
            et_setup_phone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
