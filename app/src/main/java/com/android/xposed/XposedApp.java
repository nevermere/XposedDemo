package com.android.xposed;

import android.app.Application;

import com.android.xposed.net.OkHttpUtil;
import com.hjq.toast.ToastUtils;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2020-12-08 16:48.
 */
public class XposedApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpUtil.initClient();
        ToastUtils.init(this);
    }
}
