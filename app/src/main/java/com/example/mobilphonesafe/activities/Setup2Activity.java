package com.example.mobilphonesafe.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.ui.SettingCheckView;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class Setup2Activity extends SetupBaseActivity {
    private SettingCheckView scv_setup2_bind;
    private TelephonyManager tm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        scv_setup2_bind = (SettingCheckView) findViewById(R.id.scv_setup2_bind);
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            scv_setup2_bind.setChecked(false);
        } else {
            scv_setup2_bind.setChecked(true);
        }
        scv_setup2_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scv_setup2_bind.isCheck()) {
                    scv_setup2_bind.setChecked(false);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("sim", null);
                    editor.commit();
                } else {
                    scv_setup2_bind.setChecked(true);
                    String sim = tm.getSimSerialNumber();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("sim", sim);
                    editor.commit();

                }
            }
        });
    }

    @Override
    public void showNext() {
        if (scv_setup2_bind.isCheck()) {
            IntentUtils.startActivityAndFinish(this, Setup3Activity.class);
        } else {
            ToastUtils.show(this,"手机防盗功能需要绑定SIM卡,请绑定好再继续下面的步骤；否则退出");
        }

    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinish(this, Setup1Activity.class);
    }

}
