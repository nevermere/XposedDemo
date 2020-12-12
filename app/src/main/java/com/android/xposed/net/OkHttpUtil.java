package com.android.xposed.net;

import android.text.TextUtils;

import com.android.xposed.net.adapter.DoubleDefault0Adapter;
import com.android.xposed.net.adapter.FloatDefault0Adapter;
import com.android.xposed.net.adapter.IntegerDefault0Adapter;
import com.android.xposed.net.adapter.LongDefault0Adapter;
import com.android.xposed.net.adapter.StringDefault0Adapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 描述：OkHttpUtil
 *
 * @author fzJiang
 * @date 2020-07-11 16:13.
 */
public class OkHttpUtil {

    private static Gson gson;
    private static OkHttpClient okHttpClient;

    static {
        // 初始化Gson并增加后台返回""和"null"的处理
        gson = new GsonBuilder()
                .registerTypeAdapter(String.class, new StringDefault0Adapter())
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .registerTypeAdapter(Float.class, new FloatDefault0Adapter())
                .registerTypeAdapter(float.class, new FloatDefault0Adapter())
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 初始化OkHttpClient
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public static void initClient() {
        OkHttpUtils.initClient(okHttpClient);
    }

    public static Gson getGson() {
        return gson;
    }

    public static <T> T convert(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        T result = null;
        try {
            result = gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}