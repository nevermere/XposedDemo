package com.android.xposed.hook;

import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;

import com.sky.xposed.javax.MethodHook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 描述：XposedHookService
 *
 * @author fzJiang
 * @date 2020-12-03 9:14.
 */
public class XposedHook implements IXposedHookLoadPackage, MethodHook.ThrowableCallback {

    private static final String TAG = "XposedHook";
    private static final String PACKAGE_NAME = "com.ss.android.ugc.aweme";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) {
        if (PACKAGE_NAME.equals(param.packageName)) {
            hookApplication(param);
        }
    }

    @Override
    public void onThrowable(Throwable throwable) {
        Log.i(TAG, "onThrowable:" + throwable.getMessage());
    }

    @SuppressWarnings("unchecked")
    private void hookApplication(final XC_LoadPackage.LoadPackageParam param) {
        // 设置默认的参数
        XposedUtil.init(param, this);

        // 初始化系统级Binder服务
        initXposedBinder();

        XposedUtil
                .findMethod("com.bytedance.frameworks.baselib.network.http.e", "a", String.class, Map.class)
                .hook(new MethodHook.AfterCallback() {

                    @Override
                    public void onAfter(XC_MethodHook.MethodHookParam methodHookParam) {

                        // 方法名称
                        Log.i(TAG, "------------------------------------------------------------------------");
                        Log.i(TAG, "method:" + methodHookParam.method);

                        // 参数
                        if (methodHookParam.args != null) {
                            if (methodHookParam.args.length > 1) {
                                XposedBinder.updateTestParams(
                                        (String) methodHookParam.args[0],
                                        (Map<String, List<String>>) methodHookParam.args[1]
                                );
                            }

                            for (Object arg : methodHookParam.args) {
                                Log.i(TAG, "" + arg);
                            }
                        }

                        // 结果
                        HashMap<String, String> resultMap = (HashMap<String, String>) methodHookParam.getResult();
                        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                            Log.i(TAG, entry.getKey() + ":" + entry.getValue());
                        }
                    }
                });
    }

    private void initXposedBinder() {
        if (ServiceManager.getService(XposedBinder.SERVICE_NAME) != null) {
            return;
        }

        try {
            XposedBinder binder = new XposedBinder();
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method method = serviceManager.getMethod("addService", String.class, IBinder.class);

            method.invoke(null, XposedBinder.SERVICE_NAME, binder);
            Log.i(TAG, "initXposedBinder success");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "initXposedBinder fail");
        }
    }
}