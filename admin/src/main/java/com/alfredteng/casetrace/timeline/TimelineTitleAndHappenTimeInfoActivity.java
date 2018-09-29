package com.alfredteng.casetrace.timeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.ViewHandler;

import java.util.ArrayList;
import java.util.List;

public class TimelineTitleAndHappenTimeInfoActivity extends BaseActivity {

    private boolean isAdd = false;
    private long event_id = 0;
    private long timeline_id = 0;
    private String content;
    private Toolbar toolbar;
    private EditText et_title,et_date,et_time;
    private Spinner sp_event;
    private List<String> list_event = new ArrayList<>();
    private ArrayAdapter<String> adapter_company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timeline_add_title_and_happen_time);
        ViewHandler.initToolbarWithBackButton(this,toolbar,"编辑时间线信息");
        initViews();
        isAdd = getIntent().getBooleanExtra("isAdd",false);
        if (!isAdd) {
            et_title.setText(getIntent().getStringExtra("title"));
            et_date.setText(getIntent().getStringExtra("happen_time").split(" ")[0]);
            et_time.setText(getIntent().getStringExtra("happen_time").split(" ")[2]);
        }
    }

    private void initViews() {
        et_title = (EditText)findViewById(R.id.et_title_a_add_timeline_add_title_and_happen_time);
        et_date = (EditText)findViewById(R.id.et_happen_date_a_add_timeline_add_title_and_happen_time);
        et_time = (EditText)findViewById(R.id.et_happen_time_a_add_timeline_add_title_and_happen_time);
        sp_event = (Spinner)findViewById(R.id.sp_event_name_a_add_timeline_add_title_and_happen_time);
    }
}
