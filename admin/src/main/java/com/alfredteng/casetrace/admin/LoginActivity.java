package com.alfredteng.casetrace.admin;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alfredteng.casetrace.MainActivity;
import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.BaseHttpCallback;
import com.alfredteng.casetrace.util.BaseHttpResultListener;
import com.alfredteng.casetrace.util.NetRespStatType;
import com.alfredteng.casetrace.util.NetUtil;
import com.alfredteng.casetrace.util.SharedPrefMgr;
import com.alfredteng.casetrace.util.ViewHandler;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private EditText et_user_name,et_password;
    private Button btn_login;
    private TextView tv_tips;
    private static final String TAG = "LOGIN";

    private static final String TOOLBAR_TITLE = "管理员登录";
    private static final String TIPS_EMPTY_CONTENT = "用户名或密码不能为空，请重新输入。";
    private static final String TIPS_USER_NAME_TOO_LONG = "用户名过长，请重新输入。";
    private static final String TIPS_USER_NAME_PASSWORD_NOT_MATCH = "用户名与密码不匹配，请检查。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewHandler.initToolbar(this,toolbar,TOOLBAR_TITLE);
        et_user_name = (EditText)findViewById(R.id.et_login_user_name);
        et_password = (EditText)findViewById(R.id.et_login_password);
        btn_login = (Button)findViewById(R.id.btn_login_login);
        btn_login.setOnClickListener(this);
        et_user_name.addTextChangedListener(watcher);
        et_password.addTextChangedListener(watcher);
        tv_tips = (TextView)findViewById(R.id.tv_login_tips);
        tryInitEditText();
    }

    private void tryInitEditText() {
        et_user_name.setText(SharedPrefMgr.getSharedPref(LoginActivity.this,"user_data","user_name",SharedPrefMgr.STRING));
        et_password.setText(SharedPrefMgr.getSharedPref(LoginActivity.this,"user_data","password",SharedPrefMgr.STRING));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:
                String user_name = et_user_name.getText().toString();
                String password = et_password.getText().toString();
                checkInputContent(user_name,password);
                break;
            default:break;
        }
    }

    private void checkInputContent(String user_name,String password) {
        if (user_name.equals("") || password.equals("")) {
            tv_tips.setText(TIPS_EMPTY_CONTENT);
        }else if (user_name.length()>20) {
            tv_tips.setText(TIPS_USER_NAME_TOO_LONG);
        }else {
            login(user_name,password);
        }
    }

    private void login(final String user_name, final String password) {
        String url = "/admin/admin/login?user_name=" + user_name + "&pwd=" + password;
        Log.d(TAG, "login: url: " + url);
        NetUtil.reqSendGet(this,url,new BaseHttpCallback(new BaseHttpResultListener() {

            @Override
            public void onRespStatus(String body) {
                Log.d(TAG, "onRespStatus: " + NetRespStatType.dealWithRespStat(body));
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        SharedPrefMgr.CreateSharedPref(LoginActivity.this,"user_data");
                        SharedPrefMgr.setSharedPrefInt(LoginActivity.this,"user_data","remember_password",1);
                        SharedPrefMgr.setSharedPrefStr(LoginActivity.this,"user_data","user_name",user_name);
                        SharedPrefMgr.setSharedPrefStr(LoginActivity.this,"user_data","password",password);
                        ViewHandler.switchToActivity(LoginActivity.this, MainActivity.class);
                        break;
                    default:
                        tv_tips.setText(TIPS_USER_NAME_PASSWORD_NOT_MATCH);
                        break;
                }
            }

            @Override
            public void onRespSessionExpired() {

            }

            @Override
            public void onRespMapList(String body) {

            }

            @Override
            public void onRespError() {
                tv_tips.setText(NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onReqFailure(Object object) {
                tv_tips.setText(NetUtil.CANT_CONNECT_INTERNET);
            }
        },LoginActivity.this));
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tv_tips.setText("");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            tv_tips.setText("");
        }
    };
}
