package com.android.xposed.hook;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 描述：XposedBinder
 *
 * @author fzJiang
 * @date 2020-12-09 9:40.
 */
public class XposedBinder extends Binder {

    private static final String TAG = "XposedBinder";
    public static final String SERVICE_NAME = "xposed_binder_service";
    public static final int T_DATA_TEST = 0x001001;
    public static final int T_DATA = 0x001002;

    private static String mUrl;
    private static Map<String, List<String>> mMap;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        if (T_DATA_TEST == code) {
            try {
                if (reply != null
                        && mUrl != null && mMap != null) {
                    reply.writeNoException();
                    reply.writeMap(callMethod(mUrl, mMap));
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
                    reply.writeMap(callMethod(url, map));
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    /**
     * com.bytedance.frameworks.baselib.network.http.e.a(String,Map<String,String>)
     *
     * @param url url
     * @param map map
     * @return HashMap
     */
    @SuppressWarnings("unchecked")
    private static HashMap<String, String> callMethod(String url, Map<String, List<String>> map) {

        Log.i(TAG, "-----------------callMethod-----------------");
        Log.i(TAG, "" + url);
        Log.i(TAG, "" + map);

        try {
            Class<?> clazz = XposedUtil.findClass("com.bytedance.frameworks.baselib.network.http.e");
            HashMap<String, String> resultMap = (HashMap<String, String>) XposedUtil.callStaticMethod(clazz, "a", url, map);

            resultMap.put("url", url);
            resultMap.put("map", String.valueOf(map));

            Log.i(TAG, resultMap.toString());

            return resultMap;
        } catch (Exception e) {
            Log.e(TAG, "-----------------执行失败-----------------" + e.getMessage());
            return null;
        }
    }

    public static void updateTestParams(String url, Map<String, List<String>> map) {
        XposedBinder.mUrl = url;
        XposedBinder.mMap = map;
    }
}
