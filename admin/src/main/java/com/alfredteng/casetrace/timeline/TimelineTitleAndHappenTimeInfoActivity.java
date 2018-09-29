package com.alfredteng.casetrace.timeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.BaseHttpCallback;
import com.alfredteng.casetrace.util.BaseHttpResultListener;
import com.alfredteng.casetrace.util.JsonUtil;
import com.alfredteng.casetrace.util.NetUtil;
import com.alfredteng.casetrace.util.Tool;
import com.alfredteng.casetrace.util.ViewHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineTitleAndHappenTimeInfoActivity extends BaseActivity {

    private boolean isAdd = false;
    private long event_id = 0;
    private long timeline_id = 0;
    private String title = "";
    private String content;
    private String happen_time = "";
    private String[] keys_event = new String[]{"id","title"};
    private Toolbar toolbar;
    private List<String> list_event = new ArrayList<>();
    private ArrayList<Map<String,String>> arrayList_event = new ArrayList<>();
    private ArrayAdapter<String> adapter_event;
    private EditText et_title,et_date,et_time;
    private Spinner sp_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timeline_add_title_and_happen_time);
        ViewHandler.initToolbarWithBackButton(this,toolbar,"编辑时间线信息");
        initViews();
        isAdd = getIntent().getBooleanExtra("isAdd",false);
        if (!isAdd) {
            title = getIntent().getStringExtra("title");
            happen_time = getIntent().getStringExtra("happen_time");
            et_title.setText(title);
            et_date.setText(happen_time.split(" ")[0]);
            et_time.setText(happen_time.split(" ")[2]);
            event_id = getIntent().getLongExtra("event_id",0);
            timeline_id = getIntent().getLongExtra("timeline_id",0);
        }
        Map<String,String> map = new HashMap<>();
        map.put("id","0");
        map.put("title","暂无事件");
        arrayList_event.add(map);
        list_event.add("暂无事件");
        adapter_event = new ArrayAdapter<String>(this,R.layout.tile_sp_item,list_event);
        adapter_event.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event_id = Long.parseLong(String.valueOf(arrayList_event.get(position).get("id")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                event_id = Long.parseLong(String.valueOf(arrayList_event.get(0).get("id")));
            }
        });
    }

    private void initViews() {
        et_title = (EditText)findViewById(R.id.et_title_a_add_timeline_add_title_and_happen_time);
        et_date = (EditText)findViewById(R.id.et_happen_date_a_add_timeline_add_title_and_happen_time);
        et_time = (EditText)findViewById(R.id.et_happen_time_a_add_timeline_add_title_and_happen_time);
        sp_event = (Spinner)findViewById(R.id.sp_event_name_a_add_timeline_add_title_and_happen_time);
    }


    private void loadEvent() {
        String url = "";
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {

            }

            @Override
            public void onRespMapList(String body) throws IOException {
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,keys_event);
                if (arrayList_temp.size() != 0) {
                    ArrayList<String> list_temp = new ArrayList<>();
                    list_event.clear();
                    for (int i = 0;i < list_temp.size();i++) {
                        list_temp.add(arrayList_temp.get(i).get("title"));
                    }
                    list_event.addAll(list_temp);
                    arrayList_event.clear();
                    arrayList_event.addAll(arrayList_temp);
                    adapter_event.notifyDataSetChanged();
                    if (isAdd) {
                        sp_event.setSelection(0);
                        event_id = Long.parseLong(String.valueOf(arrayList_event.get(0).get("id")));
                    }else {
                        //不是新增的话，循环比对找出id相等的公司
                        for (int i = 0;i < arrayList_event.size();i++) {
                            if (event_id == Long.parseLong(String.valueOf(arrayList_event.get(i).get("id")))) {
                                sp_event.setSelection(i);
                            }
                        }
                    }
                }
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(TimelineTitleAndHappenTimeInfoActivity.this,NetUtil.UNKNOWN_ERROR+"，无法初始化公司列表");
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(TimelineTitleAndHappenTimeInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET + "，无法初始化公司列表");
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(TimelineTitleAndHappenTimeInfoActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,url,callback);
    }
}
