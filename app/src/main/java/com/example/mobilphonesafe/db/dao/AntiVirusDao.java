package com.example.mobilphonesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ${"李东宏"} on 2015/11/28.
 * 查看文件是否问病毒文件
 */
public class AntiVirusDao {
    /**
     * @param md5 扫描文件的md5信息
     * @return 返回Null表示安全文件，否则为病毒文件
     */
    public static String isVirus(String md5) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        String desc = null;
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }

    /**
     * 获取本地病毒库的版本号
     * @return 返回本地版本号
     */
    public static int getVersion() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        int version =0;
        Cursor cursor = db.rawQuery("select subcnt from version ", null);
        while (cursor.moveToNext()) {
            version = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return version;
    }

    /**
     * 更新病毒库的版本号
     * @param newVersion 新版本号
     */
    public static void setVersion(int newVersion) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues value = new ContentValues();
        value.put("subcnt", newVersion);
        db.update("version", value, null, null);
        db.close();
    }

    public static void addVirusInfo(String md5,String type,String name,String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues value = new ContentValues();
        value.put("md5", md5);
        value.put("type", type);
        value.put("name", name);
        value.put("desc", desc);
        db.insert("datable", null, value);
        db.close();
    }
}
