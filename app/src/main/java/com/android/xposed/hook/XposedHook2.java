package com.android.xposed.hook;

import android.content.Intent;
import android.util.Log;

import com.sky.xposed.javax.MethodHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 描述：XposedHookService
 *
 * @author fzJiang
 * @date 2020-12-03 9:14.
 */
public class XposedHook2 implements IXposedHookLoadPackage, MethodHook.ThrowableCallback {

    private static final String TAG = "XposedHook";
    private static final String PACKAGE_NAME = "com.mpjx.mall";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) {
        if (PACKAGE_NAME.equals(param.packageName)) {

        }

        hookApplication(param);
    }

    @Override
    public void onThrowable(Throwable throwable) {
        Log.i(TAG, "onThrowable:" + throwable.getMessage());
    }

    private void hookApplication(final XC_LoadPackage.LoadPackageParam param) {
        Log.i(TAG, "---------------------------hook start------------------------------------");
        // 设置默认的参数
        XposedUtil.init(param, this);
//        PackageManager packageManager;
        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "getInstalledPackages", int.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.getInstalledPackages----" + methodHookParam.method + "-----" + param.packageName);
                });

        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "queryIntentActivities", Intent.class, int.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.queryIntentActivities----" + methodHookParam.method + "-----" + param.packageName);
                });

        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "getInstalledApplications", int.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.getInstalledApplications----" + methodHookParam.method + "-----" + param.packageName);
                });

        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "getPackagesForUid", int.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.getPackagesForUid----" + methodHookParam.method + "-----" + param.packageName);
                });

        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "getLaunchIntentForPackage", String.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.getLaunchIntentForPackage----" + methodHookParam.method + "-----" + param.packageName);
                });

        XposedUtil
                .findMethod("android.app.ApplicationPackageManager", "getPackageInfo", String.class, int.class)
                .hook((MethodHook.AfterCallback) methodHookParam -> {
                    Log.i(TAG, "----ApplicationPackageManager.getPackageInfo----" + methodHookParam.method + "-----" + param.packageName);
                });
    }
}