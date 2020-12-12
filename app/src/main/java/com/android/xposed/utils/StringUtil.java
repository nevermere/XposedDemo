package com.android.xposed.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：StringUtil
 *
 * @author fzJiang
 * @date 2020-12-08 10:06.
 */
public class StringUtil {

    /**
     * map字符串转map对象
     *
     * @param mapStr map字符串
     * @return Map
     */
    public static Map<String, List<String>> mapStringToMap(String mapStr) {
        if (TextUtils.isEmpty(mapStr)) {
            return null;
        }

        StringBuilder sb = new StringBuilder(mapStr);
        sb.delete(0, 1).delete(sb.length() - 1, sb.length());


        String str = sb.toString();
        List<String> strList = Arrays.asList(str.split(","));

        Map<String, List<String>> map = new HashMap<>(strList.size());
        List<String> valueList;
        // 循环数组
        for (String string : strList) {
            int index = string.indexOf("=");

            String key = string.substring(0, index);

            String value = string.substring(index + 2, string.length() - 1);

            valueList = new ArrayList<>();
            valueList.add(value);

            map.put(key, valueList);
        }
        return map;
    }

    /**
     * map转换为string
     *
     * @param map
     * @return
     */
    public static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            stringBuilder
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * string转为map
     *
     * @param str
     * @return
     */
    public static Map<String, Object> stringToMap(String str) {
        //根据逗号截取字符串数组
        String[] str1 = str.split(",");
        //创建Map对象
        Map<String, Object> map = new HashMap<>(str1.length);
        //循环加入map集合
        for (String s : str1) {
            //根据":"截取字符串数组
            String[] str2 = s.split(":");
            //str2[0]为KEY,str2[1]为值
            map.put(str2[0], str2[1]);
        }
        return map;
    }
}