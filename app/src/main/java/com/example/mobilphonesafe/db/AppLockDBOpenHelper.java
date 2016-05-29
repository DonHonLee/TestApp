package com.example.mobilphonesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ${"李东宏"} on 2015/10/30.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 创建一个应用程序的数据库,数据库文件的名称叫lock.db
     * @param context 上下文
     */
    public AppLockDBOpenHelper(Context context) {
        super(context, "lock.db", null, 1);
    }

    /**
     * @param db
     */
    //当数据库第一次被创建的时候调用下面的方法,适合做数据库表结构的初始化
    @Override
    public void onCreate(SQLiteDatabase db) {
        //_id数据库的主键,自增长
        //phone 黑名单电话号码
        //mode 拦截模式 1 电话拦截 2短信拦截 3全部拦截
        db.execSQL("create table lockInfo (_id integer primary key autoincrement,packName varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
