package com.example.mobilphonesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilphonesafe.R;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class SettingCheckView extends LinearLayout {

    private CheckBox cb_status;

    public SettingCheckView(Context context) {
        super(context);
        initView(context);
    }

    public SettingCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String bigTitle = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.mobilphonesafe", "bigTitle");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(bigTitle);
    }

    private void initView(Context context) {
        this.setOrientation(VERTICAL);
        this.addView(View.inflate(context, R.layout.ui_setting_view, null));
        cb_status = (CheckBox) findViewById(R.id.cb_status);
    }

    /**
     * 判断组件是否被选中
     * @return
     */
    public boolean isCheck() {
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的选择状态
     * @param checked
     */
    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);
    }
}

