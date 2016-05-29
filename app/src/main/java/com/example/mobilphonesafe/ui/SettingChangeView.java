package com.example.mobilphonesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilphonesafe.R;

/**
 * Created by ${"李东宏"} on 2015/11/10.
 */
public class SettingChangeView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_desc;
    public SettingChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.mobilphonesafe", "titles");
        String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example.mobilphonesafe", "desc");
        tv_title.setText(title);
        tv_desc.setText(desc);
    }

    public SettingChangeView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param desc
     * 设置desc文本
     */
    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        View view = inflate(context, R.layout.ui_change_view, this);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
    }
}
