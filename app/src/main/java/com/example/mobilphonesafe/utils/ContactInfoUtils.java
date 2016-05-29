package com.example.mobilphonesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${"李东宏"} on 2015/10/26.
 */
public class ContactInfoUtils {
    /**
     * 联系人信息的工具类
     *
     * @param context
     * @return
     */
    public static List<ContactInfo> getContactInfos(Context context) {
        //内容提供者的解析器
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            if (id != null) {
                ContactInfo info = new ContactInfoUtils().new ContactInfo();
                Cursor dataCursor = resolver.query(dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);
                    if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        //姓名
                        info.name = data1;
                    } else if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
                        //邮箱
                        info.email = data1;
                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        //号码
                        info.phone = data1;
                    }
                }
                contactInfos.add(info);
            }
        }
        return contactInfos;
    }

    public class ContactInfo {
        public String name;
        public String phone;
        public String email;
    }
}
