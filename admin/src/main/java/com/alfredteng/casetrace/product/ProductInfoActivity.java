package com.alfredteng.casetrace.product;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProductInfoActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private Toolbar toolbar;
    private EditText editText;
    private TextView tv_del;
    private TextView tv_status;
    private RelativeLayout rl_del;
    private RelativeLayout rl_status;
    private TextView tv_tips;
    private Spinner sp_company;
    private int selected_company_position = 0;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> list_company = new ArrayList<>();
    private ArrayList<Map<String,String>> arryList_company = new ArrayList<>();
    ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private boolean isAdd = false;
    private long id = 0;
    private int req_type;
    private View[] views_should_hide_when_add;
    private View[] views_should_hide_when_modify;
    private String[] keys = new String[]{"id","name","company_id","company_name","del","status","creator","creator_type","create_time","nick_name","icon"};
    private String[] str_keys_company = new String[]{"id","name","status","del","creator","creator_type","nick_name","icon"};
    private static final String TAG = "productInfoActivity";
    private static final String TIPS_EMPTY = "产品名称不允许为空，请输入";
    private static final String TIPS_RESP_ERROR = "未知错误";
    private static final String TIPS_DUPLICATE = "产品名称重复";
    private static final String TIPS_FAIL = "新增失败，请重试";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        initViews();getIntents();
        if (isAdd) {
            ViewHandler.initToolbar(this,toolbar,R.string.toolbar_tilte_product_add);
            views_should_hide_when_add = new View[]{rl_del,rl_status};
            ViewHandler.viewHide(views_should_hide_when_add);
        }else{
            ViewHandler.initToolbar(this,toolbar,R.string.toolbar_tilte_product_edit);
        }
        editText.addTextChangedListener(watcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!isAdd) {
            id = getIntent().getLongExtra("id",0);
            req_type = getIntent().getIntExtra("req_type",0);
            load();
        }
        list_company.add("暂无公司");
        arrayAdapter = new ArrayAdapter<>(this,R.layout.tile_sp_item,list_company);
        sp_company.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_company.setOnItemSelectedListener(this);
        Map<String,String> map = new HashMap<>();
        map.put("id","-1");
        map.put("name","暂无公司");
        arryList_company.add(map);
        loadCompany();
    }

    private void initViews() {
        editText = (EditText)findViewById(R.id.et_name_a_product_info);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        rl_del = (RelativeLayout)findViewById(R.id.rl_del_name_a_product_info);
        rl_status = (RelativeLayout)findViewById(R.id.rl_status_name_a_product_info);
        tv_del = (TextView)findViewById(R.id.tv_del_main_name_a_product_info);
        tv_status = (TextView)findViewById(R.id.tv_status_main_name_a_product_info);
        sp_company = (Spinner)findViewById(R.id.sp_company_name_a_product_info);
    }

    private void getIntents() {
        isAdd = getIntent().getBooleanExtra("is_add",false);
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
            case android.R.id.home:
                this.finish();
                break;
            case 101:
                save();
                break;
            case 102:
                operate(OP_DELETE);
                break;
            case 103:
                operate(OP_REJECT);
                break;
            case 104:
                operate(OP_PASS);
                break;
            case 105:
                operate(OP_RECOVER);
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_company_position = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selected_company_position = 0;
    }

    private void load() {
        String url = "/admin/product/qry/detail?id=" + id;
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case STATUS_SESSION_EXPIRED:
                        ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
                        break;
                }
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
            }

            @Override
            public void onRespMapList(String body) throws IOException{
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
                loadCompany();
            }

            @Override
            public void onRespError() {
                Toast.makeText(ProductInfoActivity.this,NetUtil.UNKNOWN_ERROR,Toast.LENGTH_SHORT).show();
                ProductInfoActivity.this.finish();
            }

            @Override
            public void onReqFailure(Object object) {
                Toast.makeText(ProductInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET,Toast.LENGTH_SHORT).show();
                ProductInfoActivity.this.finish();
            }
        },ProductInfoActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void loadCompany() {
        String url = "/admin/company/qry/ignoreStatus";
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case EMPTY:
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                list_company.clear();
                arryList_company.clear();
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                List<String> list_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,str_keys_company);
                if (arrayList_temp.size() != 0) {
                    for (int i = 0;i < arrayList_temp.size();i++) {
                        list_temp.add(arrayList_temp.get(i).get("name"));
                    }
                    list_company.addAll(list_temp);
                    arryList_company.addAll(arrayList_temp);
                }
                arrayAdapter.notifyDataSetChanged();
                int i = 0;
                if (!isAdd) {
                    i = Tool.getPositionFromList(list_company,arrayList.get(0).get("company_name"));
                }else {
                }

                sp_company.setSelection(i);
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(ProductInfoActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(ProductInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
            }
        },ProductInfoActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void save() {
        String name = editText.getText().toString();
        if (name.equals("")) {
            tv_tips.setText(TIPS_EMPTY);
        }else {
            String url = "";
            if (isAdd) {
                url = "/admin/product/add?name=" + name + "&company=" + String.valueOf(arryList_company.get(selected_company_position).get("id"));
            }else {
                if (id != 0) {
                    url = "/admin/product/edit?name=" + name + "&id=" + id + "&company=" + String.valueOf(arryList_company.get(selected_company_position).get("id"));
                }
            }
            BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {

                @Override
                public void onRespStatus(String body) {
                    Log.d(TAG, "onRespStatus: ");
                    if (isAdd) {
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this, BaseActivity.OPERATE_ADD_SUCCESS);
                                ProductInfoActivity.this.finish();
                                break;
                            case DUPLICATE:
                                tv_tips.setText(TIPS_DUPLICATE);
                                break;
                            case FAIL:
                                tv_tips.setText(BaseActivity.OPERATE_ADD_FAIL);
                                break;
                            case STATUS_SESSION_EXPIRED:
                                ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
                                break;
                        }
                    }else {
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this, BaseActivity.OPERATE_MODIFY_SUCCESS);
                                ProductInfoActivity.this.finish();
                                break;
                            case DUPLICATE:
                                tv_tips.setText(TIPS_DUPLICATE);
                                break;
                            case FAIL:
                                tv_tips.setText(OPERATE_MODIFY_FAIL);
                                break;
                        }
                    }
                }

                @Override
                public void onRespMapList(String body) {
                    Log.d(TAG, "onRespMapList: ");
                }

                @Override
                public void onRespError() {
                    tv_tips.setText(TIPS_RESP_ERROR);
                }

                @Override
                public void onRespSessionExpired() {
                    ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
                }

                @Override
                public void onReqFailure(Object object) {
                    tv_tips.setText(NetUtil.CANT_CONNECT_INTERNET);
                }
            },ProductInfoActivity.this);
            NetUtil.reqSendGet(this,url,callback);
        }
    }

    private void operate(final int operate_type) {
        String url = "";
        switch (operate_type) {
            case OP_DELETE:
                url = "/admin/product/delete?id=" + id;
                break;
            case OP_PASS:
                url = "/admin/product/pass?id=" + id;
                break;
            case OP_RECOVER:
                url = "/admin/product/recover?id=" + id;
                break;
            case OP_REJECT:
                url = "/admin/product/reject?id=" + id;
                break;
        }

        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                if (NetRespStatType.dealWithRespStat(body).equals(NetRespStatType.STATUS_SESSION_EXPIRED)) {
                    ViewHandler.alertShowAndExitApp(ProductInfoActivity.this);
                    return;
                }
                switch (operate_type) {
                    case OP_DELETE:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(ProductInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this,"已删除");
                                setResult(1);
                                ProductInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_PASS:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(ProductInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this,"已通过");
                                setResult(1);
                                ProductInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_RECOVER:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(ProductInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this,"已恢复");
                                setResult(1);
                                ProductInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_REJECT:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(ProductInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(ProductInfoActivity.this,"已拒绝");
                                setResult(1);
                                ProductInfoActivity.this.finish();
                                break;
                        }
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
                Toast.makeText(ProductInfoActivity.this,NetUtil.UNKNOWN_ERROR,Toast.LENGTH_SHORT).show();
                ProductInfoActivity.this.finish();
            }

            @Override
            public void onReqFailure(Object object) {
                Toast.makeText(ProductInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET,Toast.LENGTH_SHORT).show();
                ProductInfoActivity.this.finish();
            }
        },ProductInfoActivity.this);
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
