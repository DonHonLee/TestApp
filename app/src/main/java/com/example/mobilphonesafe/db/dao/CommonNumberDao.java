package com.example.mobilphonesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ${"李东宏"} on 2015/11/12.
 */
public class CommonNumberDao {
    /**
     * 返回数据库一共有几个大的分组
     * @return
     */
    public static int getGroupCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from classlist", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * @param groupPosition 根据分组的位置查询孩子的个数
     * @return
     */
    public static int getChildrenCountByGroupPosition(SQLiteDatabase db,int groupPosition) {
        int newPosition = groupPosition + 1;
        String tableName = "table" + newPosition;
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);
        cursor.moveToNext();
        int childCount = cursor.getInt(0);
        cursor.close();
        return childCount;
    }

    /**
     * @param groupPosition
     * @return 返回大分组的名称
     */
    public static String getGroupName(SQLiteDatabase db,int groupPosition) {
        int newPosition = groupPosition + 1;
        Cursor cursor = db.rawQuery("select name from classlist where idx = ?",
                new String[]{String.valueOf(newPosition)});
        cursor.moveToNext();
        String groupName = cursor.getString(0);
        cursor.close();
        return groupName;
    }

    /**
     * 根据分组的位置和孩子的位置查询孩子的名称
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public static String getChildNameByGroupPosition(SQLiteDatabase db,int groupPosition, int childPosition) {
        int newGroupPosition = groupPosition + 1;
        int newChildPosition = childPosition + 1;
        String tableName = "table" + newGroupPosition;
        Cursor cursor = db.rawQuery("select name,number from " + tableName + " where _id= ?", new String[]{String.valueOf(newChildPosition)});
        cursor.moveToNext();
        String name = cursor.getString(0);
        String number = cursor.getString(1);
        cursor.close();
        return name + "\n       " + number;
    }
}
