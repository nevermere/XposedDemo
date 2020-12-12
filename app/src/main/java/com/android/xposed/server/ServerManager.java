package com.android.xposed.server;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * 描述：ServerManager
 *
 * @author fzJiang
 * @date 2020-12-08 16:17.
 */
public class ServerManager extends BroadcastReceiver {

    private static final String ACTION = "com.android.xposed.server.receiver";

    private static final String CMD_KEY = "CMD_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";

    private static final int CMD_VALUE_START = 1;
    private static final int CMD_VALUE_ERROR = 2;
    private static final int CMD_VALUE_STOP = 4;

    private Activity mActivity;
    private Intent mIntent;
    private ServerCallback mCallback;
    private boolean isStarting;

    public static void onServerStart(Context context, String hostAddress) {
        sendBroadcast(context, CMD_VALUE_START, hostAddress);
    }

    public static void onServerError(Context context, String error) {
        sendBroadcast(context, CMD_VALUE_ERROR, error);
    }

    public static void onServerStop(Context context) {
        sendBroadcast(context, CMD_VALUE_STOP);
    }

    private static void sendBroadcast(Context context, int cmd) {
        sendBroadcast(context, cmd, null);
    }

    private static void sendBroadcast(Context context, int cmd, String message) {
        Intent broadcast = new Intent(ACTION);
        broadcast.putExtra(CMD_KEY, cmd);
        broadcast.putExtra(MESSAGE_KEY, message);
        context.sendBroadcast(broadcast);
    }

    public ServerManager(Activity activity, ServerCallback callback) {
        this.mActivity = activity;
        this.mCallback = callback;
        mIntent = new Intent(mActivity, XposedService.class);
    }

    public void register() {
        IntentFilter filter = new IntentFilter(ACTION);
        mActivity.registerReceiver(this, filter);
    }

    public boolean isStarting() {
        return isStarting;
    }

    public void unRegister() {
        mActivity.unregisterReceiver(this);
    }

    public void startServer() {
        isStarting = true;
        mActivity.startService(mIntent);
    }

    public void stopServer() {
        mActivity.stopService(mIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            switch (cmd) {
                case CMD_VALUE_START: {
                    isStarting = false;
                    String ip = intent.getStringExtra(MESSAGE_KEY);
                    if (mCallback != null) {
                        mCallback.onServerStart(ip);
                    }
                    break;
                }
                case CMD_VALUE_ERROR: {
                    isStarting = false;
                    String error = intent.getStringExtra(MESSAGE_KEY);
                    if (mCallback != null) {
                        mCallback.onServerError(error);
                    }
                    break;
                }
                case CMD_VALUE_STOP: {
                    isStarting = false;
                    if (mCallback != null) {
                        mCallback.onServerStop();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}