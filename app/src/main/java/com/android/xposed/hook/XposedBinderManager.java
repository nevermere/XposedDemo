package com.android.xposed.hook;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.hjq.toast.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：XposedBinderManager
 *
 * @author fzJiang
 * @date 2020-12-09 9:07.
 */
public class XposedBinderManager {

    private static final String TAG = "XposedBinderManager";
    private IBinder mBinder;
    private boolean isConnecting;

    public static XposedBinderManager getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final XposedBinderManager INSTANCE = new XposedBinderManager();
    }

    public boolean startConnect() {
        if (mBinder != null && mBinder.isBinderAlive()) {
            return true;
        }

        try {
            isConnecting = true;
            mBinder = ServiceManager.getService(XposedBinder.SERVICE_NAME);

            if (mBinder != null && mBinder.isBinderAlive()) {
                isConnecting = false;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "连接失败," + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> sendData(String url, Map<String, List<String>> map, boolean test) {

        if (mBinder == null || !mBinder.isBinderAlive()) {
            ToastUtils.show("服务未连接，请稍后重试...");
            return null;
        }

        if (isConnecting) {
            ToastUtils.show("服务连接中，请稍后重试...");
            return null;
        }


        Parcel reply = Parcel.obtain();
        Parcel data = Parcel.obtain();
        HashMap<String, String> resultMap = null;
        try {
            boolean success;
            if (test) {
                success = mBinder.transact(XposedBinder.T_DATA_TEST, data, reply, 0);
            } else {
                data.writeString(url);
                data.writeMap(map);
                success = mBinder.transact(XposedBinder.T_DATA, data, reply, 0);
            }

            if (success) {
                reply.readException();
                resultMap = reply.readHashMap(ClassLoader.getSystemClassLoader());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "数据发送失败," + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "数据发送失败," + e.getMessage());
        } finally {
            data.recycle();
            reply.recycle();
        }
        return resultMap;
    }

    public interface Callback {

        void onSuccess();

        void onFailed(String error);

    }
}