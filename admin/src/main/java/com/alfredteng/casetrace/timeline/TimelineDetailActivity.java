package com.alfredteng.casetrace.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.HttpCallback;
import com.example.alfredtools.HttpResultListener;
import com.example.alfredtools.JsonUtil;
import com.example.alfredtools.NetRespStatType;
import com.example.alfredtools.NetUtil;
import com.example.alfredtools.ViewHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TimelineDetailActivity extends BaseActivity {


    private boolean del = false;
    private int status;
    private long id = 0;
    private Toolbar toolbar;
    private WebView webView;
    private ArrayList<Map<String,String>> mapArrayList_content = new ArrayList<>();
    private static final String[] keys = new String[]{"id","title","happen_time","content","creator","creator_type","nick_name","icon","event_id","event_title","event_happen_time"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_detail);
        initViews();
        String title = getIntent().getStringExtra("title");
        del = getIntent().getBooleanExtra("del",false);
        status = getIntent().getIntExtra("status",0);
        id = getIntent().getLongExtra("id",0);
        ViewHandler.initToolbarWithBackButton(this,toolbar,title,R.id.toolbar_general);
        init();
    }

    private void initViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_general);
        webView = (WebView)findViewById(R.id.wv_a_timeline_detail);
    }

    private void init() {
        String url = "/admin/timeline/qry/detail?id=" + getIntent().getLongExtra("id",0);
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body) {

            }

            @Override
            public void onRespMapList(String body) throws IOException {
                mapArrayList_content = JsonUtil.strToListMap(body,keys);
                webView.loadData(mapArrayList_content.get(0).get("content"),"text/html","utf-8");
            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(TimelineDetailActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(TimelineDetailActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(TimelineDetailActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,url,callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,101,1,"修改");
        if (del) {
            menu.add(1,102,2,"恢复");
        }else {
            menu.add(1,103,3,"删除");
            switch (status) {
                case BaseActivity.PASSED:
                    menu.add(1,104,4,"拒绝");
                    break;
                case BaseActivity.UNCHECKED:
                    menu.add(1,105,5,"通过");
                    menu.add(1,104,6,"拒绝");
                    break;
                case BaseActivity.REJECTED:
                    menu.add(1,105,7,"通过");
                    break;
                default:break;
            }
        }
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
                if (mapArrayList_content.size()!=0) {
                    Intent intent = new Intent(TimelineDetailActivity.this,TimelineEditActivity.class);
                    intent.putExtra("is_add",false);
                    intent.putExtra("content",mapArrayList_content.get(0).get("content"));
                    intent.putExtra("id",Long.parseLong(String.valueOf(mapArrayList_content.get(0).get("id"))));
                    intent.putExtra("event_id",Long.parseLong(String.valueOf(mapArrayList_content.get(0).get("event_id"))));
                    intent.putExtra("happen_time",mapArrayList_content.get(0).get("happen_time"));
                    intent.putExtra("title",mapArrayList_content.get(0).get("title"));
                    startActivityForResult(intent,1);
                }
                break;
            case 102:
                changeStatus(BaseActivity.OP_RECOVER);
                break;
            case 103:
                changeStatus(BaseActivity.OP_DELETE);
                break;
            case 104:
                changeStatus(BaseActivity.OP_REJECT);
                break;
            case 105:
                changeStatus(BaseActivity.OP_PASS);
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        TimelineDetailActivity.this.finish();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    public void changeStatus(int op) {
        StringBuilder builder = new StringBuilder();
        builder.append("/admin/timeline/");
        switch (op) {
            case BaseActivity.OP_DELETE:
                builder.append("delete");
                break;
            case BaseActivity.OP_RECOVER:
                builder.append("recover");
                break;
            case BaseActivity.OP_REJECT:
                builder.append("reject");
                break;
            case BaseActivity.OP_PASS:
                builder.append("pass");
                break;
            default:break;
        }
        builder.append("?id=").append(id);
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        ViewHandler.toastShow(TimelineDetailActivity.this,BaseActivity.OPERATE_SUCCESS);
                        break;
                    case FAIL:
                        ViewHandler.toastShow(TimelineDetailActivity.this,BaseActivity.OPERATE_FAIL);
                        break;
                    case EMPTY:
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body) throws IOException {

            }

            @Override
            public void onRespError() {
                ViewHandler.toastShow(TimelineDetailActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object) {
                ViewHandler.toastShow(TimelineDetailActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired() {
                ViewHandler.alertShowAndExitApp(TimelineDetailActivity.this);
            }
        },this);
        NetUtil.reqSendGet(this,builder.toString(),callback);
    }
}
