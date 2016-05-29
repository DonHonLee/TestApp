package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.AppLockDao;
import com.example.mobilphonesafe.domain.AppInfo;
import com.example.mobilphonesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/23.
 */
public class AppLockActivity extends Activity implements View.OnClickListener {
    /**
     * 未加锁的应用个数
     */
    private TextView tv_unLock_count;
    /**
     * 已加锁的应用个数
     */
    private TextView tv_lock_count;

    private TextView tv_unLock;
    private TextView tv_locked;
    /**
     * 未加锁的linearLayout
     */
    private LinearLayout ll_unLock;
    /**
     * 已加锁的linearLayout
     */
    private LinearLayout ll_locked;
    /**
     * 未加锁的listView
     */
    private ListView lv_unLock;
    /**
     * 已加锁的listView
     */
    private ListView lv_locked;
    /**
     * 所有的应用程序集合
     */
    private List<AppInfo> allAppinfos;
    /**
     * 未加锁的应用程序集合
     */
    private List<AppInfo> unLockAppinfos;
    /**
     * 已加锁的应用程序集合
     */
    private List<AppInfo> lockAppinfos;
    private AppLockDao dao;
    private AppLockAdapter unLockAdapter;
    private AppLockAdapter lockAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        dao = new AppLockDao(getApplicationContext());
        tv_unLock_count = (TextView) findViewById(R.id.tv_unLock_count);
        tv_lock_count = (TextView) findViewById(R.id.tv_lock_count);

        tv_unLock = (TextView) findViewById(R.id.tv_unLock);
        tv_locked = (TextView) findViewById(R.id.tv_locked);
        ll_unLock = (LinearLayout) findViewById(R.id.ll_unLock);
        ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
        tv_locked.setOnClickListener(this);
        tv_unLock.setOnClickListener(this);

        allAppinfos = AppInfoProvider.getAppInfos(this);
        unLockAppinfos = new ArrayList<AppInfo>();
        lockAppinfos = new ArrayList<AppInfo>();
        for (AppInfo info : allAppinfos) {
            if (dao.query(info.getPackName())) {
                //已加锁的应用
                lockAppinfos.add(info);
            } else {
                //未加锁的应用
                unLockAppinfos.add(info);
            }
        }
        lv_unLock = (ListView) findViewById(R.id.lv_unLock);
        unLockAdapter = new AppLockAdapter(true);
        lv_unLock.setAdapter(unLockAdapter);

        lv_locked = (ListView) findViewById(R.id.lv_locked);
        lockAdapter = new AppLockAdapter(false);
        lv_locked.setAdapter(lockAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unLock:
                tv_unLock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_locked.setBackgroundResource(R.drawable.tab_right_default);
                ll_unLock.setVisibility(View.VISIBLE);
                ll_locked.setVisibility(View.GONE);
                break;
            case R.id.tv_locked:
                tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_unLock.setBackgroundResource(R.drawable.tab_left_default);
                ll_locked.setVisibility(View.VISIBLE);
                ll_unLock.setVisibility(View.GONE);
                break;
        }
    }

    private class AppLockAdapter extends BaseAdapter {
        boolean isLocked;

        public AppLockAdapter(boolean isLocked) {
            this.isLocked = isLocked;
        }

        @Override
        public int getCount() {
            int count;
            if (isLocked) {
                count = unLockAppinfos.size();
                tv_unLock_count.setText("未加锁的软件" + count + "个");
            } else {
                count = lockAppinfos.size();
                tv_lock_count.setText("已加锁的软件" + count + "个");
            }
            return count;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_app_unlock, null);
                holder = new ViewHolder();
                holder.iv_applock_icon = (ImageView) view.findViewById(R.id.iv_appUnlockIcon);
                holder.tv_applock_name = (TextView) view.findViewById(R.id.tv_appUnlockName);
                holder.iv_applock_status = (ImageView) view.findViewById(R.id.iv_unlock_status);
                view.setTag(holder);
            }
            final AppInfo appInfo;
            if (isLocked) {
                appInfo = unLockAppinfos.get(position);
                holder.iv_applock_status.setImageResource(R.drawable.list_button_lock_pressed);
            } else {
                appInfo = lockAppinfos.get(position);
                holder.iv_applock_status.setImageResource(R.drawable.list_button_unlock_pressed);
            }
            holder.iv_applock_icon.setImageDrawable(appInfo.getAppIcon());
            holder.tv_applock_name.setText(appInfo.getAppName());
            holder.iv_applock_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLocked) {
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1.0f,
                                Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(100);
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                unLockAppinfos.remove(appInfo);
                                lockAppinfos.add(appInfo);
                                dao.add(appInfo.getPackName());
                                unLockAdapter.notifyDataSetChanged();
                                lockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {

                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-1.0f,
                                Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(100);
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                lockAppinfos.remove(appInfo);
                                unLockAppinfos.add(appInfo);
                                dao.delete(appInfo.getPackName());
                                unLockAdapter.notifyDataSetChanged();
                                lockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                }
            });
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

    static class ViewHolder{
        ImageView iv_applock_icon;
        TextView tv_applock_name;
        ImageView iv_applock_status;
    }
}
