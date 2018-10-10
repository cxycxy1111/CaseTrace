package com.alfredteng.casetrace.user.Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alfredteng.casetrace.user.MainActivity;
import com.alfredteng.casetrace.user.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.SharedPrefMgr;
import com.example.alfredtools.ViewHandler;

import java.io.IOException;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final int SOURCE_LOGIN = 1;
    private Toolbar toolbar;
    private TextView tv_tips;
    private EditText et_user_name,et_pwd;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewHandler.initToolbar(this,toolbar,R.string.toolbar_title_login,R.id.toolbar_general);
        initViews();
    }

    private void initViews() {
        tv_tips = (TextView)findViewById(R.id.tv_a_login_tips);
        et_user_name = (EditText)findViewById(R.id.et_a_login_user_name);
        et_pwd = (EditText)findViewById(R.id.et_a_login_password);
        btn_login = (Button)findViewById(R.id.btn_a_login_login);
        btn_login.setOnClickListener(this);
        et_user_name.addTextChangedListener(watcher);
        et_pwd.addTextChangedListener(watcher);
        et_user_name.setText(SharedPrefMgr.getSharedPref(LoginActivity.this,"user_data","user_name",SharedPrefMgr.STRING));
        et_pwd.setText(SharedPrefMgr.getSharedPref(LoginActivity.this,"user_data","password",SharedPrefMgr.STRING));

    }

    private void checkInputContent(String user_name,String password) {
        if (user_name.equals("") || password.equals("")) {
            tv_tips.setText(R.string.tips_empty_content);
        }else if (user_name.length()>20) {
            tv_tips.setText(R.string.tips_user_name_too_long);
        }else {
            String url = "/user/user/login?user_name=" + user_name + "&pwd=" + password;
            NetUtil.reqSendGet(this,url,new HttpCallback(this,this,SOURCE_LOGIN));
        }
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                SharedPrefMgr.CreateSharedPref(LoginActivity.this,"user_data");
                SharedPrefMgr.setSharedPrefInt(LoginActivity.this,"user_data","remember_password",1);
                SharedPrefMgr.setSharedPrefStr(LoginActivity.this,"user_data","user_name",et_user_name.getText().toString());
                SharedPrefMgr.setSharedPrefStr(LoginActivity.this,"user_data","password",et_pwd.getText().toString());
                ViewHandler.switchToActivity(LoginActivity.this, MainActivity.class);
                break;
            case FAIL:
                tv_tips.setText(R.string.tips_user_name_pwd_not_match);
            default:
                tv_tips.setText(R.string.tips_user_name_pwd_not_match);
                break;
        }
    }

    @Override public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
    }

    @Override public void onRespError(int source) {
        super.onRespError(source);
    }

    @Override public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
    }

    @Override public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_a_login_login:
                checkInputContent(et_user_name.getText().toString(),et_pwd.getText().toString());
                break;
            default:break;
        }
    }
}
