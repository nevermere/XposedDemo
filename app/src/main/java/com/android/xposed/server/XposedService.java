package com.android.xposed.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.xposed.utils.NetUtil;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

/**
 * 描述：XposedService
 *
 * @author fzJiang
 * @date 2020-12-08 16:16.
 */
public class XposedService extends Service {

    private Server mServer;

    @Override
    public void onCreate() {
        mServer = AndServer.webServer(this)
                .port(8080)
                .timeout(20, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {

                    @Override
                    public void onStarted() {
                        InetAddress address = NetUtil.getLocalIPAddress();
                        if (address != null) {
                            ServerManager.onServerStart(XposedService.this, address.getHostAddress());
                        } else {
                            ServerManager.onServerError(XposedService.this, "未获取到本机IP地址");
                        }
                    }

                    @Override
                    public void onStopped() {
                        ServerManager.onServerStop(XposedService.this);
                    }

                    @Override
                    public void onException(Exception e) {
                        ServerManager.onServerError(XposedService.this, e.getMessage());
                    }
                }).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    private void startServer() {
        mServer.startup();
    }

    private void stopServer() {
        mServer.shutdown();
    }
}
