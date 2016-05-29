package com.example.mobilphonesafe.activities;

import android.os.Bundle;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.IntentUtils;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class Setup1Activity extends SetupBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showNext() {
        IntentUtils.startActivityAndFinish(this, Setup2Activity.class);
    }

    @Override
    public void showPre() {

    }
}
