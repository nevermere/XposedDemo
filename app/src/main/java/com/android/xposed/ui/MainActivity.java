package com.android.xposed.ui;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.xposed.R;
import com.android.xposed.hook.XposedBinderManager;
import com.android.xposed.net.OkHttpUtil;
import com.android.xposed.server.ServerCallback;
import com.android.xposed.server.ServerManager;
import com.android.xposed.ui.entity.AwemeBean;
import com.android.xposed.utils.LogUtil;
import com.android.xposed.utils.StringUtil;
import com.android.xposed.utils.SystemUtil;
import com.hjq.toast.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 描述：MainActivity
 *
 * @author fzJiang
 * @date 2020-12-03 9:17.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edit_url)
    EditText mEditUrl;
    @BindView(R.id.edit_map)
    EditText mEditMap;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.button_init)
    Button mButtonInit;
    @BindView(R.id.button_test)
    Button mButtonTest;
    @BindView(R.id.button_action)
    Button mButtonAction;
    @BindView(R.id.tv_server_status)
    TextView mTvServerStatus;
    @BindView(R.id.button_init_server)
    Button mButtonInitServer;
    @BindView(R.id.button_stop_server)
    Button mButtonStopServer;
    @BindView(R.id.button_test_server)
    Button mButtonTestServer;
    @BindView(R.id.edit_result)
    EditText mEditResult;
    @BindView(R.id.button_clear)
    Button mButtonClear;
    @BindView(R.id.button_copy)
    Button mButtonCopy;

    private ServerManager mServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mEditUrl.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditUrl.setSingleLine(false);

        mEditMap.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditMap.setSingleLine(false);

        mServerManager = new ServerManager(this, new ServerCallback() {

            @Override
            public void onServerStart(String ip) {
                // 服务已开启 http://172.17.100.15:8080/com/android/xposed/core/aweme?"
                mTvServerStatus.setText(MessageFormat.format("已开启,http://{0}:{1}", ip, "8080"));
            }

            @Override
            public void onServerError(String error) {
                // 服务开启失败
                mTvServerStatus.setText(MessageFormat.format("开启失败,{0}", error));
            }

            @Override
            public void onServerStop() {
                // 服务已关闭
                mTvServerStatus.setText("已关闭");
            }
        });
        mServerManager.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServerManager != null) {
            mServerManager.unRegister();
        }
    }

    @OnClick({R.id.button_init, R.id.button_test, R.id.button_action,
            R.id.button_init_server, R.id.button_stop_server, R.id.button_test_server,
            R.id.button_clear, R.id.button_copy, R.id.tv_server_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_init:
                if (XposedBinderManager.getInstance().startConnect()) {
                    ToastUtils.show("服务已连接");
                    mTvStatus.setText("已连接");
                } else {
                    ToastUtils.show("服务连接失败，请稍后重试...");
                }
                break;

            case R.id.button_test: {
                Map<String, String> resultMap = XposedBinderManager.getInstance().sendData(null, null, true);
                StringBuilder stringBuilder = null;
                if (resultMap != null) {
                    stringBuilder = new StringBuilder();
                    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                        stringBuilder
                                .append(entry.getKey())
                                .append(":")
                                .append(entry.getValue())
                                .append("\n");
                    }
                }
                mEditResult.setText(stringBuilder == null ? "未读取到数据" : stringBuilder.toString());
            }
            break;

            case R.id.button_action: {
                if (TextUtils.isEmpty(mEditUrl.getText()) || TextUtils.isEmpty(mEditMap.getText())) {
                    ToastUtils.show("发送数据不可为空！");
                    return;
                }

                try {
                    Map<String, List<String>> map = StringUtil.mapStringToMap(mEditMap.getText().toString());
                    Map<String, String> resultMap = XposedBinderManager.getInstance().sendData(mEditUrl.getText().toString(), map, false);
                    StringBuilder stringBuilder = null;
                    if (resultMap != null) {
                        stringBuilder = new StringBuilder();
                        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                            stringBuilder
                                    .append(entry.getKey())
                                    .append(":")
                                    .append(entry.getValue())
                                    .append("\n");
                        }
                    }
                    mEditResult.setText(stringBuilder == null ? "未读取到数据" : stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show("map集合数据转换错误！");
                }
            }
            break;

            case R.id.button_clear:
                mEditUrl.setText("");
                mEditMap.setText("");
                mEditResult.setText("");
                break;

            case R.id.button_copy:
                SystemUtil.clipboard(getApplicationContext(), mEditResult.getText().toString());
                ToastUtils.show("复制成功！");
                break;

            case R.id.button_init_server:
                if (mServerManager != null) {
                    if (mServerManager.isStarting()) {
                        ToastUtils.show("服务正在启动中！");
                        return;
                    }
                    mServerManager.startServer();
                }
                break;

            case R.id.button_stop_server:
                if (mServerManager != null) {
                    mServerManager.stopServer();
                }
                break;

            case R.id.button_test_server:

                final String url = "https://aweme.snssdk.com/aweme/v1/user/follower/list/?user_id=79971332120&max_time=1607402937&count=20&retry_type=no_retry&mcc_mnc=&iid=2796798078884398&device_id=70067341873&ac=wifi&channel=wandoujia_aweme&aid=1128&app_name=aweme&version_code=421&version_name=4.2.1&device_platform=android&ssmix=a&device_type=G8231&device_brand=Sony&language=zh&os_api=26&os_version=8.0.0&openudid=7d3d2a468275fcad&manifest_version_code=421&resolution=1080*1776&dpi=480&update_version_code=4212&_rticket=1607564158249&ts=1607564152&js_sdk_version=1.9.1";

                final String map = "{accept-encoding=[gzip], x-ss-req-ticket=[1607485100099], cookie=[[odin_tt=7f8ea2d70fbd08f3c09ab4a5e9c2abda13fff304ae5160adc0fc3f8706c4ec1f7f3e0e02e9361cd4f12bda3c877a6d1e;ttreq=2b9ada4ca4d3799246243acc8a3375844a9b7d96;install_id=2796798078884398]], sdk-version=[1]}";

                OkHttpUtils.post()
                        .url("http://10.168.1.205:8080/com/android/xposed/aweme?")
                        .addParams("url", url)
                        .addParams("map", map)
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                LogUtil.i("----onError---" + e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                AwemeBean awemeBean = OkHttpUtil.convert(response, AwemeBean.class);
                                if (awemeBean != null && awemeBean.getCode() == 0) {

                                    String xKhronos = awemeBean.getData().getxKhronos();
                                    String xGorgon = awemeBean.getData().getxGorgon();


                                    OkHttpUtils.get()
                                            .url(url)
                                            .addHeader("Cookie", map)
                                            .addHeader("Accept-Encoding", "gzip")
                                            .addHeader("X-SS-QUERIES", "dGMCDb6ot3awALq2qeved%2FyWk4LlIogVCowOjsiipaluXQSusMIviD4ItG4LFt4dlWnb170KEMwQZ9xqm74rp3zfCYUEpqgVPoxgDjbockgMsQyOBgrMdGdVrEr0opgTXPH1ew%3D%3D")
                                            .addHeader("X-Tt-Token", "008d2393c7964def92049e07192eb669e60007c8b8b821722d15ee80e1ff3f30820fa3c5c0cbd66cba1f7ce19afcd1b30ded8e75079ceb6a20cb4674024b65ea337be7d5f5df7acd8856d557a8a32b2aa9ff1-1.0.0")
                                            .addHeader("sdk-version", "1")
                                            .addHeader("X-SS-RS", "0")
                                            .addHeader("X-Khronos", xKhronos)
                                            .addHeader("X-Gorgon", xGorgon)
                                            .addHeader("X-Pods", "")

                                            .build()
                                            .execute(new StringCallback() {

                                                @Override
                                                public void onError(Call call, Exception e, int id) {
                                                    LogUtil.i("----onError---" + e.getMessage());
                                                }

                                                @Override
                                                public void onResponse(String response, int id) {
                                                    LogUtil.i("----onResponse---" + response);
                                                }
                                            });
                                }
                            }
                        });
                break;

            case R.id.tv_server_status:
                String ip = mTvServerStatus.getText().toString();
                if (!TextUtils.isEmpty(ip) && ip.contains("已开启")) {
                    SystemUtil.clipboard(getApplicationContext(), ip.replace("已开启,", ""));
                    ToastUtils.show("复制成功！");
                }
                break;

            default:
                break;
        }
    }
}