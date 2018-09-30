package com.alfredteng.casetrace.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alfredteng.casetrace.MainActivity;
import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.BaseHttpCallback;
import com.alfredteng.casetrace.util.BaseHttpResultListener;
import com.alfredteng.casetrace.util.JsonUtil;
import com.alfredteng.casetrace.util.NetRespStatType;
import com.alfredteng.casetrace.util.NetUtil;
import com.alfredteng.casetrace.util.Tool;
import com.alfredteng.casetrace.util.ViewHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminInfoActivity extends BaseActivity {

    private Toolbar toolbar;
    private ImageView ig_icon;
    private EditText et_user_name,et_nick_name,et_email,et_motto,et_pwd;
    private TextView tv_user_name,tv_status,tv_del;
    private RelativeLayout rl_status,rl_del,rl_pwd;
    private Spinner sp_type,sp_company;

    private static final String TAG = "AdminInfoActivity";
    private long id = 0;
    private int selected_sp_company_pos = 0;
    private int status;
    private int selected_admin_type = 1;
    private boolean del = false;
    private boolean isAdd = false;
    private String[] str_keys = new String[]{"id","nick_name","user_name","status","del","type",
            "email","create_time","icon","motto","company_id","name"};
    private String[] str_keys_company = new String[]{"id","name","status","del","creator","creator_type","nick_name","icon"};
    private List<String> list_company = new ArrayList<>();
    private List<String> list_admin_type = new ArrayList<>();

    private View[] views_add;
    private View[] views_modify;

    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private ArrayList<Map<String,String>> arrayList_company = new ArrayList<>();
    private ArrayAdapter<String> adapter_company,adapter_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_info);
        initViews();
        isAdd = getIntent().getBooleanExtra("is_add",false);
        initAdminType();
        if (isAdd) {
            views_add = new View[]{tv_user_name,rl_del,rl_status};

            ViewHandler.viewHide(views_add);
            ViewHandler.initToolbarWithBackButton(this,toolbar,"新增管理员");
            initSpinnerType();
            loadCompany();
        }else {
            status = getIntent().getIntExtra("status",0);
            del = getIntent().getBooleanExtra("del",false);
            id = getIntent().getLongExtra("id",0);
            if (del) {
                tv_del.setText("已删除");
            }else {
                tv_del.setText("未删除");
            }
            views_modify = new View[]{rl_pwd,et_user_name};
            ViewHandler.viewHide(views_modify);
            ViewHandler.initToolbarWithBackButton(this,toolbar,"编辑管理员");
            load(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,101,1,"保存");
        if (!isAdd) {
            menu.add(1,102,2,"修改头像");
            menu.add(1,103,3,"修改密码");
            if (del) {
                menu.add(1,104,4,"恢复");
            }else {
                menu.add(1,107,7,"删除");
                if (status == 0) {
                    menu.add(1,105,5,"锁定");
                }else {
                    menu.add(1,106,6,"解除锁定");
                }
            }
        }
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 101:
                if (isAdd) {
                    checkBeforeSubmit(BaseActivity.OP_ADD);
                }else {
                    checkBeforeSubmit(BaseActivity.OP_MODIFY);
                }
                break;
            case 102:
                break;
            case 103:
                Intent intent = new Intent(AdminInfoActivity.this,AdminPwdModifyActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("source",0);
                startActivityForResult(intent,1);
                break;
            case 104:
                checkBeforeSubmit(BaseActivity.OP_RECOVER);
                break;
            case 105:
                checkBeforeSubmit(BaseActivity.OP_LOCK);
                break;
            case 106:
                checkBeforeSubmit(BaseActivity.OP_UNLOCK);
                break;
            case 107:
                checkBeforeSubmit(BaseActivity.OP_DELETE);
                break;
            default:break;
        }
        return true;
    }

    private void initViews() {
        ig_icon = (ImageView)findViewById(R.id.ig_icon_a_admin_info);
        et_user_name = (EditText)findViewById(R.id.et_name_a_admin_info);
        et_nick_name = (EditText)findViewById(R.id.et_nickname_a_admin_info);
        et_email = (EditText)findViewById(R.id.et_email_a_admin_info);
        et_motto = (EditText)findViewById(R.id.et_motto_a_admin_info);
        tv_user_name = (TextView)findViewById(R.id.tv_noedit_name_a_admin_info);
        tv_del = (TextView)findViewById(R.id.tv_noedit_del_a_admin_info);
        tv_status = (TextView)findViewById(R.id.tv_noedit_status_a_admin_info);
        sp_company = (Spinner)findViewById(R.id.sp_company_name_a_admin_info);
        sp_type = (Spinner)findViewById(R.id.sp_type_a_admin_info);
        rl_status = (RelativeLayout)findViewById(R.id.rl_status_a_admin_info);
        rl_del = (RelativeLayout)findViewById(R.id.rl_del_a_admin_info);
        rl_pwd = (RelativeLayout)findViewById(R.id.rl_pwd_a_admin_info);
        et_pwd = (EditText)findViewById(R.id.et_pwd_a_admin_info);
    }

    private void initAdminType() {
        list_admin_type.add("超级管理员");
        list_admin_type.add("普通管理员");
        list_admin_type.add("公司超级管理员");
        list_admin_type.add("公司普通管理员");
    }

    private void load(long id) {
        String url = "/admin/admin/qry/detail?id=" + id;
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {

            }

            @Override
            public void onRespMapList(String body) throws IOException {
                arrayList = JsonUtil.strToListMap(body,str_keys);
                Map<String,String> map = new HashMap<>();
                map = arrayList.get(0);
                selected_admin_type = Integer.parseInt(String.valueOf(map.get("type")));

                tv_user_name.setText(map.get("user_name"));
                et_nick_name.setText(map.get("nick_name"));
                et_email.setText(map.get("email"));
                et_motto.setText(map.get("motto"));
                switch (Integer.parseInt(String.valueOf(map.get("status")))) {
                    case 0:
                        tv_status.setText("正常");
                        break;
                    case 1:
                        tv_status.setText("已锁定");
                        break;
                    default:break;
                }
                initSpinnerType();
                loadCompany();
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(AdminInfoActivity.this, NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(AdminInfoActivity.this, NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(AdminInfoActivity.this);
            }
        },AdminInfoActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initSpinnerType() {
        adapter_type = new ArrayAdapter<String>(AdminInfoActivity.this,R.layout.tile_sp_item,list_admin_type);
        adapter_type.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_type.setAdapter(adapter_type);
        sp_type.setSelection(selected_admin_type-1);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_admin_type = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_admin_type = 1;
            }
        });

    }

    private void loadCompany() {
        String url = "/admin/company/qry/ignoreStatus";
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case EMPTY:
                        Map<String,String> map = new HashMap<>();
                        map.put("id","0");
                        map.put("name","暂无公司");
                        arrayList_company.add(map);
                        refreshCompanySpinner();
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                arrayList_company = JsonUtil.strToListMap(body,str_keys_company);
                for (int i = 0;i < arrayList_company.size();i++) {
                    list_company.add(arrayList_company.get(i).get("name"));
                }
                Tool.sortListMapAsc(arrayList_company,"name");
                Tool.sortListAsc(list_company);
                refreshCompanySpinner();
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(AdminInfoActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(AdminInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(AdminInfoActivity.this);
            }
        },AdminInfoActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void refreshCompanySpinner() {
        adapter_company = new ArrayAdapter<String>(AdminInfoActivity.this,R.layout.tile_sp_item,list_company);
        adapter_company.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_company.setAdapter(adapter_company);
        sp_company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sp_company_pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_sp_company_pos = 0;
            }
        });
        int i = Tool.getPositionFromList(list_company,String.valueOf(arrayList.get(0).get("name")));
        sp_company.setSelection(i);
    }

    private void checkBeforeSubmit(int op_type) {
        StringBuilder url = new StringBuilder();
        url.append("/admin/admin");
        switch (op_type) {
            case BaseActivity.OP_ADD:
                if (et_user_name.getText().toString().equals("") | et_pwd.getText().toString().equals("")) {
                    ViewHandler.toastShow(AdminInfoActivity.this,"用户名或密码为空，请补充");
                    return;
                }
                if (et_nick_name.getText().toString().length() > 20 | et_motto.getText().toString().length()>100 | et_email.getText().toString().length() > 100 || et_user_name.getText().toString().length() > 20) {
                    ViewHandler.toastShow(AdminInfoActivity.this,"部分资料过长，请重新输入");
                    return;
                }
                url.append("/add?company=").append(String.valueOf(arrayList_company.get(selected_sp_company_pos).get("id")))
                        .append("&user_name=").append(et_user_name.getText().toString())
                        .append("&pwd=").append(et_pwd.getText().toString())
                        .append("&type=").append(selected_admin_type)
                        .append("&email=").append(et_email.getText().toString())
                        .append("&motto=").append(et_motto.getText().toString());
                if (et_nick_name.getText().toString().equals("")) {
                    url.append("&nick_name=").append(et_user_name.getText().toString());
                }else {
                    url.append("&nick_name=").append(et_nick_name.getText().toString());
                }
                break;
            case BaseActivity.OP_MODIFY:
                if (et_nick_name.getText().toString().equals("")) {
                    ViewHandler.toastShow(AdminInfoActivity.this,"昵称不能为空，请输入");
                }
                if (et_nick_name.getText().toString().length() > 20 ||et_motto.getText().toString().length()>100 || et_email.getText().toString().length() > 100 || et_user_name.getText().toString().length() > 20) {
                    ViewHandler.toastShow(AdminInfoActivity.this,"部分资料过长，请重新输入");
                    return;
                }
                url.append("/edit?company=").append(String.valueOf(arrayList_company.get(selected_sp_company_pos).get("id")))
                        .append("&type=").append(String.valueOf(selected_admin_type))
                        .append("&email=").append(et_email.getText().toString())
                        .append("&motto=").append(et_motto.getText().toString());
                if (et_nick_name.getText().toString().equals("")) {
                    url.append("&nick_name=").append(et_user_name.getText().toString());
                }else {
                    url.append("&nick_name=").append(et_nick_name.getText().toString());
                }
                url.append("&id=").append(id);
                break;
            case BaseActivity.OP_DELETE:
                url.append("/delete?id=").append(id);
                break;
            case BaseActivity.OP_RECOVER:
                url.append("/recover?id=").append(id);
                break;
            case BaseActivity.OP_LOCK:
                url.append("/lock?id=").append(id);
                break;
            case BaseActivity.OP_UNLOCK:
                url.append("/unlock?id=").append(id);
                break;
        }
        Log.d(TAG, "checkBeforeSubmit: " + url.toString());
        submit(url.toString());
    }

    private void submit(String url) {
        BaseHttpCallback baseHttpCallback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case DUPLICATE:
                        ViewHandler.toastShow(AdminInfoActivity.this,"用户名重复，请重新输入");
                        break;
                    case FAIL:
                        ViewHandler.toastShow(AdminInfoActivity.this,"操作失败，请重试");
                        break;
                    case SUCCESS:
                        ViewHandler.toastShow(AdminInfoActivity.this,"操作成功");
                        AdminInfoActivity.this.finish();
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body) throws IOException {

            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(AdminInfoActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(AdminInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(AdminInfoActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,url,baseHttpCallback);
    }

}
