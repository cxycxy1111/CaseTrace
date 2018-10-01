package com.alfredteng.casetrace.timeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

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
import java.util.Map;

public class TimeLineDetailActivity extends BaseActivity {

    private Toolbar toolbar;
    private WebView webView;
    private static final String[] keys = new String[]{"id","title","happen_time","content","creator","creator_type","nick_name","icon","event_id","event_title","event_happen_time"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_detail);
        initViews();
        String title = getIntent().getStringExtra("title");
        ViewHandler.initToolbarWithBackButton(this,toolbar,title);
        init();
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        webView = (WebView)findViewById(R.id.wv_a_timeline_detail);
    }

    private void init() {
        String url = "/admin/timeline/qry/detail?id=" + getIntent().getLongExtra("id",0);
        BaseHttpCallback callback = new BaseHttpCallback(new BaseHttpResultListener() {
            @Override
            public void onRespStatus(String body) {

            }

            @Override
            public void onRespMapList(String body) throws IOException {
                ArrayList<Map<String,String>> mapArrayList_content = new ArrayList<>();
                mapArrayList_content = JsonUtil.strToListMap(body,keys);
                webView.loadData(mapArrayList_content.get(0).get("content"),"text/html","utf-8");
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(TimeLineDetailActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(TimeLineDetailActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(TimeLineDetailActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }
}
