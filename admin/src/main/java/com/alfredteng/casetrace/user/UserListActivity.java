package com.alfredteng.casetrace.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.alfredteng.casetrace.utils.ViewHandler;
import com.alfredteng.casetrace.utils.adaptor.RecyclerViewAdaptor1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private RecyclerViewAdaptor1 adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_event = new String[]{"id","title","del","status","happen_time",
            "subcribe_count","creator_type","creator"};
    private String[] str_key_case = new String[]{"id","title","creator","creator_type",
            "case_happen_time","case_create_time","update_time", "upvote_count",
            "downvote_count","view_count","event_id","event_title"};
    private String[] str_key_timeline = new String[]{"id","name","happen_time","creator",
            "creator_type","nick_name","icon"};
    private String[] str_key_company = new String[]{"id","name","del","status","creator",
            "creator_type","nick_name","icon"};
    private String[] str_key_product = new String[]{"id","name","company_id","company_name",
            "status","del","nick_name","icon","creator","creator_type","create_time",
            "update_time"};
    private String[] str_key_user = new String[]{"id","nick_name","user_name","status","del",
            "email","create_time","icon","motto"};
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
        adaptor1 = new RecyclerViewAdaptor1(arrayList,UserListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this,LinearLayoutManager.VERTICAL,false));
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
                            ArrayList<Map<String,String>> list = new ArrayList<>();
                            list.add(map);
                            arrayList.addAll(list);
                        }
                        adaptor1.notifyDataSetChanged();
                }
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(UserListActivity.this);
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                arrayList.clear();
                adaptor1.notifyDataSetChanged();
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                switch (entity_type) {
                    case ENTITY_ADMIN:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_admin);
                        str_body_key = "nick_name";
                        break;
                    case ENTITY_USER:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_user);
                        str_body_key = "nick_name";
                        break;
                    case ENTITY_COMPANY:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_company);
                        str_body_key = "name";
                        break;
                    case ENTITY_PRODUCT:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_product);
                        str_body_key = "name";
                        break;
                    case ENTITY_CASE:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_case);
                        str_body_key = "title";
                        break;
                    case ENTITY_EVENT:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_event);
                        str_body_key = "title";
                        break;
                    case ENTITY_TIMELINE:
                        arrayList_temp = JsonUtil.strToListMap(body,str_key_timeline);
                        break;
                }
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
                switch (entity_type) {
                    case ENTITY_ADMIN:
                        break;
                    case ENTITY_USER:
                        break;
                    case ENTITY_COMPANY:
                        adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(UserListActivity.this, CompanyInfoActivity.class);
                                intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                                intent.putExtra("is_add",false);
                                intent.putExtra("req_type",req_type);
                                startActivityForResult(intent,1);
                            }
                        });
                        break;
                    case ENTITY_PRODUCT:
                        break;
                    case ENTITY_EVENT:
                        break;
                    case ENTITY_TIMELINE:
                        break;
                    case ENTITY_CASE:
                        break;
                }
            }

            @Override
            public void onRespError() {
                arrayList.clear();
                Map<String,String> map = new HashMap<>();
                map.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_ERROR));
                ArrayList<Map<String,String>> list = new ArrayList<>();
                list.add(map);
                arrayList.addAll(list);
                adaptor1.notifyDataSetChanged();
            }

            @Override
            public void onReqFailure(Object object) {
                arrayList.clear();
                Map<String,String> map = new HashMap<>();
                map.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_NET_ERROR));
                ArrayList<Map<String,String>> list = new ArrayList<>();
                list.add(map);
                arrayList.addAll(list);
                adaptor1.notifyDataSetChanged();

            }
        },UserListActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/");
        switch (entity_type) {
            case ENTITY_ADMIN:
                builder.append("admin/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_passed);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_deleted);
                        break;
                    case LOCKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_locked);
                        break;
                    default:
                        break;
                }
                break;
            case ENTITY_USER:
                builder.append("user/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_passed);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_deleted);
                        break;
                    case LOCKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_locked);
                        break;
                    default:
                        break;
                }
                break;
            case ENTITY_COMPANY:
                builder.append("company/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_passed);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_unchecked);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_rejected);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_deleted);
                        break;
                }
                break;
            case ENTITY_PRODUCT:
                builder.append("product/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_passed);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_unchecked);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_rejected);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_deleted);
                        break;
                }
                break;
            case ENTITY_EVENT:
                builder.append("event/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_passed);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_unchecked);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_rejected);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_deleted);
                        break;
                }
                break;
            case ENTITY_TIMELINE:
                builder.append("timeline/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_passed);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_unchecked);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_rejected);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_deleted);
                        break;
                }
                break;
            case ENTITY_CASE:
                builder.append("case/qry/");
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_passed);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_unchecked);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_rejected);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_deleted);
                        break;
                }
                break;
        }
        if (entity_type == ENTITY_ADMIN || entity_type == ENTITY_USER) {
            switch (req_type) {
                case PASSED:
                    builder.append("normal?page_no=1");
                    break;
                case LOCKED:
                    builder.append("locked?page_no=1");
                    break;
                case DELETED:
                    builder.append("deleted?page_no=1");
                    break;
            }
        } else {
            switch (req_type) {
                case PASSED:
                    builder.append("normal?page_no=1");
                    break;
                case UNCHECKED:
                    builder.append("unchecked?page_no=1");
                    break;
                case REJECTED:
                    builder.append("rejected?page_no=1");
                    break;
                case DELETED:
                    builder.append("deleted?page_no=1");
                    break;
            }
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
