package com.alfredteng.casetrace.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BaseHttpCallback implements Callback {

    private static final String TAG = "BaseHttpCallback";
    private Handler handler;
    private BaseHttpResultListener baseHttpResultListener;
    private Context context;

    public BaseHttpCallback(BaseHttpResultListener baseHttpResultListener,Context context) {
        this.baseHttpResultListener = baseHttpResultListener;
        this.handler = new Handler(Looper.getMainLooper());
        this.context = context;
    }

    public void onStart() {
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: onFailure: 无法连接服务器！");
                baseHttpResultListener.onReqFailure(new BaseHttpExtException(0,"无法连接服务器，请检查网络"));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String body = response.body().string();
        Log.d(TAG, "onResponse: body: " + body);
        handler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(body);
            }
        });
    }

    private void handleResponse(final String body) {
        if (body == null || body.toString().trim().equals("")) {
            baseHttpResultListener.onReqFailure(new BaseHttpExtException(-1,"请求结果为空"));
            return;
        }
        try {
            if (body.startsWith("[")) {
                baseHttpResultListener.onRespMapList(body);
            }else if (body.startsWith("{")) {
                if (body.contains(NetUtil.STATUS)) {
                    if (body.contains(NetUtil.STATUS_SESSION_EXPIRED)) {
                        baseHttpResultListener.onRespSessionExpired();
                    }else {
                        baseHttpResultListener.onRespStatus(body);
                    }
                }else {
                    Log.d(TAG, "handleResponse: " + body);
                }
            }else {
                Log.d(TAG, "handleResponse: body: " +body);
                baseHttpResultListener.onRespError();
            }
        }catch (Exception e) {
            baseHttpResultListener.onReqFailure(new BaseHttpExtException(-3,e.getMessage()));
        }finally {
        }
    }
}
