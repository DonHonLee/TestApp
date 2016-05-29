package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.mobilphonesafe.R;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public abstract class SetupBaseActivity extends Activity {
    public SharedPreferences sp;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
                    return true;
                }
                if ((e1.getRawX() - e2.getRawX() > 150)) {
                    showNext();
                    overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
                }
                if ((e2.getRawX() - e1.getRawX()) > 150) {
                    showPre();
                    overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void showNext();

    public void next(View view) {
        showNext();
        overridePendingTransition(R.anim.trans_next_in, R.anim.trans_next_out);
    }

    public abstract void showPre();

    public void pre(View view) {
        showPre();
        overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
    }
}
