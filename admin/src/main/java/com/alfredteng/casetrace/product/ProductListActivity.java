package com.alfredteng.casetrace.product;

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

public class ProductListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private RecyclerViewAdaptor1 adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_product = new String[]{"id","name","company_id","company_name",
            "status","del","nick_name","icon","creator","creator_type","create_time",
            "update_time"};


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
        adaptor1 = new RecyclerViewAdaptor1(arrayList,ProductListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductListActivity.this,LinearLayoutManager.VERTICAL,false));
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
                }
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(ProductListActivity.this);
            }

            @Override
            public void onRespMapList(String body) throws IOException {
                arrayList.clear();
                adaptor1.notifyDataSetChanged();
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,str_key_product);
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
                arrayList.addAll(arrayList_temp);
                adaptor1.notifyDataSetChanged();
                adaptor1.setStr_body_key(str_body_key);
                adaptor1.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }
                });
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
        },ProductListActivity.this);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/");
        builder.append("product/qry/");
        switch (req_type) {
            case PASSED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_passed);
                builder.append("normal?page_no=1");
                break;
            case UNCHECKED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_unchecked);
                builder.append("unchecked?page_no=1");
                break;
            case REJECTED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_rejected);
                builder.append("rejected?page_no=1");
                break;
            case DELETED:
                ViewHandler.initToolbar(this, toolbar, R.string.toolbar_tilte_product_deleted);
                builder.append("deleted?page_no=1");
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
