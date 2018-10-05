package com.alfredteng.casetrace.timeline;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.ViewHandler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineEditExtInfoActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private boolean isAdd = false;
    private int current_year,current_month,current_date,current_hour,current_minute,current_second;
    private long event_id = 0;
    private long timeline_id = 0;
    private String content = "";
    private String[] keys_event = new String[]{"id","title"};
    private static final String TAG = "TLTitleAndHappenTimeInfo";
    private Toolbar toolbar;
    private TextView tv_tips;
    private List<String> list_event = new ArrayList<>();
    private ArrayList<Map<String,String>> arrayList_event = new ArrayList<>();
    private ArrayAdapter<String> adapter_event;
    private EditText et_title,et_date,et_time;
    private Spinner sp_event;
    private DatePickerDialog dpd_happen_date;
    private TimePickerDialog tpd_happen_time;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_add_title_and_happen_time);
        ViewHandler.initToolbarWithBackButton(this,toolbar,"编辑时间线信息",R.id.toolbar_general);
        initViews();
        isAdd = getIntent().getBooleanExtra("isAdd",false);
        content = getIntent().getStringExtra("content");
        if (!isAdd) {
            event_id = getIntent().getLongExtra("event_id",0);
            Log.d(TAG, "onCreate: event_id: " + event_id);
            timeline_id = getIntent().getLongExtra("timeline_id",0);
            et_title.setText(getIntent().getStringExtra("title"));
            et_date.setText(getIntent().getStringExtra("happen_time").split(" ")[0]);
            et_time.setText(getIntent().getStringExtra("happen_time").split(" ")[1].substring(0,8));
        }
        if (isAdd) {
            initCurrentDateIfAdd();
        }else {
            try {
                initCurrentDateIfEdit(getIntent().getStringExtra("happen_time"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        initDatePicker();
        initTImePicker();
        Map<String,String> map = new HashMap<>();
        map.put("id","0");
        map.put("title","暂无事件");
        arrayList_event.add(map);
        list_event.add("暂无事件");
        adapter_event = new ArrayAdapter<String>(this,R.layout.tile_sp_item,list_event);
        adapter_event.setDropDownViewResource(R.layout.tile_sp_dropdown_item);
        sp_event.setAdapter(adapter_event);
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
        loadEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,101,1,"保存");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 101:
                checkBeforeCommit();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_happen_date_a_add_timeline_add_title_and_happen_time:
                dpd_happen_date.show();
                break;
            case R.id.et_happen_time_a_add_timeline_add_title_and_happen_time:
                tpd_happen_time.show();
                break;
            default:break;
        }
    }

    private void initViews() {
        et_title = (EditText)findViewById(R.id.et_title_a_add_timeline_add_title_and_happen_time);
        et_date = (EditText)findViewById(R.id.et_happen_date_a_add_timeline_add_title_and_happen_time);
        et_time = (EditText)findViewById(R.id.et_happen_time_a_add_timeline_add_title_and_happen_time);
        sp_event = (Spinner)findViewById(R.id.sp_event_name_a_add_timeline_add_title_and_happen_time);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        et_date.setInputType(InputType.TYPE_NULL);
        et_time.setInputType(InputType.TYPE_NULL);
        et_date.setOnClickListener(this);
        et_time.setOnClickListener(this);
        et_date.setOnFocusChangeListener(this);
        et_time.setOnFocusChangeListener(this);
        et_title.addTextChangedListener(textWatcher);
        et_time.addTextChangedListener(textWatcher);
        et_date.addTextChangedListener(textWatcher);
    }

    private void loadEvent() {
        String url = "/admin/event/qry/ignoreStatus";
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body,int source) {
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onRespMapList(String body,int source) throws IOException {
                ArrayList<Map<String,String>> arrayList_temp = new ArrayList<>();
                arrayList_temp = JsonUtil.strToListMap(body,keys_event);
                if (arrayList_temp.size() != 0) {
                    ArrayList<String> list_temp = new ArrayList<>();
                    list_event.clear();//清空原事件列表
                    for (int i = 0;i < arrayList_temp.size();i++) {
                        list_temp.add(arrayList_temp.get(i).get("title"));
                    }
                    list_event.addAll(list_temp);
                    arrayList_event.clear();//清空事件列表
                    arrayList_event.addAll(arrayList_temp);
                    adapter_event.notifyDataSetChanged();
                    if (isAdd) {//新增的话，定位到0
                        sp_event.setSelection(0);
                        event_id = Long.parseLong(String.valueOf(arrayList_temp.get(0).get("id")));
                    }else {
                        //不是新增的话，循环比对找出id相等的公司
                        for (int i = 0;i < arrayList_temp.size();i++) {
                            Map<String,String> map = arrayList_temp.get(i);
                            Object o = map.get("id");
                            String s = String.valueOf(o);
                            Long l = Long.parseLong(s);
                            if (event_id == l) {
                                 sp_event.setSelection(i);
                                 event_id = l;
                            }
                        }
                    }
                }
            }

            @Override
            public void onRespError(int source) {
                ViewHandler.toastShow(TimelineEditExtInfoActivity.this,NetUtil.UNKNOWN_ERROR+"，无法初始化公司列表");
            }

            @Override
            public void onReqFailure(Object object,int source) {
                ViewHandler.toastShow(TimelineEditExtInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET + "，无法初始化公司列表");
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(TimelineEditExtInfoActivity.this);
            }
        },this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    @SuppressLint("LongLogTag")
    public void checkBeforeCommit() {
        if (et_title.getText().toString().equals("")) {
            tv_tips.setText("标题不能为空，请补充。");
            return;
        }
        if (et_date.getText().toString().equals("") | et_time.getText().toString().equals("")) {
            tv_tips.setText("日期或事件不能为空，请补充");
            return;
        }
        Log.d(TAG, "checkBeforeCommit: event_id=" + event_id);
        if (event_id == 0) {
            tv_tips.setText("请先添加事件");
            return;
        }
        submit();
    }

    public void submit() {
        String url = "";
        HashMap<String,Object> map = new HashMap<>();
        if (isAdd) {
            String title = et_title.getText().toString();
            String happen_time = et_date.getText().toString() + " " + et_time.getText().toString() + ":00";
            url = "/admin/timeline/add";
            map.put("event",String.valueOf(event_id));
            map.put("title",title);
            map.put("happen_time",happen_time);
            map.put("content",content);

        }else {
            String title = et_title.getText().toString();
            String time = et_time.getText().toString();
            if (time.length() < 8) {
                time = time + ":00";
            }
            String happen_time = et_date.getText().toString() + " " + time;
            url = "/admin/timeline/edit";
            map.put("event",String.valueOf(event_id));
            map.put("title",title);
            map.put("happen_time",happen_time);
            map.put("content",content);
            map.put("id",timeline_id);
        }
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body,int source) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        setResult(1);
                        if (isAdd) {
                            SharedPreferences sharedPreferences = getSharedPreferences("timeline_draft",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear().apply();
                        }
                        TimelineEditExtInfoActivity.this.finish();
                        break;
                    case FAIL:
                        ViewHandler.toastShow(TimelineEditExtInfoActivity.this,BaseActivity.OPERATE_FAIL);
                        break;
                    case DUPLICATE:
                        ViewHandler.toastShow(TimelineEditExtInfoActivity.this,NetUtil.STATUS_DUPLICATE);
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body,int source) throws IOException {
            }

            @Override
            public void onRespError(int source) {
                ViewHandler.toastShow(TimelineEditExtInfoActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object,int source) {
                ViewHandler.toastShow(TimelineEditExtInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(TimelineEditExtInfoActivity.this);
            }
        },this,1);
        NetUtil.reqSendPost(this,url,map,callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_happen_date_a_add_timeline_add_title_and_happen_time:
                dpd_happen_date.show();
                break;
            case R.id.et_happen_time_a_add_timeline_add_title_and_happen_time:
                tpd_happen_time.show();
                break;
            default:break;
        }
    }
    private void initCurrentDateIfAdd() {
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);
        current_hour = calendar.get(Calendar.HOUR_OF_DAY);
        current_minute = calendar.get(Calendar.MINUTE);
        current_second = calendar.get(Calendar.SECOND);
    }

    private void initCurrentDateIfEdit(String happen_time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        date = simpleDateFormat.parse(happen_time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_date = calendar.get(Calendar.DAY_OF_MONTH);
        current_hour = calendar.get(Calendar.HOUR_OF_DAY);
        current_minute = calendar.get(Calendar.MINUTE);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                current_year = year;current_month = month;current_month = dayOfMonth;
                et_date.setText(year + "-" + String.valueOf(month+1) + "-" + dayOfMonth);
            }
        };
        dpd_happen_date = new DatePickerDialog(this,listener,current_year,current_month,current_date);
    }

    private void initTImePicker() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                current_hour = hourOfDay;current_minute = minute;
                et_time.setText(hourOfDay + ":" + minute);
            }
        };
        tpd_happen_time = new TimePickerDialog(this,listener,current_hour,current_minute,true);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
