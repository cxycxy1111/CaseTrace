package com.example.alfredtools;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpCallback implements Callback {

    private static final String TAG = "HttpCallback";
    private Handler handler;
    private HttpResultListener HttpResultListener;
    private Context context;

    public HttpCallback(HttpResultListener HttpResultListener,Context context) {
        this.HttpResultListener = HttpResultListener;
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
                HttpResultListener.onReqFailure(new HttpExtException(0,"无法连接服务器，请检查网络"));
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
            HttpResultListener.onReqFailure(new HttpExtException(-1,"请求结果为空"));
            return;
        }
        try {
            if (body.startsWith("[")) {
                HttpResultListener.onRespMapList(body);
            }else if (body.startsWith("{")) {
                if (body.contains(NetUtil.STATUS)) {
                    if (body.contains(NetUtil.STATUS_SESSION_EXPIRED)) {
                        HttpResultListener.onRespSessionExpired();
                    }else {
                        HttpResultListener.onRespStatus(body);
                    }
                }else {
                    Log.d(TAG, "handleResponse: " + body);
                }
            }else {
                Log.d(TAG, "handleResponse: body: " +body);
                HttpResultListener.onRespError();
            }
        }catch (Exception e) {
            HttpResultListener.onReqFailure(new HttpExtException(-3,e.getMessage()));
        }finally {
        }
    }

}
