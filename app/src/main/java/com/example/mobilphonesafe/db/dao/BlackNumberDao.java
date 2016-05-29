package com.example.mobilphonesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilphonesafe.db.BlackNumberDBOpenHelper;
import com.example.mobilphonesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作黑名单数据库,提供增删改查的方法.
 * Created by ${"李东宏"} on 2015/10/30.
 */
public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;

    /**
     * 黑名单dao的构造方法
     *
     * @param context 上下文
     */
    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 添加黑名单号码
     *
     * @param phone 黑名单的号码
     * @param mode  黑名单的拦截模式
     * @return 返回true则添加成功，返回false则添加失败
     */
    public boolean add(String phone, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("phone", phone);
        value.put("mode", mode);
        long id = db.insert("blackNumberInfo", null, value);
        db.close();
        if (id != -1) {
            return true;
        }
        return false;
    }

    /**
     * 删除黑名单
     *
     * @param phone 要删除的黑名单号码
     * @return 返回true则删除成功，返回false则删除失败
     */
    public boolean delete(String phone) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowId = db.delete("blackNumberInfo", "phone=?", new String[]{phone});
        db.close();
        if (rowId == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param newMode 新的拦截模式
     * @return 返回true则更新成功  返回false则更新失败
     */
    public boolean update(String phone, String newMode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("mode", newMode);
        int rowId = db.update("blackNumberInfo", value, "phone=?", new String[]{phone});
        db.close();
        if (rowId == 0) {
            return false;
        } else {
            return true;
        }
    }

    public String find(String phone) {
        String mode = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blackNumberInfo", null, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(cursor.getColumnIndex("mode"));
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 获取全部的黑名单信息
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.query("blackNumberInfo", null, null, null, null, null, "_id desc");
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            info.setPhone(phone);
            info.setMode(mode);
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }

    /**
     * 分批显示黑名单号码号码的信息
     *
     * @param startIndex 开始的位置
     * @param maxCount   最多获取多少条数据
     * @return 返回一个List<BlackNumberInfo>
     */
    public List<BlackNumberInfo> findPart(int startIndex, int maxCount) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.rawQuery("select _id,phone,mode from blackNumberInfo order by _id desc limit ? offset ?", new String[]{
                String.valueOf(maxCount), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            info.setPhone(phone);
            info.setMode(mode);
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }


    /**
     * 分页显示
     * @param pageNumber 页码号
     * @return
     */
    public List<BlackNumberInfo> findPage(int pageNumber) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.rawQuery("select _id,phone,mode from blackNumberInfo order by _id desc limit ? offset ?", new String[]{
                String.valueOf(20), String.valueOf(20*pageNumber)});
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            info.setPhone(phone);
            info.setMode(mode);
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }

    public int getTotalCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.rawQuery("select count(*) from blackNumberInfo",null);
        cursor.moveToNext();
        int totalCount = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalCount;
    }
}
