package com.alfredteng.casetrace.cases;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.Tool;
import com.example.alfredtools.ViewHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseEditExtInfoActivity extends BaseActivity implements HttpResultListener,Spinner.OnItemSelectedListener {

    private boolean isAdd = false;
    private static final int SOURCE_EVENT_LOAD = 1;
    private static final int SOURCE_CASE_SUBMMIT = 2;
    private long event_id = 0;
    private long case_id = 0;
    private int selected_pos = 0;
    private int status = 0;
    private boolean del = false;
    private String content = "";
    private String[] keys = new String[]{"id","title"};
    private Toolbar toolbar;
    private Spinner sp_event;
    private TextView tv_tips;
    private ArrayAdapter<String> adapter;
    private List<String> arryList_event = new ArrayList<>();
    private ArrayList<String> arrayList_intent = new ArrayList<>();
    private ArrayList<Map<String,String>> mapArrayList_event = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_extra_info_edit);
        initViews();

        isAdd = getIntent().getBooleanExtra("is_add",false);

        ViewHandler.initToolbarWithBackButton(this,toolbar,"选择事件",R.id.toolbar_general);

        if (isAdd) {
            content = getIntent().getStringExtra("content");
        }else {
            arrayList_intent = getIntent().getStringArrayListExtra("data");
            case_id = Long.parseLong(arrayList_intent.get(0));
            status = Integer.parseInt(arrayList_intent.get(1));
            del = Boolean.parseBoolean(arrayList_intent.get(2));
            event_id = Long.parseLong(arrayList_intent.get(3));
            content = arrayList_intent.get(4);
        }
        NetUtil.reqSendGet(this,
                "/admin/event/qry/ignoreStatus",
                new HttpCallback(this,this,SOURCE_EVENT_LOAD));
    }

    private void initViews() {
        sp_event = (Spinner)findViewById(R.id.sp_name_a_case_ext_info_edit);
        tv_tips = (TextView)findViewById(R.id.tv_tips);

        arryList_event.add("暂无事件");
        Map<String,String> map = new HashMap<>();
        map.put("id","0");
        map.put("title","暂无事件");
        mapArrayList_event.add(map);
        adapter = new ArrayAdapter<String>(this,R.layout.tile_sp_item,arryList_event);
        sp_event.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_event.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,101,1,R.string.option_menu_submit);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 101:
                checkBeforeSubmit();
                break;
            default:break;
        }
        return true;
    }

    private void checkBeforeSubmit() {
        if (event_id == 0) {
            if (mapArrayList_event.size()>0) {
                Object o = mapArrayList_event.get(0).get("id");
                String s = String.valueOf(o);
                event_id = Long.parseLong(s);
            }
        }
        if (event_id != 0) {
            submit();
        }else {
            ViewHandler.toastShow(this,"暂未选择事件，请选择");
        }
    }

    private void submit() {
        String url = "";
        HashMap<String,Object> map = new HashMap<>();
        if (isAdd) {
            url = "/admin/case/add";
        }else {
            url = "/admin/case/update";
            map.put("id",String.valueOf(case_id));
        }
        event_id = Long.valueOf(String.valueOf(mapArrayList_event.get(selected_pos).get("id")));
        String tilte = "";
        if (content.length() <= 20) {
            int length = content.length();
            tilte = content.substring(0,length-1);
        }else {
            tilte = content.substring(0,20);
        }
        map.put("event",String.valueOf(event_id));
        map.put("title",tilte);
        map.put("content",content);
        NetUtil.reqSendPost(this,url,map,new HttpCallback(this,this,SOURCE_CASE_SUBMMIT));
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == SOURCE_CASE_SUBMMIT) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case FAIL:
                    tv_tips.setText(OPERATE_FAIL);
                    break;
                case SUCCESS:
                    if (isAdd) {
                        clearDraftBeforeQuit();
                    }
                    setResult(1);
                    this.finish();
                    tv_tips.setText(OPERATE_SUCCESS);
                    break;
                case DUPLICATE:
                    break;
                default:break;
            }
        }else if (source == SOURCE_EVENT_LOAD) {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == SOURCE_CASE_SUBMMIT) {

        }else if (source == SOURCE_EVENT_LOAD) {
            ArrayList<Map<String,String>> mapArrayList_temp = new ArrayList<>();
            ArrayList<String> arrayList_temp = new ArrayList<>();
            try {
                mapArrayList_temp = JsonUtil.strToListMap(body,keys);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mapArrayList_temp.size() > 0) {
                arryList_event.clear();
                mapArrayList_event.clear();
                adapter.notifyDataSetChanged();
                for (int i = 0;i < mapArrayList_temp.size();i++) {
                    arrayList_temp.add(mapArrayList_temp.get(i).get("title"));
                }
                arryList_event.addAll(arrayList_temp);
                mapArrayList_event.addAll(mapArrayList_temp);
                adapter.notifyDataSetChanged();
                if (!isAdd) {
                    for (int i = 0;i < mapArrayList_temp.size();i++) {
                        Object o = mapArrayList_temp.get(i).get("id");
                        String s = String.valueOf(o);
                        long l = Long.parseLong(s);
                        if (l == event_id) {
                            selected_pos = i;
                            sp_event.setSelection(i);
                        }
                    }
                }else {
                    sp_event.setSelection(0);
                    selected_pos = 0;
                    Object o = mapArrayList_event.get(0).get("id");
                    String s = String.valueOf(o);
                    event_id = Long.parseLong(s);
                }
            }
        }
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object,source);
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_pos = position;
        Object o = mapArrayList_event.get(position).get("id");
        String s = String.valueOf(o);
        event_id = Long.parseLong(s);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selected_pos = 0;
    }

    private void clearDraftBeforeQuit() {
        SharedPreferences sharedPreferences = getSharedPreferences("content_draft",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
    }

}
