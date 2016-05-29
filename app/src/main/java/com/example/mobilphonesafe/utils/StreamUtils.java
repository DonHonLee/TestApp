package com.example.mobilphonesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ${"李东宏"} on 2015/10/23.
 */
public class StreamUtils {
    /**
     * 把一个流的内容读取出来转化成字符串
     *
     * @param is 输入流
     * @return 解析成功返回字符串 解析失败返回null
     */
    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 把一个流的内容写到某个文件里面
     * @param is 输入流
     * @param file 文件位置
     * @param fileName 输入到哪个文件里面
     */
    public static void readStream(InputStream is,File file,String fileName) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            FileOutputStream fos = new FileOutputStream(file);
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer,0,len);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

