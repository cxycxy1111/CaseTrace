package com.alfredteng.casetrace.util;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.Tool;
import com.example.alfredtools.ViewHandler;

public class Test extends BaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private EditText et_unicode,et_zh;
    private Button btn_unicode,btn_zh;
    private TextView tv_unicode,tv_zh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initViews();
        ViewHandler.initToolbarWithBackButton(this,toolbar,"测试",R.id.toolbar_general);
    }

    private void initViews() {
        et_unicode = (EditText)findViewById(R.id.et_unicode);
        et_zh = (EditText)findViewById(R.id.et_zh);
        btn_unicode = (Button)findViewById(R.id.btn_unicode);
        btn_zh = (Button)findViewById(R.id.btn_zh);
        tv_unicode = (TextView) findViewById(R.id.tv_unicode);
        tv_zh = (TextView)findViewById(R.id.tv_zh);

        btn_unicode.setOnClickListener(this);
        btn_zh.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_unicode:
                String s = et_unicode.getText().toString();
                String res = transformToZh(s);
                tv_unicode.setText(res);
                break;
            case R.id.btn_zh:
                String s1 = et_unicode.getText().toString();
                transformToUnicode(s1);
                break;
            default:break;
        }
    }

    private String transformToZh(String s) {
        return Tool.transformUnicodeToStr(s);
    }

    private void transformToUnicode(String s) {

    }
}
