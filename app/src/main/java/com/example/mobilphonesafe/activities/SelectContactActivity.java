package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.ContactInfoUtils;

import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class SelectContactActivity extends Activity {
    private ListView lv_contacts;
    private List<ContactInfoUtils.ContactInfo> infos;
    private LinearLayout ll_loading;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            lv_contacts.setAdapter(new ContactAdapter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                //获取手机里面全部的联系人信息
                infos = ContactInfoUtils.getContactInfos(SelectContactActivity.this);//当联系人数据很多的时候是个耗时的操作
                handler.sendEmptyMessage(1);
            }
        }.start();
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = infos.get(position).phone;
                Intent data = new Intent();
                data.putExtra("phone", phone);
                setResult(0, data);
                finish();
            }
        });
    }

    private class ContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SelectContactActivity.this, R.layout.item_contact, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            tv_name.setText(infos.get(position).name);
            tv_phone.setText(infos.get(position).phone);
            return view;
        }
    }
}
