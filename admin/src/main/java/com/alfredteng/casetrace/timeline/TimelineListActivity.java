package com.alfredteng.casetrace.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class TimelineListActivity extends BaseActivity implements HttpResultListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();
    private GeneralRecyclerViewAdaptor adaptor1;
    private boolean isLoadEnd = false;
    private int entity_type = 0;
    private int req_type = 0;
    private int page_no = 1;
    private static final int SOURCE_LOAD_LIST = 1;
    private String url = "";
    private String str_body_key = "";
    private static final String TAG = "RecyclerView";
    private String[] str_key_timeline = new String[]{"id","title","happen_time","creator",
            "creator_type","nick_name","icon"};

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
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/");
        builder.append("timeline/qry/");
        switch (req_type) {
            case PASSED:
                builder.append("normal?page_no=").append(page_no);
                break;
            case UNCHECKED:
                builder.append("unchecked?page_no=").append(page_no);
                break;
            case REJECTED:
                builder.append("rejected?page_no=").append(page_no);
                break;
            case DELETED:
                builder.append("deleted?page_no=").append(page_no);
                break;
        }
        url = builder.toString();

        Map<String,String> map = new HashMap<>();
        map.put("holder_type","-1");
        arrayList.add(map);
        recyclerView = (RecyclerView)findViewById(R.id.rv_a_rv_list);
        adaptor1 = new GeneralRecyclerViewAdaptor(arrayList,TimelineListActivity.this,str_body_key);
        adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(TimelineListActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adaptor1);

        NetUtil.reqSendGet(this,url,new HttpCallback(this,this,SOURCE_LOAD_LIST));
    }

    private void initToolbar(int entity_type,int req_type) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arrayList.clear();
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        adaptor1.setOnItemClickListener(null);
        page_no = 1;
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

    @Override
    public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                arrayList.clear();
                if (arrayList.size() == 0){
                    Map<String,String> map = new HashMap<>();
                    map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_EMPTY));
                    arrayList.add(map);
                }
                adaptor1.notifyDataSetChanged();
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
        if (source == SOURCE_LOAD_LIST) {
            if (page_no == 1) {
                arrayList.clear();
            }
            ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
            arrayList_temp = JsonUtil.strToListMap(body,str_key_timeline);
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
            adaptor1.notifyDataSetChanged();
            adaptor1.setStr_body_key(str_body_key);
            adaptor1.setOnItemClickListener(new GeneralRecyclerViewAdaptor.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, "onItemClick: before intent create");
                    Intent intent = new Intent(TimelineListActivity.this,TimelineViewActivity.class);
                    Log.d(TAG, "onItemClick: after intent created");
                    intent.putExtra("title",arrayList.get(position).get("title"));
                    intent.putExtra("id",Long.parseLong(String.valueOf(arrayList.get(position).get("id"))));
                    intent.putExtra("status",Integer.parseInt(String.valueOf(arrayList.get(position).get("status"))));
                    switch (req_type) {
                        case DELETED:
                            intent.putExtra("del",true);
                            break;
                        default:
                            intent.putExtra("del",false);
                            break;
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
            page_no =page_no + 1;
            adaptor1.notifyDataSetChanged();
        }
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
        arrayList.clear();
        Map<String,String> map = new HashMap<>();
        map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_ERROR));
        ArrayList<Map<String,String>> list = new ArrayList<>();
        list.add(map);
        arrayList.addAll(list);
        adaptor1.notifyDataSetChanged();
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
        arrayList.clear();
        Map<String,String> map = new HashMap<>();
        map.put("holder_type",String.valueOf(GeneralRecyclerViewAdaptor.TYPE_NET_ERROR));
        ArrayList<Map<String,String>> list = new ArrayList<>();
        list.add(map);
        arrayList.addAll(list);
        adaptor1.notifyDataSetChanged();
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }
}
