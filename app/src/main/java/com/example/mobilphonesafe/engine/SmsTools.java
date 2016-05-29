package com.example.mobilphonesafe.engine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * Created by ${"李东宏"} on 2015/11/13.
 */
public class SmsTools {

    /**
     * 声明一个接口，包含一些回调函数
     *
     * @author Jocker
     */
    public interface BackupSmsCallBack {
        /**
         * 短信备份之前调用的方法
         *
         * @param max 短信的总条目个数
         */
        void beforeSmsBackup(int max);

        /**
         * 当短信备份的过程中调用的方法
         *
         * @param process 当前的备份进度
         */
        void onSmsBackup(int process);
    }


    /**
     * 备份短信的业务类
     *
     * @param context           上下文
     * @param backupSmsCallBack 短信备份实现的接口
     * @param fileName          备份到哪个文件
     * @return
     */
    public static boolean SmsBackup(Context context, BackupSmsCallBack backupSmsCallBack, String fileName) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            String path = Environment.getExternalStorageDirectory().getPath() + "/MobilPhoneSafe";
            File files = new File(path);
            if (!files.exists()) {
                files.mkdirs();
            }
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body","read"}, null, null, null);
            int max = cursor.getCount();
            File file = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            KXmlSerializer xmlSerializer = new KXmlSerializer();
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "info");
            xmlSerializer.attribute(null, "total", String.valueOf(max));
            xmlSerializer.text("\n      ");

            backupSmsCallBack.beforeSmsBackup(max);
            int process = 0;
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");
                xmlSerializer.text("\n      ");

                xmlSerializer.startTag(null, "address");
                String address = cursor.getString(0);
                xmlSerializer.text(address);
                xmlSerializer.endTag(null, "address");

                xmlSerializer.text("\n      ");
                xmlSerializer.startTag(null, "date");
                String date = cursor.getString(1);
                xmlSerializer.text(date);
                xmlSerializer.endTag(null, "date");

                xmlSerializer.text("\n      ");
                String type = cursor.getString(2);
                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(type);
                xmlSerializer.endTag(null, "type");

                xmlSerializer.text("\n      ");
                String body = cursor.getString(3);
                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(body);
                xmlSerializer.endTag(null, "body");

                xmlSerializer.text("\n      ");
                String read = cursor.getString(4);
                xmlSerializer.startTag(null, "read");
                xmlSerializer.text(read);
                xmlSerializer.endTag(null, "read");

                xmlSerializer.text("\n      ");
                xmlSerializer.endTag(null, "sms");
                xmlSerializer.text("\n\n    ");

                process++;
                backupSmsCallBack.onSmsBackup(process);
            }
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "info");
            xmlSerializer.endDocument();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean SmsReduction(final Context context, BackupSmsCallBack backupSmsCallBack, final String fileName) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            ContentValues values = null;
            String path = Environment.getExternalStorageDirectory().getPath() + "/MobilPhoneSafe";
            File files = new File(path);
            if (!files.exists()) {
                files.mkdirs();
            }
            File file = new File(path, fileName);
            if (file.exists() && file.length() > 0) {
                FileInputStream is = new FileInputStream(file);
                XmlPullParser parser = Xml.newPullParser();
                //InputStream is = getAssets().open("SmsBackup.xml");
                parser.setInput(is, "UTF-8");
                int eventType = parser.getEventType();
                int i = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            Log.w("+++++", "开始解析");
                            eventType = parser.next();
                            break;
                        case XmlPullParser.START_TAG:
                            if (parser.getName().equals("info")) {
                                parser.getAttributeCount();
                                int total = Integer.parseInt(parser.getAttributeValue(null, "total"));
                                backupSmsCallBack.beforeSmsBackup(total);
                                Log.w("+++++", "info解析");
                            } else if (parser.getName().equals("sms")) {
                                values = new ContentValues();
                                Log.w("+++++", "sms解析");
                            } else if (parser.getName().equals("address")) {
                                values.put("address", parser.nextText());
                                Log.w("+++++", "address解析");
                            } else if (parser.getName().equals("date")) {
                                values.put("date", parser.nextText());
                                Log.w("+++++", "date解析");
                            } else if (parser.getName().equals("type")) {
                                values.put("type", parser.nextText());
                                Log.w("+++++", "type解析");
                            } else if (parser.getName().equals("body")) {
                                values.put("body", parser.nextText());
                                Log.w("+++++", "body解析");
                            }else if (parser.getName().equals("read")) {
                                values.put("read", parser.nextText());
                                Log.w("+++++", "read解析");
                            }
                            eventType = parser.nextTag();
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("sms")) {
                                Log.w("+++++", "sms添加数据" + i);
                                i++;
                                backupSmsCallBack.onSmsBackup(i);
                                resolver.insert(uri, values);
                                values = null;
                                eventType = parser.nextTag();
                            } else if (parser.getName().equals("info")) {
                                Log.w("+++++", "info解析结束");
                                eventType = parser.next();
                                is.close();
                            }
                            break;
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解决XMLPULL不能处理特殊字符的问题,重写XmlSerializer
     */
    public static class KXmlSerializer implements XmlSerializer {

        //    static final String UNDEFINED = ":";

        // BEGIN android-added
        /**
         * size (in characters) for the write buffer
         */
        private static final int WRITE_BUFFER_SIZE = 500;
        // END android-added

        // BEGIN android-changed
        // (Guarantee that the writer is always buffered.)
        private BufferedWriter writer;
        // END android-changed

        private boolean pending;
        private int auto;
        private int depth;

        private String[] elementStack = new String[12];
        //nsp/prefix/name
        private int[] nspCounts = new int[4];
        private String[] nspStack = new String[8];
        //prefix/nsp; both empty are ""
        private boolean[] indent = new boolean[4];
        private boolean unicode;
        private String encoding;

        private final void check(boolean close) throws IOException {
            if (!pending) {
                return;
            }

            depth++;
            pending = false;

            if (indent.length <= depth) {
                boolean[] hlp = new boolean[depth + 4];
                System.arraycopy(indent, 0, hlp, 0, depth);
                indent = hlp;
            }
            indent[depth] = indent[depth - 1];

            for (int i = nspCounts[depth - 1]; i < nspCounts[depth]; i++) {
                writer.write(' ');
                writer.write("xmlns");
                if (!StringUtils.isEmpty(nspStack[i * 2])) {
                    writer.write(':');
                    writer.write(nspStack[i * 2]);
                } else if (StringUtils.isEmpty(getNamespace()) && !StringUtils.isEmpty(nspStack[i * 2 + 1])) {
                    throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
                }
                writer.write("=\"");
                writeEscaped(nspStack[i * 2 + 1], '"');
                writer.write('"');
            }

            if (nspCounts.length <= depth + 1) {
                int[] hlp = new int[depth + 8];
                System.arraycopy(nspCounts, 0, hlp, 0, depth + 1);
                nspCounts = hlp;
            }

            nspCounts[depth + 1] = nspCounts[depth];
            //   nspCounts[depth + 2] = nspCounts[depth];

            writer.write(close ? " />" : ">");
        }

        private final void writeEscaped(String s, int quot) throws IOException {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\n':
                    case '\r':
                    case '\t':
                        if (quot == -1) {
                            writer.write(c);
                        } else {
                            writer.write("&#" + ((int) c) + ';');
                        }
                        break;
                    case '&':
                        writer.write("&amp;");
                        break;
                    case '>':
                        writer.write("&gt;");
                        break;
                    case '<':
                        writer.write("&lt;");
                        break;
                    default:
                        if (c == quot) {
                            writer.write(c == '"' ? "&quot;" : "&apos;");
                            break;
                        }
                        // BEGIN android-changed: refuse to output invalid characters
                        // See http://www.w3.org/TR/REC-xml/#charsets for definition.
                        // Corrected for c:geo to handle utf-16 codepoint surrogates correctly
                        // See http://en.wikipedia.org/wiki/UTF-16#Code_points_U.2B10000_to_U.2B10FFFF
                        // Note: tab, newline, and carriage return have already been
                        // handled above.
                        // Check for lead surrogate
                        if (c >= 0xd800 && c <= 0xdbff) {

                            if (i + 1 < s.length()) {
                                writer.write(s.substring(i, i + 1));
                                i++;
                                break;
                            }
                            // if the lead surrogate is at the string end, it's not valid utf-16
                            reportInvalidCharacter(c);
                        }
                        boolean valid = (c >= 0x20 && c <= 0xd7ff) || (c >= 0xe000 && c <= 0xfffd);
                        if (!valid) {
                            reportInvalidCharacter(c);
                        }
                        if (unicode || c < 127) {
                            writer.write(c);
                        } else {
                            writer.write("&#" + ((int) c) + ";");
                        }
                        // END android-changed
                }
            }
        }

        // BEGIN android-added
        private static void reportInvalidCharacter(char ch) {
            throw new IllegalArgumentException("Illegal character (" + Integer.toHexString((int) ch) + ")");
        }
        // END android-added

    /*
     * private final void writeIndent() throws IOException {
     * writer.write("\r\n");
     * for (int i = 0; i < depth; i++)
     * writer.write(' ');
     * }
     */

        public void docdecl(String dd) throws IOException {
            writer.write("<!DOCTYPE");
            writer.write(dd);
            writer.write(">");
        }

        public void endDocument() throws IOException {
            while (depth > 0) {
                endTag(elementStack[depth * 3 - 3], elementStack[depth * 3 - 1]);
            }
            flush();
        }

        public void entityRef(String name) throws IOException {
            check(false);
            writer.write('&');
            writer.write(name);
            writer.write(';');
        }

        public boolean getFeature(String name) {
            //return false;
            return ("http://xmlpull.org/v1/doc/features.html#indent-output"
                    .equals(
                            name))
                    ? indent[depth]
                    : false;
        }

        public String getPrefix(String namespace, boolean create) {
            try {
                return getPrefix(namespace, false, create);
            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }

        private final String getPrefix(
                String namespace,
                boolean includeDefault,
                boolean create)
                throws IOException {

            for (int i = nspCounts[depth + 1] * 2 - 2; i >= 0; i -= 2) {
                if (nspStack[i + 1].equals(namespace)
                        && (includeDefault || !StringUtils.isEmpty(nspStack[i]))) {
                    String cand = nspStack[i];
                    for (int j = i + 2; j < nspCounts[depth + 1] * 2; j++) {
                        if (nspStack[j].equals(cand)) {
                            cand = null;
                            break;
                        }
                    }
                    if (cand != null) {
                        return cand;
                    }
                }
            }

            if (!create) {
                return null;
            }

            String prefix;

            if (StringUtils.isEmpty(namespace)) {
                prefix = "";
            } else {
                do {
                    prefix = "n" + (auto++);
                    for (int i = nspCounts[depth + 1] * 2 - 2; i >= 0; i -= 2) {
                        if (prefix.equals(nspStack[i])) {
                            prefix = null;
                            break;
                        }
                    }
                } while (prefix == null);
            }

            boolean p = pending;
            pending = false;
            setPrefix(prefix, namespace);
            pending = p;
            return prefix;
        }

        public Object getProperty(String name) {
            throw new RuntimeException("Unsupported property");
        }

        public void ignorableWhitespace(String s)
                throws IOException {
            text(s);
        }

        public void setFeature(String name, boolean value) {
            if ("http://xmlpull.org/v1/doc/features.html#indent-output"
                    .equals(name)) {
                indent[depth] = value;
            } else {
                throw new RuntimeException("Unsupported Feature");
            }
        }

        public void setProperty(String name, Object value) {
            throw new RuntimeException(
                    "Unsupported Property:" + value);
        }

        public void setPrefix(String prefix, String namespace)
                throws IOException {

            check(false);
            if (prefix == null) {
                prefix = "";
            }
            if (namespace == null) {
                namespace = "";
            }

            String defined = getPrefix(namespace, true, false);

            // boil out if already defined

            if (prefix.equals(defined)) {
                return;
            }

            int pos = (nspCounts[depth + 1]++) << 1;

            if (nspStack.length < pos + 1) {
                String[] hlp = new String[nspStack.length + 16];
                System.arraycopy(nspStack, 0, hlp, 0, pos);
                nspStack = hlp;
            }

            nspStack[pos++] = prefix;
            nspStack[pos] = namespace;
        }

        public void setOutput(Writer writer) {
            // BEGIN android-changed
            // Guarantee that the writer is always buffered.
            if (writer instanceof BufferedWriter) {
                this.writer = (BufferedWriter) writer;
            } else {
                this.writer = new BufferedWriter(writer, WRITE_BUFFER_SIZE);
            }
            // END android-changed

            // elementStack = new String[12]; //nsp/prefix/name
            //nspCounts = new int[4];
            //nspStack = new String[8]; //prefix/nsp
            //indent = new boolean[4];

            nspCounts[0] = 2;
            nspCounts[1] = 2;
            nspStack[0] = "";
            nspStack[1] = "";
            nspStack[2] = "xml";
            nspStack[3] = "http://www.w3.org/XML/1998/namespace";
            pending = false;
            auto = 0;
            depth = 0;

            unicode = false;
        }

        public void setOutput(OutputStream os, String encoding)
                throws IOException {
            if (os == null) {
                throw new IllegalArgumentException("os == null");
            }
            setOutput(encoding == null
                    ? new OutputStreamWriter(os)
                    : new OutputStreamWriter(os, encoding));
            this.encoding = encoding;
            if (encoding != null && encoding.toLowerCase(Locale.US).startsWith("utf")) {
                unicode = true;
            }
        }

        public void startDocument(String encoding, Boolean standalone) throws IOException {
            writer.write("<?xml version='1.0' ");

            if (encoding != null) {
                this.encoding = encoding;
                if (encoding.toLowerCase(Locale.US).startsWith("utf")) {
                    unicode = true;
                }
            }

            if (this.encoding != null) {
                writer.write("encoding='");
                writer.write(this.encoding);
                writer.write("' ");
            }

            if (standalone != null) {
                writer.write("standalone='");
                writer.write(
                        standalone.booleanValue() ? "yes" : "no");
                writer.write("' ");
            }
            writer.write("?>");
        }

        public XmlSerializer startTag(String namespace, String name)
                throws IOException {
            check(false);

            //        if (namespace == null)
            //            namespace = "";

            if (indent[depth]) {
                writer.write("\r\n");
                for (int i = 0; i < depth; i++) {
                    writer.write("  ");
                }
            }

            int esp = depth * 3;

            if (elementStack.length < esp + 3) {
                String[] hlp = new String[elementStack.length + 12];
                System.arraycopy(elementStack, 0, hlp, 0, esp);
                elementStack = hlp;
            }

            String prefix =
                    namespace == null
                            ? ""
                            : getPrefix(namespace, true, true);

            if (namespace != null && StringUtils.isEmpty(namespace)) {
                for (int i = nspCounts[depth]; i < nspCounts[depth + 1]; i++) {
                    if (StringUtils.isEmpty(nspStack[i * 2]) && !StringUtils.isEmpty(nspStack[i * 2 + 1])) {
                        throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
                    }
                }
            }

            elementStack[esp++] = namespace;
            elementStack[esp++] = prefix;
            elementStack[esp] = name;

            writer.write('<');
            if (!StringUtils.isEmpty(prefix)) {
                writer.write(prefix);
                writer.write(':');
            }

            writer.write(name);

            pending = true;

            return this;
        }

        public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
            if (!pending) {
                throw new IllegalStateException("illegal position for attribute");
            }

            //        int cnt = nspCounts[depth];

            if (namespace == null) {
                namespace = "";
            }

            //        depth--;
            //        pending = false;

            String prefix =
                    StringUtils.isEmpty(namespace)
                            ? ""
                            : getPrefix(namespace, false, true);

            //        pending = true;
            //        depth++;

        /*
         * if (cnt != nspCounts[depth]) {
         * writer.write(' ');
         * writer.write("xmlns");
         * if (nspStack[cnt * 2] != null) {
         * writer.write(':');
         * writer.write(nspStack[cnt * 2]);
         * }
         * writer.write("=\"");
         * writeEscaped(nspStack[cnt * 2 + 1], '"');
         * writer.write('"');
         * }
         */

            writer.write(' ');
            if (!StringUtils.isEmpty(prefix)) {
                writer.write(prefix);
                writer.write(':');
            }
            writer.write(name);
            writer.write('=');
            char q = value.indexOf('"') == -1 ? '"' : '\'';
            writer.write(q);
            writeEscaped(value, q);
            writer.write(q);

            return this;
        }

        public void flush() throws IOException {
            check(false);
            writer.flush();
        }

        /*
         * public void close() throws IOException {
         * check();
         * writer.close();
         * }
         */
        public XmlSerializer endTag(String namespace, String name)
                throws IOException {

            if (!pending) {
                depth--;
                //        if (namespace == null)
                //          namespace = "";
            }

            if ((namespace == null
                    && elementStack[depth * 3] != null)
                    || (namespace != null
                    && !namespace.equals(elementStack[depth * 3]))
                    || !elementStack[depth * 3 + 2].equals(name)) {
                throw new IllegalArgumentException("</{" + namespace + "}" + name + "> does not match start");
            }

            if (pending) {
                check(true);
                depth--;
            } else {
                if (indent[depth + 1]) {
                    writer.write("\r\n");
                    for (int i = 0; i < depth; i++) {
                        writer.write("  ");
                    }
                }

                writer.write("</");
                String prefix = elementStack[depth * 3 + 1];
                if (!StringUtils.isEmpty(prefix)) {
                    writer.write(prefix);
                    writer.write(':');
                }
                writer.write(name);
                writer.write('>');
            }

            nspCounts[depth + 1] = nspCounts[depth];
            return this;
        }

        public String getNamespace() {
            return getDepth() == 0 ? null : elementStack[getDepth() * 3 - 3];
        }

        public String getName() {
            return getDepth() == 0 ? null : elementStack[getDepth() * 3 - 1];
        }

        public int getDepth() {
            return pending ? depth + 1 : depth;
        }

        public XmlSerializer text(String text) throws IOException {
            check(false);
            indent[depth] = false;
            writeEscaped(text, -1);
            return this;
        }

        public XmlSerializer text(char[] text, int start, int len)
                throws IOException {
            text(new String(text, start, len));
            return this;
        }

        public void cdsect(String data) throws IOException {
            check(false);
            // BEGIN android-changed: ]]> is not allowed within a CDATA,
            // so break and start a new one when necessary.
            data = data.replace("]]>", "]]]]><![CDATA[>");
            char[] chars = data.toCharArray();
            // We also aren't allowed any invalid characters.
            for (char ch : chars) {
                boolean valid = (ch >= 0x20 && ch <= 0xd7ff) ||
                        (ch == '\t' || ch == '\n' || ch == '\r') ||
                        (ch >= 0xe000 && ch <= 0xfffd);
                if (!valid) {
                    reportInvalidCharacter(ch);
                }
            }
            writer.write("<![CDATA[");
            writer.write(chars, 0, chars.length);
            writer.write("]]>");
            // END android-changed
        }

        public void comment(String comment) throws IOException {
            check(false);
            writer.write("<!--");
            writer.write(comment);
            writer.write("-->");
        }

        public void processingInstruction(String pi)
                throws IOException {
            check(false);
            writer.write("<?");
            writer.write(pi);
            writer.write("?>");
        }
    }
}