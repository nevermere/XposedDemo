package com.android.xposed.server;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2020-12-08 16:19.
 */
public interface ServerCallback {

    void onServerStart(String ip);

    void onServerError(String error);

    void onServerStop();
}
