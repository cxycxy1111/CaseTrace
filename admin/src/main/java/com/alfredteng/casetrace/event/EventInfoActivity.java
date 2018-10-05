package com.alfredteng.casetrace.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alfredteng.casetrace.MainActivity;
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
import java.util.Map;

public class EventInfoActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private Toolbar toolbar;
    private EditText et_name,et_happen_date,et_happen_time;
    private TextView tv_del,tv_status,tv_tips;
    private RelativeLayout rl_del,rl_status;
    private DatePickerDialog dpd_happen_date;
    private TimePickerDialog tpd_happen_time;
    private int current_year,current_month,current_date,current_hour,current_minute,current_second;
    private int selected_year,selected_month,selected_date,selected_hour,selected_minute;
    private boolean isAdd = false;
    private long id = 0;
    private int req_type;
    private static final String TAG = "EventInfoActivity";
    private static final String TIPS_EMPTY = "事件名称不允许为空，请输入";
    private static final String TIPS_RESP_ERROR = "未知错误";
    private static final String TIPS_DUPLICATE = "事件名称重复";
    private static final String TIPS_FAIL = "新增失败，请重试";
    private String[] keys = new String[]{"id","title","del","status","happen_time","subcribe_count",
            "creator_type","creator","nick_name","icon"};
    private View[] views_hide_when_add;
    private View[] views_hide_wehn_edit;
    private ArrayList<Map<String,String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        initViews();
        isAdd = getIntent().getBooleanExtra("is_add",false);
        et_happen_date.setInputType(InputType.TYPE_NULL);
        et_happen_date.setOnClickListener(this);
        et_happen_date.setOnFocusChangeListener(this);
        et_happen_time.setInputType(InputType.TYPE_NULL);
        et_happen_time.setOnFocusChangeListener(this);
        et_happen_time.setOnClickListener(this);
        if (isAdd) {
            initCurrentDateIfAdd();
            ViewHandler.initToolbarWithBackButton(this,toolbar,R.string.toolbar_tilte_event_add,R.id.toolbar_general);
            views_hide_when_add = new View[]{rl_del,rl_status};
            ViewHandler.viewHide(views_hide_when_add);
        }else{
            String str_happen_time = getIntent().getStringExtra("happen_time");
            try {
                initCurrentDateIfEdit(str_happen_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            et_happen_date.setText(String.valueOf(current_year) + "-" + String.valueOf(current_month + 1) + "-" + String.valueOf(current_date));
            et_happen_time.setText(String.valueOf(current_hour) + ":" + String.valueOf(current_minute));
            ViewHandler.initToolbarWithBackButton(this,toolbar,R.string.toolbar_tilte_event_edit,R.id.toolbar_general);
        }
        initDatePicker();
        initTImePicker();
        et_name.addTextChangedListener(watcher);
        if (!isAdd) {
            id = getIntent().getLongExtra("id",0);
            req_type = getIntent().getIntExtra("req_type",0);
            load();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(1,101,1,"保存");
        if (!isAdd) {
            switch (req_type) {
                case PASSED:
                    menu.add(1,102,2,"删除");
                    break;
                case UNCHECKED:
                    menu.add(1,103,2,"拒绝");
                    menu.add(1,104,3,"通过");
                    menu.add(1,102,2,"删除");
                    break;
                case REJECTED:
                    menu.add(1,104,3,"通过");
                    menu.add(1,102,2,"删除");
                    break;
                case DELETED:
                    menu.add(1,105,2,"恢复");
                    break;
                default:break;
            }
        }
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 101:
                save();
                break;
            case 102:
                operate(OP_DELETE);
                break;
            case 103:
                operate(OP_REJECT);
                break;
            case 104:
                operate(OP_PASS);
                break;
            case 105:
                operate(OP_RECOVER);
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_happen_date_a_event_info:
                dpd_happen_date.show();
                break;
            case R.id.et_happen_time_a_event_info:
                tpd_happen_time.show();
                break;
            default:break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_happen_date_a_event_info:
                if (hasFocus) {
                    dpd_happen_date.show();
                }
                break;
            case R.id.et_happen_time_a_event_info:
                if (hasFocus) {
                    tpd_happen_time.show();
                }
                break;
            default:break;
        }
    }

    private void initViews() {
        et_name = (EditText)findViewById(R.id.et_name_a_event_info);
        et_happen_date = (EditText)findViewById(R.id.et_happen_date_a_event_info);
        et_happen_time = (EditText)findViewById(R.id.et_happen_time_a_event_info);
        tv_tips = (TextView)findViewById(R.id.tv_tips);
        rl_del = (RelativeLayout)findViewById(R.id.rl_del_name_a_event_info);
        rl_status = (RelativeLayout)findViewById(R.id.rl_status_name_a_event_info);
        tv_del = (TextView)findViewById(R.id.tv_del_main_name_a_event_info);
        tv_status = (TextView)findViewById(R.id.tv_status_main_name_a_event_info);
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
                et_happen_date.setText(year + "-" + String.valueOf(month+1) + "-" + dayOfMonth);
            }
        };
        dpd_happen_date = new DatePickerDialog(this,listener,current_year,current_month,current_date);
    }

    private void initTImePicker() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                current_hour = hourOfDay;current_minute = minute;
                et_happen_time.setText(hourOfDay + ":" + minute);
            }
        };
        tpd_happen_time = new TimePickerDialog(this,listener,current_hour,current_minute,true);
    }

    private void load() {
        String url = "/admin/event/qry/detail?id=" + id;
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body,int source) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case STATUS_SESSION_EXPIRED:
                        ViewHandler.alertShowAndExitApp(EventInfoActivity.this);
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(EventInfoActivity.this);
            }

            @Override
            public void onRespMapList(String body,int source) throws IOException{
                ArrayList<Map<String,String>> arrayList = new ArrayList<>();
                arrayList = JsonUtil.strToListMap(body,keys);
                et_name.setText(arrayList.get(0).get("name"));
                switch (Integer.parseInt(String.valueOf(arrayList.get(0).get("status")))){
                    case PASSED:
                        tv_status.setText("已通过");
                        break;
                    case UNCHECKED:
                        tv_status.setText("待审核");
                        break;
                    case REJECTED:
                        tv_status.setText("已拒绝");
                        break;
                }
                boolean b = Boolean.parseBoolean(String.valueOf(arrayList.get(0).get("del")));
                if (b) {
                    tv_del.setText("已删除");
                }else {
                    tv_del.setText("未删除");
                }
            }

            @Override
            public void onRespError(int source) {
                Toast.makeText(EventInfoActivity.this,NetUtil.UNKNOWN_ERROR,Toast.LENGTH_SHORT).show();
                EventInfoActivity.this.finish();
            }

            @Override
            public void onReqFailure(Object object,int source) {
                Toast.makeText(EventInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET,Toast.LENGTH_SHORT).show();
                EventInfoActivity.this.finish();
            }
        },EventInfoActivity.this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    private void save() {
        String name = et_name.getText().toString();
        String str_happen_date = et_happen_date.getText().toString();
        String str_happen_time = et_happen_time.getText().toString();
        String happen_time = str_happen_date + " " + str_happen_time;
        if (name.equals("")| str_happen_date.equals("") | str_happen_time.equals("")) {
            tv_tips.setText(TIPS_EMPTY);
        }else {
            String url = "";
            if (isAdd) {
                url = "/admin/event/add?title=" + name + "&happen_time=" + happen_time;
            }else {
                if (id != 0) {
                    url = "/admin/event/edit?title=" + name + "&id=" + id + "&happen_time=" + happen_time;
                }
            }
            HttpCallback callback = new HttpCallback(new HttpResultListener() {

                @Override
                public void onRespStatus(String body,int source) {
                    Log.d(TAG, "onRespStatus: ");
                    if (isAdd) {
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this, BaseActivity.OPERATE_ADD_SUCCESS);
                                EventInfoActivity.this.finish();
                                break;
                            case DUPLICATE:
                                tv_tips.setText(TIPS_DUPLICATE);
                                break;
                            case FAIL:
                                tv_tips.setText(BaseActivity.OPERATE_ADD_FAIL);
                                break;
                            case STATUS_SESSION_EXPIRED:
                                ViewHandler.alertShowAndExitApp(EventInfoActivity.this);
                                break;
                            default:break;
                        }
                    }else {
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this, BaseActivity.OPERATE_MODIFY_SUCCESS);
                                EventInfoActivity.this.finish();
                                break;
                            case DUPLICATE:
                                tv_tips.setText(TIPS_DUPLICATE);
                                break;
                            case FAIL:
                                tv_tips.setText(OPERATE_MODIFY_FAIL);
                                break;
                            default:break;
                        }
                    }
                }

                @Override
                public void onRespMapList(String body,int source) {
                    Log.d(TAG, "onRespMapList: ");
                }

                @Override
                public void onRespError(int source) {
                    tv_tips.setText(TIPS_RESP_ERROR);
                }

                @Override
                public void onRespSessionExpired(int source) {
                    ViewHandler.alertShowAndExitApp(EventInfoActivity.this);
                }

                @Override
                public void onReqFailure(Object object,int source) {
                    tv_tips.setText(NetUtil.CANT_CONNECT_INTERNET);
                }
            },EventInfoActivity.this,1);
            NetUtil.reqSendGet(this,url,callback);
        }
    }

    private void operate(final int operate_type) {
        String url = "";
        switch (operate_type) {
            case OP_DELETE:
                url = "/admin/event/delete?id=" + id;
                break;
            case OP_PASS:
                url = "/admin/event/pass?id=" + id;
                break;
            case OP_RECOVER:
                url = "/admin/event/recover?id=" + id;
                break;
            case OP_REJECT:
                url = "/admin/event/reject?id=" + id;
                break;
        }

        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body,int source) {
                if (NetRespStatType.dealWithRespStat(body).equals(NetRespStatType.STATUS_SESSION_EXPIRED)) {
                    ViewHandler.alertShowAndExitApp(EventInfoActivity.this);
                    return;
                }
                switch (operate_type) {
                    case OP_DELETE:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(EventInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this,"已删除");
                                setResult(1);
                                EventInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_PASS:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(EventInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this,"已通过");
                                setResult(1);
                                EventInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_RECOVER:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(EventInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this,"已恢复");
                                setResult(1);
                                EventInfoActivity.this.finish();
                                break;
                        }
                        break;
                    case OP_REJECT:
                        switch (NetRespStatType.dealWithRespStat(body)) {
                            case FAIL:
                                ViewHandler.toastShow(EventInfoActivity.this,"请求失败");
                                break;
                            case SUCCESS:
                                ViewHandler.toastShow(EventInfoActivity.this,"已拒绝");
                                setResult(1);
                                EventInfoActivity.this.finish();
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onRespSessionExpired(int source) {

            }

            @Override
            public void onRespMapList(String body,int source) {

            }

            @Override
            public void onRespError(int source) {
                Toast.makeText(EventInfoActivity.this,NetUtil.UNKNOWN_ERROR,Toast.LENGTH_SHORT).show();
                EventInfoActivity.this.finish();
            }

            @Override
            public void onReqFailure(Object object,int source) {
                Toast.makeText(EventInfoActivity.this,NetUtil.CANT_CONNECT_INTERNET,Toast.LENGTH_SHORT).show();
                EventInfoActivity.this.finish();
            }
        },EventInfoActivity.this,1);
        NetUtil.reqSendGet(this,url,callback);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tv_tips.setText("");
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
