package com.android.xposed.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 描述：SystemUtil
 *
 * @author fzJiang
 * @date 2020-12-07 17:35.
 */
public class SystemUtil {

    /**
     * 复制剪贴板
     *
     * @param context Context
     * @param str     要复制的字符串
     */
    public static boolean clipboard(Context context, String str) {
        try {
            // 获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", str);
            // 将ClipData内容放到系统剪贴板里
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
