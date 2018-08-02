/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zsyj.sharesdk.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StringUtils {
    public static String join(Object[] elements, CharSequence separator) {
        return join(Arrays.asList(elements), separator);
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n|&#160;");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String join(Iterable<? extends Object> elements, CharSequence separator) {
        StringBuilder builder = new StringBuilder();

        if (elements != null) {
            Iterator<? extends Object> iter = elements.iterator();
            if (iter.hasNext()) {
                builder.append(String.valueOf(iter.next()));
                while (iter.hasNext()) {
                    builder.append(separator).append(String.valueOf(iter.next()));
                }
            }
        }

        return builder.toString();
    }

    public static String fixLastSlash(String str) {
        String res = str == null ? "/" : str.trim() + "/";
        if (res.length() > 2 && res.charAt(res.length() - 2) == '/')
            res = res.substring(0, res.length() - 1);
        return res;
    }

    public static int convertToInt(String str) throws NumberFormatException {
        int s, e;
        for (s = 0; s < str.length(); s++)
            if (Character.isDigit(str.charAt(s)))
                break;
        for (e = str.length(); e > 0; e--)
            if (Character.isDigit(str.charAt(e - 1)))
                break;
        if (e > s) {
            try {
                return Integer.parseInt(str.substring(s, e));
            } catch (NumberFormatException ex) {
                Log.e("convertToInt", String.valueOf(ex));
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * @return 字符串是否为空
     */
    public static Boolean isNullStr(String str) {
        try {
            Boolean result = false;

            if (str == null || str.length() <= 0 || str.equals(null)
                    || str.equals("null")) {
                result = true;
            }

            return result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return 字符串是否不为空
     */
    public static Boolean isNotNullStr(String str) {
        try {
            Boolean result = true;

            if (str == null || str.length() <= 0 || str.equals(null)
                    || str.equals("null")) {
                result = false;
            }

            return result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断一个字符串中是否包含指定字符串
     *
     * @param str         一个字符串
     * @param searchChars 指定字符串
     */
    public static boolean isHaveStr(String str, String searchChars) {
        try {
            if (isNotNullStr(str) && isNotNullStr(searchChars)) {
                return str.contains(searchChars);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断两个字符串是否相同
     *
     * @param str1 字符串1
     * @param str2 字符串2
     */
    public static boolean isSameStr(String str1, String str2) {
        try {
            if (isNotNullStr(str1) && isNotNullStr(str2)) {
                return str1.equals(str2);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
            /*
            移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
            联通：130、131、132、152、155、156、185、186
            电信：133、153、180、189、（1349卫通）
            总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9（第二位为7是虚拟运营商）
            */
        String num = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 将手机号码第4位到第7位替换成*号
     *
     * @return 隐藏后的手机号字符串
     */
    public static String phoneNuberHide(String str) {
        try {
            // 括号表示组，被替换的部分$n表示第n组的内容
            str = str.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            return str;
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 判断输入的字符串是否含有数字
     *
     * @return 是否含有数字
     */
    public static boolean HasDigit(String content) {
        boolean flag = false;
        try {

            Pattern p = Pattern.compile(".*\\d+.*");
            Matcher m = p.matcher(content);
            if (m.matches()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        try {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 完整的判断中文汉字和符号
    public static boolean myIsChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * AES加密
     */
    public static String AES_Encrypt(String keyStr, String plainText) {
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeToString(cipher.doFinal(plainText.getBytes()),
                    Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * AES解密
     */
    public static String AES_Decrypt(String keyStr, String encryptData) {
        try {
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encrypted1 = Base64.decode(encryptData, Base64.DEFAULT);
            return new String(cipher.doFinal(encrypted1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Key generateKey(String key) throws Exception {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            return keySpec;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 对外提供getMD5(String)方法
     *
     * @return 加密失败返回""
     */
    public static String getMD5(String val) throws NoSuchAlgorithmException {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    val.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        try {
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }

            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 生成32位编码
     *
     * @return string
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap   要排序的Map对象
     * @param urlEncode 是否需要URLENCODE
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtils.isNotNullStr(item.getKey())&&StringUtils.isNotNullStr(item.getValue())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buf.append(key + "=" + val);
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param string 指定字符串
     * @return 不为 null 且 长度大于 0 返回 true, 否则返回 false
     */
    public static boolean notEmpty(CharSequence string) {
        return string != null && string.length() > 0;
    }
}
