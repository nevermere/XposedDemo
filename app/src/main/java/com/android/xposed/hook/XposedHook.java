package com.android.xposed.hook;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.sky.xposed.javax.MethodHook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 描述：XposedHookService
 *
 * @author fzJiang
 * @date 2020-12-03 9:14.
 */
@SuppressWarnings("rawtypes")
public class XposedHook implements IXposedHookLoadPackage, MethodHook.ThrowableCallback {

    private static final String TAG = "XposedHookService";
    private static final String TAG_CALL = "CallMethod";

    private static final String PACKAGE_NAME = "com.ss.android.ugc.aweme";

    private static String mUrl;
    private static Map<String, List<String>> mMap;

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
                                mUrl = (String) methodHookParam.args[0];
                                mMap = (Map<String, List<String>>) methodHookParam.args[1];
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

    public static class XposedBinder extends Binder {

        public static final String SERVICE_NAME = "xposed_binder_service";
        public static final int T_DATA_TEST = 0x001001;
        public static final int T_DATA = 0x001002;

        @SuppressWarnings("unchecked")
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            if (T_DATA_TEST == code) {
                try {
                    if (reply != null) {
                        reply.writeNoException();
                        reply.writeMap(callMethod(mUrl, mMap, true));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (T_DATA == code) {
                try {
                    String url = data.readString();
                    Map<String, List<String>> map = data.readHashMap(ClassLoader.getSystemClassLoader());
                    if (reply != null) {
                        reply.writeNoException();
                        reply.writeMap(callMethod(url, map, false));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    /**
     * com.bytedance.frameworks.baselib.network.http.e.a(String,Map<String,String>)
     *
     * @param url  url
     * @param map  map
     * @param test test
     * @return
     */
    @SuppressWarnings("unchecked")
    private static HashMap<String, String> callMethod(String url, Map<String, List<String>> map, boolean test) {
        Log.i(TAG_CALL, "----------------------------------");
        Log.i(TAG_CALL, "" + url);
        Log.i(TAG_CALL, "" + map);

        try {
            Class clazz = XposedUtil.findClass("com.bytedance.frameworks.baselib.network.http.e");
            HashMap<String, String> resultMap = (HashMap<String, String>) XposedUtil.callStaticMethod(clazz, "a", url, map);

            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                Log.i(TAG_CALL, entry.getKey() + ":" + entry.getValue());
            }

            if (test) {
                resultMap.put("url", url);
                resultMap.put("map", String.valueOf(map));
            }

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG_CALL, "-----------------执行失败-----------------" + e.getMessage());
            return null;
        }
    }

//    private static String callMethod(String args) {
//        // l.a(com.ss.sys.ces.a.leviathan(i, l.a(paramAnonymousString.toString())))
//        try {
//            Log.i(TAG, "-----------------args-----------------" + args);
//
//            Class clazzA = XposedUtil.findClass("com.ss.android.common.applog.l");
//            Class clazzL = XposedUtil.findClass("com.ss.sys.ces.a");
//
//            // com.ss.android.common.applog.l.a(String) 十六进制字符串
//            byte[] resultA = (byte[]) XposedUtil.callStaticMethod(clazzA, "a", args);
//            Log.i(TAG, "-----------------resultA-----------------" + Arrays.toString(resultA));
//
//            // com.ss.sys.ces.a.leviathan(int,byte[])
//            int index = (int) (System.currentTimeMillis() / 1000L);
//            byte[] resultL = (byte[]) XposedUtil.callStaticMethod(clazzL, "leviathan", index, resultA);
//            Log.i(TAG, "-----------------resultL-----------------" + Arrays.toString(resultL) + ",index:" + index);
//
//            // com.ss.android.common.applog.l.a(byte[])
//            String resultString = (String) XposedUtil.callStaticMethod(clazzA, "a", resultL);
//            Log.i(TAG, "-----------------resultString-----------------" + resultString);
//
//            return resultString;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i(TAG, "-----------------执行失败-----------------" + e.getMessage());
//            return null;
//        }
//    }
}