package com.example.mobilphonesafe.activities;

import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.domain.ProcessInfo;
import com.example.mobilphonesafe.engine.TaskInfoProvider;
import com.example.mobilphonesafe.utils.IntentUtils;
import com.example.mobilphonesafe.utils.SystemInfoUtils;
import com.example.mobilphonesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/20.
 */
public class TaskManagerActivity extends AppCompatActivity {
    //PackManager相当于windows系统的软件管理器，获取的是静态信息
    //ActivityManager相当于Windows系统的任务管理器获取的是动态信息
    private int CheckStatus = 0;
    private int runningProcessCount;
    private long availRam;
    private long totalRam;
    private TextView tv_process_count;
    private TextView tv_memory_info;
    private TextView tv_status;
    private LinearLayout ll_loading;
    private List<ProcessInfo> userProcessInfos;
    private List<ProcessInfo> systemProcessInfos;
    private ListView lv_taskmanger;
    private TaskManagerAdapter mTaskManagerAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTaskManagerAdapter = new TaskManagerAdapter();
            lv_taskmanger.setAdapter(mTaskManagerAdapter);
            if (!getSharedPreferences("config", MODE_PRIVATE).getBoolean("showSystem", true)) {
                if (userProcessInfos != null) {
                    runningProcessCount = userProcessInfos.size();
                    tv_process_count.setText("运行中的进程" + runningProcessCount + "个");
                }
            } else {
                runningProcessCount = SystemInfoUtils.getRunningProcessCount(TaskManagerActivity.this);
                tv_process_count.setText("运行中的进程" + runningProcessCount + "个");
            }
            ll_loading.setVisibility(View.INVISIBLE);
        }
    };
    private android.support.v7.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initActionBar();
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_taskmanger = (ListView) findViewById(R.id.lv_taskmanger);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
        tv_status = (TextView) findViewById(R.id.tv_status);
        runningProcessCount = SystemInfoUtils.getRunningProcessCount(TaskManagerActivity.this);
        availRam = SystemInfoUtils.getAvailRam(TaskManagerActivity.this);
        totalRam = SystemInfoUtils.getTotalRam(TaskManagerActivity.this);
        tv_memory_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availRam) + "/" + Formatter.formatFileSize(this, totalRam));
        fillData();

        //给listView添加滚动监听器
        lv_taskmanger.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userProcessInfos != null && systemProcessInfos != null) {
                    if (firstVisibleItem > userProcessInfos.size()) {
                        tv_status.setText("系统进程:" + systemProcessInfos.size()
                                + "个");
                    } else {
                        tv_status.setText("用户进程:" + userProcessInfos.size()
                                + "个");
                    }
                }
            }
        });

        //为listItem添加点击事件
        lv_taskmanger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo processInfo;
                if (position == 0) {
                    return;
                } else if (position == (userProcessInfos.size() + 1)) {
                    return;
                } else if (position <= userProcessInfos.size()) {
                    processInfo = userProcessInfos.get(position - 1);
                } else {
                    processInfo = systemProcessInfos.get(position - userProcessInfos.size() - 2);
                }
                if (processInfo.getPackName().equals(getPackageName())) {
                    return;
                }
                if (processInfo.isChecked()) {
                    processInfo.setChecked(false);
                } else {
                    processInfo.setChecked(true);
                }
                mTaskManagerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                List<ProcessInfo> infos = TaskInfoProvider.getRunningProcessInfos(TaskManagerActivity.this);
                userProcessInfos = new ArrayList<ProcessInfo>();
                systemProcessInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : infos) {
                    if (info.isUserTask()) {
                        //用户进程
                        userProcessInfos.add(info);
                    } else {
                        //系统进行
                        systemProcessInfos.add(info);
                    }
                }
                Log.w("+++++", "xitong" + userProcessInfos.size() + "/" + systemProcessInfos.size());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("showSystem", true)) {
                return userProcessInfos.size() + systemProcessInfos.size() + 2;
            } else {
                return userProcessInfos.size() + 1;
            }

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
            ProcessInfo processInfo;
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setText("用户进程：" + userProcessInfos.size() + "个");
                return textView;
            } else if (position == (userProcessInfos.size() + 1)) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
                textView.setText("系统进程：" + systemProcessInfos.size() + "个");
                return textView;
            } else if (position <= userProcessInfos.size()) {
                processInfo = userProcessInfos.get(position - 1);
            } else {
                processInfo = systemProcessInfos.get(position - userProcessInfos.size() - 2);
            }
            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_taskinfo, null);
                holder = new ViewHolder();
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_memsize);
                holder.cb = (CheckBox) view.findViewById(R.id.cb);
                view.setTag(holder);
            }
            holder.iv_appicon.setImageDrawable(processInfo.getIcon());
            holder.tv_appname.setText(processInfo.getAppName());
            holder.tv_memsize.setText("内存占用：" + Formatter.formatFileSize(getApplicationContext(), processInfo.getMemSize()));
            holder.cb.setChecked(processInfo.isChecked());
            if (processInfo.getPackName().equals(getPackageName())) {
                holder.cb.setVisibility(View.INVISIBLE);
            } else {
                holder.cb.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_memsize;
        CheckBox cb;
    }

    /**
     * 全选
     * @param view
     */
    public void selectAll(View view) {
        for (ProcessInfo info : userProcessInfos) {
            if (info.getPackName().equals(getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        for (ProcessInfo info : systemProcessInfos) {
            info.setChecked(true);
            mTaskManagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 反选
     * @param view
     */
    public void selectOpposite(View view) {
        for (ProcessInfo info : userProcessInfos) {
            if (info.getPackName().equals(getPackageName())) {
                continue;
            }
            info.setChecked(false);
        }
        for (ProcessInfo info : systemProcessInfos) {
            info.setChecked(false);
            mTaskManagerAdapter.notifyDataSetChanged();
        }
    }

    public void killSelect(View view) {
        int count = 0;
        long saveMem = 0;
        List<ProcessInfo> killedProcess = new ArrayList<ProcessInfo>();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ProcessInfo info : userProcessInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackName());
                count++;
                saveMem += info.getMemSize();
                killedProcess.add(info);
            }
        }
        for (ProcessInfo info : systemProcessInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackName());
                count++;
                saveMem += info.getMemSize();
                killedProcess.add(info);
            }
        }
        for (ProcessInfo info : killedProcess) {
            if (info.isUserTask()) {
                userProcessInfos.remove(info);
            } else {
                systemProcessInfos.remove(info);
            }
        }
        ToastUtils.show(this, "关闭了" + count + "个进程，释放了" + Formatter.formatFileSize(this, saveMem) + "内存");
        mTaskManagerAdapter.notifyDataSetChanged();
        runningProcessCount -= count;
        availRam += saveMem;
        tv_process_count.setText("运行中的进程" + runningProcessCount + "个");
        tv_memory_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availRam) + "/" + Formatter.formatFileSize(this, totalRam));
    }

    public void openSetting(View view) {
        IntentUtils.startActivity(TaskManagerActivity.this, TaskManagerSettingActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mTaskManagerAdapter != null) {
            mTaskManagerAdapter.notifyDataSetChanged();
        }
        if (!getSharedPreferences("config", MODE_PRIVATE).getBoolean("showSystem", true)) {
            if (userProcessInfos != null) {
                runningProcessCount = userProcessInfos.size();
                tv_process_count.setText("运行中的进程" + runningProcessCount + "个");
            }
        } else {
            runningProcessCount = SystemInfoUtils.getRunningProcessCount(TaskManagerActivity.this);
            tv_process_count.setText("运行中的进程" + runningProcessCount + "个");
        }
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_task);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
