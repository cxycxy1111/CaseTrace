package com.example.alfredtools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.Tool;
import com.example.alfredtools.ViewHandler;

public class BaseActivity extends AppCompatActivity {

    public static final String OPERATE_ADD_SUCCESS = "新增成功";
    public static final String OPERATE_ADD_FAIL = "新增失败";
    public static final String OPERATE_DELETE_SUCCESS = "删除成功";
    public static final String OPERATE_DELETE_FAIL = "删除失败";
    public static final String OPERATE_MODIFY_SUCCESS = "修改成功";
    public static final String OPERATE_MODIFY_FAIL = "修改失败";
    public static final String OPERATE_SUCCESS = "操作成功";
    public static final String OPERATE_FAIL = "操作失败";
    public static final String OPERATE_PARTLY_FAIL = "部分操作失败";
    public static final String OPERATE_WRONG_NUMBER_FORMAT = "数字格式错误";
    public static final String OPERATE_EMPTY_ESSENTIAL_INFO = "必要信息不能为空";
    public static final String OPERATE_NO_SUCH_RESULT = "不存在此记录";
    public static final String OPERATE_DUPLICATED = "重复操作";
    public static final String ALERT_STATUS_SESSION_EXPIRED = "登录已过期，请重新登录";
    public static final String OPERATE_VIEW_STATUS_AUTHORIZE_FAIL = "你没有权限查看，请联系管理员";
    public static final String OPERATE_MANAGE_STATUS_AUTHORIZE_FAIL = "你没有权限管理，请联系管理员";
    public static final String OPERATE_MEMBER_CARD_EXPIRED = "会员卡已过期，无法签到";
    public static final String OPERATE_REST_TIMES_NOT_ENOUGH = "剩余次数不足，无法签到";
    public static final String OPERATE_BALANCE_NOT_ENOUGH = "余额不足，无法签到";


    public static final int RESP_TYPE_MAPLIST = 1;
    public static final int RESP_TYPE_MAP = 2;
    public static final int RESP_TYPE_STAT = 4;
    public static final int RESP_TYPE_ERROR = 5;
    public static final int RESP_TYPE_DATA = 6;

    public static final int DELETED = -1;
    public static final int PASSED = 0;
    public static final int UNCHECKED = 1;
    public static final int REJECTED = 2;
    public static final int LOCKED = 1;

    public static final int OP_DELETE = 1;
    public static final int OP_RECOVER = 2;
    public static final int OP_LOCK = 5;
    public static final int OP_PASS = 3;
    public static final int OP_REJECT = 4;
    public static final int OP_ADD = 6;
    public static final int OP_MODIFY = 7;
    public static final int OP_UNLOCK = 8;

    public static final int ENTITY_EVENT = 1;
    public static final int ENTITY_CASE = 2;
    public static final int ENTITY_TIMELINE = 3;
    public static final int ENTITY_COMPANY = 4;
    public static final int ENTITY_PRODUCT = 5;
    public static final int ENTITY_USER = 6;
    public static final int ENTITY_ADMIN = 7;

    public static final int LOAD_NUM = 10;


    private ProgressBar progressBar;

    public BaseActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMgr.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMgr.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(),"请点击左上角的返回按钮返回上一界面",Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createProgressBar(Activity activity) {
        Context context = activity;
        FrameLayout rootFrameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(layoutParams);
        progressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(progressBar);
    }

    public ProgressBar getProgressBar(Activity activity) {
        if (progressBar == null) {
            createProgressBar(activity);
        }
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

}
