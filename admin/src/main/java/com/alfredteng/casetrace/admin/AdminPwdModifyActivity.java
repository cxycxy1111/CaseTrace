package com.alfredteng.casetrace.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.utils.BaseActivity;
import com.alfredteng.casetrace.utils.BaseHttpCallback;
import com.alfredteng.casetrace.utils.BaseHttpResultListener;
import com.alfredteng.casetrace.utils.NetRespStatType;
import com.alfredteng.casetrace.utils.NetUtil;
import com.alfredteng.casetrace.utils.ViewHandler;

import java.io.IOException;

public class AdminPwdModifyActivity extends BaseActivity {

    private int source = 0;
    private long id = 0;
    private Toolbar toolbar;
    private EditText et_pwd;
    private EditText et_pwd_rpt;
    private TextView tv_tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pwd_modify);
        ViewHandler.initToolbarWithBackButton(this,toolbar,"修改密码");
        et_pwd = (EditText)findViewById(R.id.et_pwd_a_admin_pwd_modify);
        et_pwd_rpt = (EditText)findViewById(R.id.et_rpt_pwd_a_admin_pwd_modify);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        et_pwd.addTextChangedListener(watcher);
        et_pwd_rpt.addTextChangedListener(watcher);
        id = getIntent().getLongExtra("id",0);
        source = getIntent().getIntExtra("source",0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,101,1,"提交");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 101:
                checkBeforeSubmit();
                break;
            default:break;
        }
        return true;
    }

    public void checkBeforeSubmit() {
        if (!et_pwd_rpt.getText().toString().equals(et_pwd.getText().toString())) {
            tv_tips.setText("两次输入的密码不一致，请重新输入");
            return;
        }
        if (et_pwd_rpt.getText().toString().equals("") | et_pwd.getText().toString().equals("")) {
            tv_tips.setText("密码或重复密码不能为空，请重新输入");
            return;
        }
        submit();
    }

    private void submit() {
        String url = "";
        switch (source) {
            case 0:
                url = "/admin/admin/edit/password?id=" + id + "&pwd=" + et_pwd.getText().toString();
                break;
            case 1:
                url = "/admin/user/edit/password?id=" + id + "&pwd=" + et_pwd.getText().toString();
                break;
        }
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        ViewHandler.toastShow(AdminPwdModifyActivity.this,BaseActivity.OPERATE_MODIFY_SUCCESS);
                        break;
                    case FAIL:
                        ViewHandler.toastShow(AdminPwdModifyActivity.this,BaseActivity.OPERATE_MODIFY_FAIL);
                        break;
                }
            }

            @Override
            public void onRespMapList(String body) throws IOException {

            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(AdminPwdModifyActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(AdminPwdModifyActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(AdminPwdModifyActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,url,callback);
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
