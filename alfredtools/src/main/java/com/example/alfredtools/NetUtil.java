package com.example.alfredtools;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetUtil {

    private static final String TAG = "NetUtil";
    /**
     * 从服务器中返回的stat的值类型
     */
    public static final String CANT_CONNECT_INTERNET = "无法连接服务器";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String NULL_POINTER_ERROR = "空指针";

    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String DATA = "data";

    public static final String STATUS_NO_SUCH_RESULT = "no_such_record";
    public static final String STATUS_FAIL = "fail";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_DUPLICATE = "duplicate";
    public static final String STATUS_EMPTY = "empty";
    public static final String STATUS_PARTYLY_FAIL = "exe_partly_fail";
    static final String STATUS_NOT_MATCH = "not_match";
    static final String STATUS_SESSION_EXPIRED = "session_expired";
    static final String STATUS_AUTHORIZE_FAIL = "authorize_fail";

    //使用家里网络进行调试
    private static final String SERVER_SQ = "http://10.0.0.7:8080";
    //使用家里LINUX机器网络进行调试
    private static final String SERVER_SQ_LINUX= "http://10.0.0.8:8080";
    //使用冯静霞家的网络进行调试
    private static final String SERVER_FJX_HOME = "http://192.168.0.106:8080";
    //使用省移动网络进行调试
    private static final String SERVER_CHINA_MOBILE = "http://10.167.151.237:8080/Sailfish";
    //使用iPhone手机网络进行调试
    private static final String SERVER_ALFRED_IPHONE = "http://172.20.10.3:8080";
    //使用正式服务器进行调试
    private static final String SERVER_ALIYUN = "http://39.104.72.97:8080/Sailfish";

    private static final String SELECTED_HOST = SERVER_FJX_HOME;

    private static OkHttpClient client;

    /**
     * @param context Activity
     * @param address URL
     * @param callback 回调
     */
    public static void reqSendGet(Context context, String address, HttpCallback callback) {
        String str = SELECTED_HOST + address;
        Log.d(TAG, "reqSendGet: url: " + str);
        initOkHttpClient(context);
        Request request = new Request
                .Builder().url(str).method("GET",null).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     *
     * @param context
     * @param url
     * @param map
     * @param callback
     */
    public static void reqSendPost(Context context, String url, HashMap<String,Object> map, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        initOkHttpClient(context);
        for (Map.Entry<String,Object> entry:map.entrySet()) {
            builder.addEncoded(entry.getKey(),String.valueOf(entry.getValue()));
        }
        String str = SELECTED_HOST + url;
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(str).post(body).build();
        request.header("charset=utf-8");
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     *
     * @param resp
     * @return
     */
    public static int respAnalyse(String resp) {
        if (resp.startsWith("[")) {
            return BaseActivity.RESP_TYPE_MAPLIST;
        }else if (resp.startsWith("{")) {
            if (resp.contains(STATUS)) {
                return BaseActivity.RESP_TYPE_STAT;
            }else if (resp.contains(DATA)){
                return BaseActivity.RESP_TYPE_DATA;
            } else {
                return BaseActivity.RESP_TYPE_MAP;
            }
        }else {
            return BaseActivity.RESP_TYPE_ERROR;
        }
    }

    /**
     *
     * @param context Activity
     */
    private static void initOkHttpClient(Context context) {
        final Map<String,List<Cookie>> cookieStore = new HashMap<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).cookieJar(new CookieMgr(context));
        client = builder.build();
    }

}
