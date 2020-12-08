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
}