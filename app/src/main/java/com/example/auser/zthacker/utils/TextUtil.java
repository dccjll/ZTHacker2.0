package com.example.auser.zthacker.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TextUtil {
    /**
     * 判断字符串是否为空
     *
     * @param text
     * @return
     */
    public static boolean isNull(String text) {
        if (text == null)
            return true;
        else if (text.trim().equals(""))
            return true;
        else if (text.trim().equals("null"))
            return true;
        else
            return false;
    }


    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(string.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String desensitize(String content, int start, int length) {
        if (TextUtils.isEmpty(content) || start + length >= content.length()) {
            return content;
        }
        StringBuilder sb = new StringBuilder(content.substring(0, start));
        for (int i = 0; i < length; i++) {
            sb.append(" *");
        }
        sb.append(content.substring(start + length, content.length()));
        return sb.toString();
    }
}
