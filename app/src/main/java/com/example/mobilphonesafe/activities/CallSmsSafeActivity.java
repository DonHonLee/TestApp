package com.example.mobilphonesafe.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.BlackNumberDao;
import com.example.mobilphonesafe.domain.BlackNumberInfo;
import com.example.mobilphonesafe.utils.ToastUtils;

import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/10/30.
 */
public class CallSmsSafeActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private ListView lv_blacknumber;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> infos;
    private AllBlackNumberAdapter adapter;
    private LinearLayout ll_loading;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            if (adapter == null) {
                adapter = new AllBlackNumberAdapter();
                lv_blacknumber.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };
    private int startIndex = 0;
    private int maxCount = 20;
    private int totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsmssafe);
        initActionBar();
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        dao = new BlackNumberDao(this);
        totalCount = dao.getTotalCount();
        fillData();
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int position = lv_blacknumber.getLastVisiblePosition();
                int size = infos.size();
                if (position == size - 1) {
                    Log.i("+++++", "ListView到达底部，需要更新新的数据");
                    startIndex += maxCount;
                    if (startIndex >= totalCount) {
                        ToastUtils.show(CallSmsSafeActivity.this, "数据全部加载完成");
                    }
                    fillData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_call);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                if (infos == null) {
                    //第一次获取数据时从0开始显示
                    infos = dao.findPart(startIndex, maxCount);//当数据过多的时候是个耗时的操作
                } else {
                    //之后的数据都是从后面添加进去
                    infos.addAll(dao.findPart(startIndex, maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    /**
     * 添加黑名单的对话框
     *
     * @param
     */
    public void addBlackNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        final EditText et_black_number = (EditText) dialogView.findViewById(R.id.et_black_number);
        final RadioGroup rg_mode = (RadioGroup) dialogView.findViewById(R.id.rg_mode);
        Button bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);

        final AlertDialog dialog = builder.create();
        dialog.setView(dialogView, 0, 0, 0, 0);
        dialog.show();
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_black_number.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.show(CallSmsSafeActivity.this, "添加的黑名单不能为空");
                    return;
                }
                int id = rg_mode.getCheckedRadioButtonId();
                String mode = "3";
                switch (id) {
                    case R.id.rb_phone:
                        mode = "1";
                        break;
                    case R.id.rb_sms:
                        mode = "2";
                        break;
                    case R.id.rb_all:
                        mode = "3";
                        break;
                }
                boolean result = dao.add(phone, mode);
                //刷新界面
                if (result) {
                    ToastUtils.show(CallSmsSafeActivity.this, "添加成功");
                    BlackNumberInfo object = new BlackNumberInfo();
                    object.setPhone(phone);
                    object.setMode(mode);
                    infos.add(0, object);
                    //通知listView刷新界面
                    //lv_blacknumber.setAdapter(new AllBlackNumberAdapter()); 这样刷新界面会导致界面回到第一个条目
                    adapter.notifyDataSetChanged();//通知listView界面更新
                } else {
                    ToastUtils.show(CallSmsSafeActivity.this, "添加失败");
                }
                dialog.dismiss();
            }
        });
    }

    private class AllBlackNumberAdapter extends BaseAdapter {

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

        /**
         * @param position
         * @param convertView 历史回收的view对象。当某个view对象被完全移除屏幕的时候
         * @param parent
         * @return 1.尽量的复用convertview（历史缓存的view），减少view对象创建的个数
         * 2.尽量的减少子孩子id的查询次数，定义一个viewHolder
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView != null) {
                //复用历史view对象
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_callsmssafe, null);
                //每次寻找子孩子都会消耗很多cpu、内存
                //只有当子view对象第一次创建的时候查询id
                holder = new ViewHolder();
                holder.tv_black_phone = (TextView) view.findViewById(R.id.tv_black_phone);
                holder.tv_black_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.iv_delete_blacknumber = (ImageView) view.findViewById(R.id.iv_delete_blacknumber);
                view.setTag(holder);
            }
            final String phone = infos.get(position).getPhone();
            holder.tv_black_phone.setText(phone);
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)) {
                holder.tv_black_mode.setText("电话拦截");
            } else if ("2".equals(mode)) {
                holder.tv_black_mode.setText("短信拦截");
            } else if ("3".equals(mode)) {
                holder.tv_black_mode.setText("全部拦截");
            }
            holder.iv_delete_blacknumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定删除这个黑名单号码么?");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int which) {
                                    // 删除数据库的记录
                                    boolean result = dao.delete(phone);
                                    if (result) {
                                        ToastUtils.show(CallSmsSafeActivity.this, "删除成功");
                                        // 删除ui界面的数据
                                        infos.remove(position);
                                        // 刷新界面
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        ToastUtils.show(CallSmsSafeActivity.this, "删除失败");
                                    }
                                }
                            });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });
            return view;
        }
    }

    /**
     * static减少字节码的装载次数
     * 子孩子的id容器
     */
    static class ViewHolder {
        TextView tv_black_phone;
        TextView tv_black_mode;
        ImageView iv_delete_blacknumber;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_bar_menu:
                addBlackNumber();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
