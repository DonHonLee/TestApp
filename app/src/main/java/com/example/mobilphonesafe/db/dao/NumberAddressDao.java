package com.example.mobilphonesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ${"李东宏"} on 2015/11/4.
 */
public class NumberAddressDao {
    /**
     * 返回号码归属地信息
     * @param number 电话号码
     * @return 返回归属地信息
     */
    public static String getAddress(String number) {
        String location = number;
        //检测number是手机号码还是固定电话，采用正则表达式
        //^1[3578]\d{9}$
        if (number.matches("^1[3578]\\d{9}$")) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.mobilphonesafe/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{number.substring(0, 7)});
            if (cursor.moveToFirst()) {
                location = cursor.getString(0);
            }
            cursor.close();
            db.close();
        } else {
            switch (number.length()) {
                case 3:
                    if ("110".equals(number)) {
                        return "匪警";
                    } else if ("120".equals(number)) {
                        return "急救电话";
                    } else if ("119".equals(number)) {
                        return "火警";
                    }
                    break;
                case 4:
                    return "模拟器";
                case 5:
                    return "客服电话";
                case 7:
                    if ((!number.startsWith("0")) && (!number.startsWith("1"))) {
                        return "本地号码";
                    }
                    break;
                case 8:
                    if ((!number.startsWith("0")) && (!number.startsWith("1"))) {
                        return "本地号码";
                    }
                    break;

                default:
                    if(number.length()>=10&&number.startsWith("0")){
                        //长途电话
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                                "/data/data/com.example.mobilphonesafe/files/address.db", null,
                                SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
                        if(cursor.moveToNext()){
                            location = cursor.getString(0).substring(0, cursor.getString(0).length()-2);
                        }
                        cursor.close();
                        cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
                        if(cursor.moveToNext()){
                            location = cursor.getString(0).substring(0, cursor.getString(0).length()-2);
                        }
                        cursor.close();
                        db.close();
                    }
                    break;
            }
        }

        return location;
    }
}
