package com.android.xposed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xposed.R;
import com.android.xposed.hook.XposedHook;
import com.android.xposed.utils.StringUtil;
import com.android.xposed.utils.SystemUtil;
import com.sky.xposed.common.helper.ReceiverHelper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 描述：MainActivity
 *
 * @author fzJiang
 * @date 2020-12-03 9:17.
 */
public class MainActivity extends AppCompatActivity implements ReceiverHelper.ReceiverCallback {

    private static final String TAG = "MainActivity";

    private EditText mEditUrl;
    private EditText mEditMap;

    private TextView mTextStatus;
    private TextView mTextResult;

    private IBinder mBinder;
    private boolean isConnecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditUrl = findViewById(R.id.edit_url);
        mEditMap = findViewById(R.id.edit_map);

        mTextStatus = findViewById(R.id.tv_status);
        mTextResult = findViewById(R.id.text);

        mEditUrl.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditUrl.setSingleLine(false);

        mEditMap.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditMap.setSingleLine(false);

        Button buttonInit = findViewById(R.id.button_init);
        buttonInit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initClient();
            }
        });

        Button buttonTest = findViewById(R.id.button_test);
        buttonTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendData(null, null, true);
            }
        });

        Button buttonAction = findViewById(R.id.button_action);
        buttonAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditUrl.getText()) || TextUtils.isEmpty(mEditMap.getText())) {
                    Toast.makeText(MainActivity.this, "发送数据不可为空！", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Map<String, List<String>> map = StringUtil.mapStringToMap(mEditMap.getText().toString());
                    sendData(mEditUrl.getText().toString(), map, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "map集合数据转换错误！", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button buttonClear = findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEditUrl.setText("");
                mEditMap.setText("");
                mTextResult.setText("");
            }
        });

        Button buttonCopy = findViewById(R.id.button_copy);
        buttonCopy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SystemUtil.clipboard(MainActivity.this, mTextResult.getText().toString());
                Toast.makeText(MainActivity.this, "复制成功！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onReceive(String action, Intent intent) {
        if (!com.sky.xposed.common.Constant.Action.REFRESH_PREFERENCE.equals(action)) {
            return;
        }

        String message;
        if (intent == null || (message = intent.getStringExtra(com.sky.xposed.common.Constant.Key.DATA)) == null) {
            return;
        }

        if (TextUtils.isEmpty(mTextResult.getText())) {
            mTextResult.setText(message);
        } else {
            mTextResult.setText(MessageFormat.format("{0}\n{1}", mTextResult.getText(), message));
        }
    }

    private void initClient() {
        if (mBinder != null && mBinder.isBinderAlive()) {
            return;
        }

        try {
            isConnecting = true;
            mBinder = ServiceManager.getService(XposedHook.XposedBinder.SERVICE_NAME);

            if (mBinder != null && mBinder.isBinderAlive()) {
                isConnecting = false;
                mTextStatus.setText("已连接");
                Toast.makeText(MainActivity.this, "服务连接成功", Toast.LENGTH_LONG).show();
            } else {
                mTextStatus.setText("未连接");
                Toast.makeText(MainActivity.this, "服务连接失败，请稍后重试...", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mTextStatus.setText("未连接");
            Toast.makeText(MainActivity.this, "服务连接失败，请稍后重试...", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("unchecked")
    private void sendData(String url, Map<String, List<String>> map, boolean test) {
        if (mBinder == null || !mBinder.isBinderAlive()) {
            Toast.makeText(MainActivity.this, "服务未连接，请稍后重试...", Toast.LENGTH_LONG).show();
            return;
        }

        if (isConnecting) {
            Toast.makeText(MainActivity.this, "服务连接中，请稍后重试...", Toast.LENGTH_LONG).show();
            return;
        }

        Parcel reply = Parcel.obtain();
        Parcel data = Parcel.obtain();
        try {
            boolean success;
            if (test) {
                success = mBinder.transact(XposedHook.XposedBinder.T_DATA_TEST, data, reply, 0);
            } else {
                data.writeString(url);
                data.writeMap(map);
                success = mBinder.transact(XposedHook.XposedBinder.T_DATA, data, reply, 0);
            }

            if (success) {
                reply.readException();
                HashMap<String, String> resultMap = reply.readHashMap(ClassLoader.getSystemClassLoader());

                StringBuilder stringBuilder = null;
                if (resultMap != null) {
                    stringBuilder = new StringBuilder();
                    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                        Log.i(TAG, entry.getKey() + ":" + entry.getValue());
                        stringBuilder
                                .append(entry.getKey())
                                .append(":")
                                .append(entry.getValue())
                                .append("\n");
                    }
                }
                mTextResult.setText(stringBuilder == null ? "未读取到数据" : stringBuilder.toString());
            } else {
                Toast.makeText(MainActivity.this, "数据发送失败", Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "数据发送失败，" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "数据发送失败，" + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}