package com.alfredteng.casetrace.event;

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
import com.example.alfredtools.ViewHandler;
import com.alfredteng.casetrace.util.adaptor.GeneralRecyclerViewAdaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private GeneralRecyclerViewAdaptor adaptor1;
    private int entity_type = 0;
    private int req_type = 0;
    private int page_no=1;
    private boolean isLoadEnd = false;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_event = new String[]{"id","title","del","status","happen_time",
            "subcribe_count","creator_type","creator"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_list);
        entity_type = getIntent().getIntExtra("entity_type",0);
        req_type = getIntent().getIntExtra("req_type",0);
        initToolbar(entity_type,req_type);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_rv_list);
        req(req_type);
    }

    private void req(final int req_type) {
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/").append("event/qry/");
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
        url = builder.toString();
        if (page_no == 1) {
            Map<String,String> map = new HashMap<>();
            map.put("holder_type","-1");
            arrayList.add(map);
        }
        adaptor1 = new GeneralRecyclerViewAdaptor(arrayList,EventListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(EventListActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adaptor1);
        HttpCallback callback = new HttpCallback(new HttpResultListener() {

            @Override
            public void onRespStatus(String body,int source) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case EMPTY:
                        arrayList.clear();
                        if (arrayList.size() == 0){
                            Map<String,String> map = new HashMap<>();
                            map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_EMPTY));
                            arrayList.add(map);
                        }
                        adaptor1.notifyDataSetChanged();
                }
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(EventListActivity.this);
            }

            @Override
            public void onRespMapList(String body,int source) throws IOException {
                if (page_no == 1) {
                    arrayList.clear();
                }
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,str_key_event);
                str_body_key = "title";
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
                if (arrayList.size()>=BaseActivity.LOAD_NUM) {
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_LOAD_MORE));
                    arrayList_temp.add(map1);
                }else {
                    if (page_no !=1) {
                        arrayList.remove(arrayList.size()-1);
                    }
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_END));
                    arrayList_temp.add(map1);
                    isLoadEnd = true;
                }
                arrayList.addAll(arrayList_temp);
                adaptor1.notifyDataSetChanged();
                adaptor1.setStr_body_key(str_body_key);
                adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(EventListActivity.this,EventInfoActivity.class);
                        intent.putExtra("is_add",false);
                        intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                        intent.putExtra("happen_time",String.valueOf(arrayList.get(position).get("happen_time")));
                        intent.putExtra("req_type",req_type);
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
                page_no = page_no+1;
                adaptor1.notifyDataSetChanged();
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
        },EventListActivity.this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void initToolbar(int entity_type,int req_type) {
        switch (req_type) {
            case PASSED:
                ViewHandler.initToolbarWithBackButton(this, toolbar, R.string.toolbar_tilte_event_passed,R.id.toolbar_general);
                break;
            case UNCHECKED:
                ViewHandler.initToolbarWithBackButton(this, toolbar, R.string.toolbar_tilte_event_unchecked,R.id.toolbar_general);
                break;
            case REJECTED:
                ViewHandler.initToolbarWithBackButton(this, toolbar, R.string.toolbar_tilte_event_rejected,R.id.toolbar_general);
                break;
            case DELETED:
                ViewHandler.initToolbarWithBackButton(this, toolbar, R.string.toolbar_tilte_event_deleted,R.id.toolbar_general);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        page_no = 1;
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
