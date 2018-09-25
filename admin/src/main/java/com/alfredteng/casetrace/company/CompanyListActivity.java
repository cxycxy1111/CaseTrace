package com.alfredteng.casetrace.company;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.alfredteng.casetrace.MainActivity;
import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.BaseHttpCallback;
import com.alfredteng.casetrace.util.BaseHttpResultListener;
import com.alfredteng.casetrace.util.JsonUtil;
import com.alfredteng.casetrace.util.NetRespStatType;
import com.alfredteng.casetrace.util.NetUtil;
import com.alfredteng.casetrace.util.ViewHandler;
import com.alfredteng.casetrace.util.RecyclerViewAdaptor1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompanyListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private RecyclerViewAdaptor1 adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private int page_no = 1;
    private boolean isLoadEnd = false;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_company = new String[]{"id","name","del","status","creator",
            "creator_type","nick_name","icon"};

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
        if (page_no == 1) {
            final Map<String,String> map = new HashMap<>();
            map.put("holder_type","-1");
            arrayList.add(map);
        }
        adaptor1 = new RecyclerViewAdaptor1(arrayList,CompanyListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(CompanyListActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adaptor1);
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/");
        builder.append("company/qry/");
        switch (req_type) {
            case PASSED:
                builder.append("normal?page_no=").append(page_no);
                break;
            case UNCHECKED:
                builder.append("unchecked?page_no=").append(page_no);
                break;
            case REJECTED:
                builder.append("rejected?page_no=1").append(page_no);
                break;
            case DELETED:
                builder.append("deleted?page_no=1").append(page_no);
                break;
        }
        url = builder.toString();
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
                        adaptor1.notifyDataSetChanged();break;
                    default:break;
                }
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(CompanyListActivity.this);
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                if (page_no == 1) {
                    arrayList.clear();
                }
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,str_key_company);
                str_body_key = "name";
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
                if (arrayList_temp.size() >= BaseActivity.LOAD_NUM) {
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_LOAD_MORE));
                    arrayList_temp.add(map1);
                }else {
                    if (page_no != 1) {
                        arrayList.remove(arrayList.size()-1);
                    }
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_END));
                    arrayList_temp.add(map1);
                    isLoadEnd = true;
                }
                arrayList.addAll((page_no-1)*BaseActivity.LOAD_NUM,arrayList_temp);
                adaptor1.setStr_body_key(str_body_key);
                adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CompanyListActivity.this, CompanyInfoActivity.class);
                        intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                        intent.putExtra("is_add",false);
                        intent.putExtra("req_type",req_type);
                        startActivityForResult(intent,1);
                    }
                });
                if (isLoadEnd) {
                    adaptor1.setOnLoadMoreClickListener(new RecyclerViewAdaptor1.OnLoadMoreClickListener() {
                        @Override
                        public void onLoadMoreClick(View view, int position) {

                        }
                    });
                }else {
                    adaptor1.setOnLoadMoreClickListener(new RecyclerViewAdaptor1.OnLoadMoreClickListener() {
                        @Override
                        public void onLoadMoreClick(View view, int position) {
                            req(req_type);
                        }
                    });
                }
                page_no =page_no + 1;
                adaptor1.notifyDataSetChanged();
            }

            @Override
            public void onRespError() {
                arrayList.clear();
                ArrayList<Map<String,String>> list = new ArrayList<>();
                Map map1 =new  HashMap<String,String>();
                map1.put("holder_type",String.valueOf(RecyclerViewAdaptor1.TYPE_ERROR));
                list.add(map1);
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
        },CompanyListActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        page_no = 1;
        isLoadEnd = false;
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
