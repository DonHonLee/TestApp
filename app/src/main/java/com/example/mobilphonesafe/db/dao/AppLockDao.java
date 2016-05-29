package com.example.mobilphonesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mobilphonesafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/11/23.
 */
public class AppLockDao {
    private AppLockDBOpenHelper helper;
    private Context context;
    public AppLockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
        this.context = context;
    }

    /**
     * 添加加锁的程序包名
     * @param packName 包名
     * @return
     */
    public boolean add(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("packName", packName);
        long result = db.insert("lockInfo", null, value);
        db.close();
        if (result != -1) {
            Uri uri = Uri.parse("content://com.example.mobilphonesafe.applockchang");
            context.getContentResolver().notifyChange(uri, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除加锁的程序包名
     * @param packName 包名
     * @return
     */
    public boolean delete(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.delete("lockInfo", "packName = ?", new String[]{packName});
        db.close();
        if (result >0) {
            Uri uri = Uri.parse("content://com.example.mobilphonesafe.applockchang");
            context.getContentResolver().notifyChange(uri, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询加锁的程序包名
     * @param packName 包名
     * @return
     */
    public boolean query(String packName) {
        boolean result;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockInfo", null, "packName=?", new String[]{packName}, null, null,"_id desc");
        result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部锁定的应用程序包名
     * @return 返回一个String类型的集合
     */
    public List<String> findAll(){
        List<String> packnames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockInfo", new String[]{"packName"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
