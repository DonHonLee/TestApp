package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.db.dao.CommonNumberDao;

/**
 * Created by ${"李东宏"} on 2015/11/11.
 */
public class CommonNumberActivity extends Activity {
    private ExpandableListView elv;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/commonnum.db",
                null, SQLiteDatabase.OPEN_READONLY);
        elv = (ExpandableListView) findViewById(R.id.elv);
        elv.setAdapter(new MyExpandableListViewAdapter());
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(CommonNumberActivity.this,groupPosition + "---" + childPosition,Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    private class MyExpandableListViewAdapter implements ExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(db);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao.getChildrenCountByGroupPosition(db,groupPosition);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(CommonNumberActivity.this);
            }else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(20);
            tv.setTextColor(Color.RED);
            tv.setText("        " + CommonNumberDao.getGroupName(db,groupPosition));
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(CommonNumberActivity.this);
            }else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);
            tv.setText("        " + CommonNumberDao.getChildNameByGroupPosition(db,groupPosition,childPosition));
            return tv;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }



        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }



        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
