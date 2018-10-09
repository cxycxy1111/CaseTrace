package com.alfredteng.casetrace.company;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfredteng.casetrace.MainActivity;
import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.ViewHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CompanyInfoActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText editText;
    private TextView tv_del;
    private TextView tv_status;
    private RelativeLayout rl_del;
    private RelativeLayout rl_status;
    private TextView tv_tips;
    private boolean isAdd = false;
    private long id = 0;
    private int req_type;
    private int operate_type;
    private String[] keys = new String[]{"id","name","del","status","creator","creator_type","nick_name","icon"};
    private static final int SOURCE_LOAD = 1;
    private static final int SOURCE_SUBMIT = 2;
    private static final int SOURCE_OPERATE = 3;
    private static final String TAG = "CompanyInfoActivity";
    private static final String TIPS_EMPTY = "公司名称不允许为空，请输入";
    private static final String TIPS_RESP_ERROR = "未知错误";
    private static final String TIPS_DUPLICATE = "公司名称重复";
    private static final String TIPS_FAIL = "新增失败，请重试";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        editText = (EditText)findViewById(R.id.et_name_a_company_info);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        rl_del = (RelativeLayout)findViewById(R.id.rl_del_name_a_company_info);
        rl_status = (RelativeLayout)findViewById(R.id.rl_status_name_a_company_info);
        tv_del = (TextView)findViewById(R.id.tv_del_main_name_a_company_info);
        tv_status = (TextView)findViewById(R.id.tv_status_main_name_a_company_info);
        isAdd = getIntent().getBooleanExtra("is_add",false);
        if (isAdd) {
            ViewHandler.initToolbar(this,toolbar,R.string.toolbar_tilte_company_add,R.id.toolbar_general);
            rl_del.setVisibility(View.GONE);
            rl_status.setVisibility(View.GONE);
        }else{
            ViewHandler.initToolbar(this,toolbar,R.string.toolbar_tilte_company_edit,R.id.toolbar_general);
        }
        editText.addTextChangedListener(watcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!isAdd) {
            id = getIntent().getLongExtra("id",0);
            req_type = getIntent().getIntExtra("req_type",0);
            String url = "/admin/company/qry/detail?id=" + id;
            NetUtil.reqSendGet(this,url,new HttpCallback(this,this,SOURCE_LOAD));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(1,101,1,"保存");
        if (!isAdd) {
            switch (req_type) {
                case PASSED:
                    menu.add(1,102,2,"删除");
                    break;
                case UNCHECKED:
                    menu.add(1,103,2,"拒绝");
                    menu.add(1,104,3,"通过");
                    menu.add(1,102,2,"删除");
                    break;
                case REJECTED:
                    menu.add(1,104,3,"通过");
                    menu.add(1,102,2,"删除");
                    break;
                case DELETED:
                    menu.add(1,105,2,"恢复");
                    break;
            }
        }
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: this.finish();break;
            case 101: save();break;
            case 102: operate(OP_DELETE);break;
            case 103: operate(OP_REJECT);break;
            case 104: operate(OP_PASS);break;
            case 105: operate(OP_RECOVER);break;
            default:break;
        }
        return true;
    }

    @Override
    public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        if (source == SOURCE_SUBMIT) {
            if (isAdd) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        ViewHandler.toastShow(CompanyInfoActivity.this, BaseActivity.OPERATE_ADD_SUCCESS);
                        CompanyInfoActivity.this.finish();
                        break;
                    case DUPLICATE:
                        tv_tips.setText(TIPS_DUPLICATE);
                        break;
                    case FAIL:
                        tv_tips.setText(BaseActivity.OPERATE_ADD_FAIL);
                        break;
                    case STATUS_SESSION_EXPIRED:
                        ViewHandler.alertShowAndExitApp(CompanyInfoActivity.this);
                        break;
                }
            }else {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        ViewHandler.toastShow(CompanyInfoActivity.this, BaseActivity.OPERATE_MODIFY_SUCCESS);
                        CompanyInfoActivity.this.finish();
                        break;
                    case DUPLICATE:
                        tv_tips.setText(TIPS_DUPLICATE);
                        break;
                    case FAIL:
                        tv_tips.setText(OPERATE_MODIFY_FAIL);
                        break;
                }
            }
        }else if (source == SOURCE_LOAD) {

        }else if (source == SOURCE_OPERATE) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case SUCCESS:
                    switch (operate_type) {
                        case OP_DELETE:
                            ViewHandler.toastShow(CompanyInfoActivity.this,"已删除");
                            break;
                        case OP_PASS:
                            ViewHandler.toastShow(CompanyInfoActivity.this,"已通过");
                            break;
                        case OP_RECOVER:
                            ViewHandler.toastShow(CompanyInfoActivity.this,"已恢复");
                            break;
                        case OP_REJECT:
                            ViewHandler.toastShow(CompanyInfoActivity.this,"已拒绝");
                            break;
                    }
                    setResult(1);
                    CompanyInfoActivity.this.finish();
                    break;
                case FAIL:
                    ViewHandler.toastShow(CompanyInfoActivity.this,"请求失败");
                    break;
            }
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
        if (source == SOURCE_LOAD) {
            ArrayList<Map<String,String>> arrayList = new ArrayList<>();
            arrayList = JsonUtil.strToListMap(body,keys);
            editText.setText(arrayList.get(0).get("name"));
            switch (Integer.parseInt(String.valueOf(arrayList.get(0).get("status")))){
                case PASSED:
                    tv_status.setText("已通过");
                    break;
                case UNCHECKED:
                    tv_status.setText("待审核");
                    break;
                case REJECTED:
                    tv_status.setText("已拒绝");
                    break;
            }
            boolean b = Boolean.parseBoolean(String.valueOf(arrayList.get(0).get("del")));
            if (b) {
                tv_del.setText("已删除");
            }else {
                tv_del.setText("未删除");
            }
        }else if (source == SOURCE_SUBMIT) {

        }
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }

    private void save() {
        String name = editText.getText().toString();
        if (name.equals("")) {
            tv_tips.setText(TIPS_EMPTY);
        }else {
            String url = "";
            if (isAdd) {
                url = "/admin/company/add?name=" + name;
            }else {
                if (id != 0) {
                    url = "/admin/company/edit?name=" + name + "&id=" + id;
                }
            }
            NetUtil.reqSendGet(this,url,new HttpCallback(this,this,SOURCE_SUBMIT));
        }
    }

    private void operate(final int operate_type) {
        String url = "";
        this.operate_type = operate_type;
        switch (operate_type) {
            case OP_DELETE:
                url = "/admin/company/delete?id=" + id;
                break;
            case OP_PASS:
                url = "/admin/company/pass?id=" + id;
                break;
            case OP_RECOVER:
                url = "/admin/company/recover?id=" + id;
                break;
            case OP_REJECT:
                url = "/admin/company/reject?id=" + id;
                break;
        }
        NetUtil.reqSendGet(this,url,new HttpCallback(this,this,SOURCE_OPERATE));
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
