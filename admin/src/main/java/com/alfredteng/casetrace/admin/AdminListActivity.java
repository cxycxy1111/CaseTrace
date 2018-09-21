package com.alfredteng.casetrace.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.company.CompanyInfoActivity;
import com.alfredteng.casetrace.utils.BaseActivity;
import com.alfredteng.casetrace.utils.BaseHttpCallback;
import com.alfredteng.casetrace.utils.BaseHttpResultListener;
import com.alfredteng.casetrace.utils.JsonUtil;
import com.alfredteng.casetrace.utils.NetRespStatType;
import com.alfredteng.casetrace.utils.NetUtil;
import com.alfredteng.casetrace.utils.Tool;
import com.alfredteng.casetrace.utils.ViewHandler;
import com.alfredteng.casetrace.utils.adaptor.RecyclerViewAdaptor1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private RecyclerViewAdaptor1 adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_admin = new String[]{"id","nick_name","user_name","status","del",
            "email","type","create_time","icon","motto","company_id","name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_list);
        entity_type = getIntent().getIntExtra("entity_type",0);
        req_type = getIntent().getIntExtra("req_type",0);
        initToolbar(entity_type,req_type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        req(req_type);
    }

    private void req(final int req_type) {
        Map<String,String> map = new HashMap<>();
        map.put("holder_type","-1");
        arrayList.add(map);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_rv_list);
        adaptor1 = new RecyclerViewAdaptor1(arrayList,AdminListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminListActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adaptor1);
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {

            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case EMPTY:
                        arrayList.clear();
                        if (arrayList.size() == 0){
                            Map<String,String> map = new HashMap<>();
                            map.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_EMPTY));
                            arrayList.add(map);
                        }
                        adaptor1.notifyDataSetChanged();
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(AdminListActivity.this);
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                arrayList.clear();
                adaptor1.notifyDataSetChanged();
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,str_key_admin);
                str_body_key = "nick_name";
                for (int i = 0;i < arrayList_temp.size();i++) {
                    Map<String,String> map = new HashMap<>();
                    map = arrayList_temp.get(i);
                    map.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_ADMIN));
                    arrayList_temp.set(i,map);
                }
                if (arrayList_temp.size() == 0){
                    Map<String,String> map = new HashMap<>();
                    map.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_EMPTY));
                    arrayList_temp.add(map);
                }
                arrayList.addAll(arrayList_temp);
                adaptor1.notifyDataSetChanged();
                adaptor1.setStr_body_key(str_body_key);
                adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(AdminListActivity.this,AdminInfoActivity.class);
                        intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                        intent.putExtra("is_add",false);
                        intent.putExtra("status",Integer.parseInt(String.valueOf(arrayList.get(position).get("status"))));
                        intent.putExtra("del", Tool.parseStringToBool(String.valueOf(arrayList.get(position).get("del"))));
                        startActivityForResult(intent,1);
                    }
                });
            }

            @Override
            public void onRespError() {

            }

            @Override
            public void onReqFailure(Object object) {

            }
        },AdminListActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/").append("admin/qry/");
        switch (req_type) {
            case PASSED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_passed);
                builder.append("normal?page_no=1");
                break;
            case DELETED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_deleted);
                builder.append("deleted?page_no=1");
                break;
            case LOCKED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_locked);
                builder.append("locked?page_no=1");
                break;
            default:
                break;
        }
        url = builder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arrayList.clear();
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        adaptor1.setOnItemClickListener(null);
        req(req_type);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

}
