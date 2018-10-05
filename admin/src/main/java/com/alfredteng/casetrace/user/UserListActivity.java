package com.alfredteng.casetrace.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.Tool;
import com.example.alfredtools.ViewHandler;
import com.alfredteng.casetrace.util.adaptor.GeneralRecyclerViewAdaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private GeneralRecyclerViewAdaptor adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private int page_no = 1;
    private boolean isLoadEnd = false;
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
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_rv_list);
        req(req_type);
    }

    private void req(final int req_type) {
        if (page_no ==1) {
            Map<String,String> map = new HashMap<>();
            map.put("holder_type","-1");
            arrayList.add(map);
        }
        adaptor1 = new GeneralRecyclerViewAdaptor(arrayList,UserListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(UserListActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor1);
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/");
        switch (entity_type) {
            case ENTITY_ADMIN:
                builder.append("admin/qry/");
                break;
            case ENTITY_USER:
                builder.append("user/qry/");
                break;
            case ENTITY_COMPANY:
                builder.append("company/qry/");
                break;
            case ENTITY_PRODUCT:
                builder.append("product/qry/");
                break;
            case ENTITY_EVENT:
                builder.append("event/qry/");
                break;
            case ENTITY_TIMELINE:
                builder.append("timeline/qry/");
                break;
            case ENTITY_CASE:
                builder.append("case/qry/");
                break;
            default:break;
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
                    builder.append("normal?page_no=").append(page_no);
                    break;
                case UNCHECKED:
                    builder.append("unchecked?page_no=1").append(page_no);
                    break;
                case REJECTED:
                    builder.append("rejected?page_no=1").append(page_no);
                    break;
                case DELETED:
                    builder.append("deleted?page_no=1").append(page_no);
                    break;
            }
        }
        url = builder.toString();
        HttpCallback callback = new HttpCallback(new HttpResultListener() {

            @Override
            public void onRespStatus(String body,int source) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case EMPTY:
                        arrayList.clear();
                        if (arrayList.size() == 0){
                            Map<String,String> map = new HashMap<>();
                            map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_EMPTY));
                            ArrayList<Map<String,String>> list = new ArrayList<>();
                            list.add(map);
                            arrayList.addAll(list);
                        }
                        adaptor1.notifyDataSetChanged();
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(UserListActivity.this);
            }

            @Override
            public void onRespMapList(String body,int source) throws IOException {
                if (page_no == 1) {
                    arrayList.clear();
                }
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
                    map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_ADMIN));
                    arrayList_temp.set(i,map);
                }
                if (arrayList_temp.size() == 0){
                    Map<String,String> map = new HashMap<>();
                    map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_EMPTY));
                    arrayList_temp.add(map);
                }
                if (arrayList_temp.size() >= BaseActivity.LOAD_NUM) {
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_LOAD_MORE));
                    arrayList_temp.add(map1);
                }else {
                    if (page_no != 1) {
                        arrayList.remove(arrayList.size()-1);
                    }
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_END));
                    arrayList_temp.add(map1);
                    isLoadEnd = true;
                }
                arrayList.addAll(arrayList_temp);
                adaptor1.setStr_body_key(str_body_key);
                adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(UserListActivity.this,UserInfoActivity.class);
                        intent.putExtra("isAdd",false);
                        intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                        intent.putExtra("status",Integer.parseInt(String.valueOf(arrayList.get(position).get("status"))));
                        if (Tool.parseStringToBool(String.valueOf(arrayList.get(position).get("del")))) {
                            intent.putExtra("del",true);
                        }else {
                            intent.putExtra("del",false);
                        }
                        startActivityForResult(intent,1);
                    }
                });
                if (isLoadEnd) {
                    adaptor1.setOnLoadMoreClickListener(new GeneralRecyclerViewAdaptor.OnLoadMoreClickListener() {
                        @Override
                        public void onLoadMoreClick(View view, int position) {

                        }
                    });
                }else {
                    adaptor1.setOnLoadMoreClickListener(new GeneralRecyclerViewAdaptor.OnLoadMoreClickListener() {
                        @Override
                        public void onLoadMoreClick(View view, int position) {
                            req(req_type);
                        }
                    });
                }
                page_no = page_no + 1;
                adaptor1.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adaptor1.getItemCount()-1);
            }

            @Override
            public void onRespError(int source) {
                arrayList.clear();
                Map<String,String> map = new HashMap<>();
                map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_ERROR));
                ArrayList<Map<String,String>> list = new ArrayList<>();
                list.add(map);
                arrayList.addAll(list);
                adaptor1.notifyDataSetChanged();
            }

            @Override
            public void onReqFailure(Object object,int source) {
                arrayList.clear();
                Map<String,String> map = new HashMap<>();
                map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_NET_ERROR));
                ArrayList<Map<String,String>> list = new ArrayList<>();
                list.add(map);
                arrayList.addAll(list);
                adaptor1.notifyDataSetChanged();

            }
        },UserListActivity.this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {

        switch (entity_type) {
            case ENTITY_ADMIN:

                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_passed,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_deleted,R.id.toolbar_general);
                        break;
                    case LOCKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_admin_locked,R.id.toolbar_general);
                        break;
                    default:
                        break;
                }
                break;
            case ENTITY_USER:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_passed,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_deleted,R.id.toolbar_general);
                        break;
                    case LOCKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_user_locked,R.id.toolbar_general);
                        break;
                    default:
                        break;
                }
                break;
            case ENTITY_COMPANY:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_passed,R.id.toolbar_general);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_unchecked,R.id.toolbar_general);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_rejected,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_company_deleted,R.id.toolbar_general);
                        break;
                }
                break;
            case ENTITY_PRODUCT:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_passed,R.id.toolbar_general);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_unchecked,R.id.toolbar_general);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_rejected,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_deleted,R.id.toolbar_general);
                        break;
                }
                break;
            case ENTITY_EVENT:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_passed,R.id.toolbar_general);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_unchecked,R.id.toolbar_general);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_rejected,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_event_deleted,R.id.toolbar_general);
                        break;
                }
                break;
            case ENTITY_TIMELINE:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_passed,R.id.toolbar_general);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_unchecked,R.id.toolbar_general);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_rejected,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_timeline_deleted,R.id.toolbar_general);
                        break;
                }
                break;
            case ENTITY_CASE:
                switch (req_type) {
                    case PASSED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_passed,R.id.toolbar_general);
                        break;
                    case UNCHECKED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_unchecked,R.id.toolbar_general);
                        break;
                    case REJECTED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_rejected,R.id.toolbar_general);
                        break;
                    case DELETED:
                        ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_case_deleted,R.id.toolbar_general);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        page_no =1;
        isLoadEnd =false;
        arrayList.clear();
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        adaptor1.setOnItemClickListener(null);
        adaptor1 = null;
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
