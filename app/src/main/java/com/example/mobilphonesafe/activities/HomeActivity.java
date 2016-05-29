package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.Md5Utils;
import com.example.mobilphonesafe.utils.ToastUtils;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class HomeActivity extends Activity {

    private GridView gv_home;
    private SharedPreferences sp;
    private static final String[] names = {"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private static int[] icons = {
            R.drawable.shoujifangdaonormal_selector, R.drawable.tongxunweishinormal_selector, R.drawable.ruanjianguanjianormal_selector,
            R.drawable.jinchengguanlinormal_seletor, R.drawable.liuliangtongjinormal_selector, R.drawable.shoujishadunormal_selector,
            R.drawable.huancunqinglinormal_seletor, R.drawable.gaojigongjunormal_seletor, R.drawable.shezhizhongxinnormal_selector
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AdManager.getInstance(this).init("49e5e50188bddcfa", "81aea01c9def26c9", true);
        setContentView(R.layout.activity_home);
        //#################广告插件##############
        //// 实例化广告条
        //AdView adViewTop = new AdView(this, AdSize.FIT_SCREEN);
        //// 获取要嵌入广告条的布局
        //LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
        //// 将广告条加入到布局中
        //adLayout.addView(adViewTop);
        //// 实例化 LayoutParams（重要）
        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT,
        //        FrameLayout.LayoutParams.WRAP_CONTENT);
        //
        //// 设置广告条的悬浮位置
        //layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
        //
        //// 实例化广告条
        //AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        //
        //// 调用 Activity 的 addContentView 函数
        //this.addContentView(adView, layoutParams);

        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter());
        sp = getSharedPreferences("config", MODE_PRIVATE);
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://手机防盗
                        String password = sp.getString("password", null);
                        if (TextUtils.isEmpty(password)) {
                            //弹出设置密码的对话框
                            showSetupPasswordDialog();
                        } else {
                            //弹出输入密码的对话框
                            showEnterPasswordDialog();
                        }
                        break;
                    case 1://添加黑名单
                        IntentUtils.startActivity(HomeActivity.this, CallSmsSafeActivity.class);
                        break;
                    case 2://软件管理
                        IntentUtils.startActivity(HomeActivity.this, AppManagerActivity.class);
                        break;
                    case 3://进程管理
                        IntentUtils.startActivity(HomeActivity.this, TaskManagerActivity.class);
                        break;
                    case 4://流量统计
                        IntentUtils.startActivity(HomeActivity.this, TrafficManagerActivity.class);
                        break;
                    case 5://手机杀毒
                        IntentUtils.startActivity(HomeActivity.this, AntiVirusActivity.class);
                        break;
                    case 6://缓存清理
                        IntentUtils.startActivity(HomeActivity.this, CleanCacheActivity.class);
                        break;
                    case 7://高级设置
                        IntentUtils.startActivity(HomeActivity.this, AToolsActivity.class);
                        break;
                    case 8://设置中心
                        IntentUtils.startActivity(HomeActivity.this, SettingActivity.class);
                        break;
                }
            }
        });
    }

    private AlertDialog dialog;
    private View view;
    private AlertDialog.Builder builder;
    private Button bt_ok;
    private Button bt_cancel;
    private EditText et_password;

    private void showEnterPasswordDialog() {
        view = View.inflate(HomeActivity.this, R.layout.dialog_enter_pwd, null);
        et_password = (EditText) view.findViewById(R.id.et_password);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String inputPassword = et_password.getText().toString().trim();
                String savedPassword = sp.getString("password", null);
                if (TextUtils.isEmpty(inputPassword)) {
                    ToastUtils.show(HomeActivity.this, "密码不能为空");
                    return;
                }
                //md5加密
                if (Md5Utils.encode(inputPassword).equals(savedPassword)) {
                    ToastUtils.show(HomeActivity.this, "密码输入正确，进入手机防盗界面");
                    dialog.dismiss();
                    boolean finishSetup = sp.getBoolean("finishSetup", false);
                    //判断用户是否完成设置界面
                    if (finishSetup) {
                        //是，进入手机防盗界面
                        IntentUtils.startActivity(HomeActivity.this, LostFindActivity.class);
                    } else {
                        //否，则进入设置向导
                        IntentUtils.startActivity(HomeActivity.this, Setup1Activity.class);
                    }
                } else {
                    ToastUtils.show(HomeActivity.this, "密码输入错误，请重新输入密码");
                }
            }
        });
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    private void showSetupPasswordDialog() {
        view = View.inflate(this, R.layout.dialog_setup_pwd, null);
        et_password = (EditText) view.findViewById(R.id.et_password);
        final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();
                String password_confirm = et_password_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)) {
                    ToastUtils.show(HomeActivity.this, "密码不能为空");
                    return;
                }
                if (!password.equals(password_confirm)) {
                    ToastUtils.show(HomeActivity.this, "两次密码不一致");
                    return;
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", Md5Utils.encode(password));
                editor.commit();
                dialog.dismiss();
                showEnterPasswordDialog();
            }
        });
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_homeitem_name);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
            tv_name.setText(names[position]);
            iv_icon.setImageResource(icons[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    public void iv_clear(View view) {
        IntentUtils.startActivity(HomeActivity.this, CleanCacheActivity.class);
    }
}

