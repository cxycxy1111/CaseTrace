package com.alfredteng.casetrace.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;

public class TimelineViewActivity extends BaseActivity {


    private boolean del = false;
    private int status;
    private long id = 0;
    private TextView tv_id,tv_title,tv_event_id,tv_event_title,tv_happen_time,tv_creator,tv_creator_type,tv_creator_nick_name,tv_create_time,tv_del,tv_status;
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
        tv_id = (TextView)findViewById(R.id.et_id_a_timeline_view);
        tv_title = (TextView)findViewById(R.id.et_title_a_timeline_view);
        tv_event_id = (TextView)findViewById(R.id.et_event_id_a_timeline_view);
        tv_event_title = (TextView)findViewById(R.id.et_event_title_a_timeline_view);
        tv_happen_time = (TextView)findViewById(R.id.et_happen_time_a_timeline_view);
        tv_create_time = (TextView)findViewById(R.id.et_create_time_a_timeline_view);
        tv_creator = (TextView)findViewById(R.id.et_creator_a_timeline_view);
        tv_creator_type = (TextView)findViewById(R.id.et_creator_type_a_timeline_view);
        tv_creator_nick_name = (TextView)findViewById(R.id.et_nick_name_a_timeline_view);
        tv_del = (TextView)findViewById(R.id.et_del_a_timeline_view);
        tv_status = (TextView)findViewById(R.id.et_status_a_timeline_view);
    }

    private void init() {
        String url = "/admin/timeline/qry/detail?id=" + getIntent().getLongExtra("id",0);
        HttpCallback callback = new HttpCallback(new HttpResultListener() {
            @Override
            public void onRespStatus(String body,int source) {

            }

            @Override
            public void onRespMapList(String body,int source) throws IOException {
                mapArrayList_content = JsonUtil.strToListMap(body,keys);
                Map<String,String> map = new HashMap<>();
                map = mapArrayList_content.get(0);
                webView.loadData(str_html_prefix + map.get("content") + str_html_suffix,"text/html","utf-8");
                Object o1 = map.get("id");
                tv_id.setText(String.valueOf(o1));

                Object o2= map.get("event_id");
                tv_event_id.setText(String.valueOf(o2));

                Object o3 = map.get("status");
                status = Integer.parseInt(String.valueOf(o3));
                if (status == PASSED) {
                    tv_status.setText(R.string.entity_status_passed);
                }else if (status == REJECTED) {
                    tv_status.setText(R.string.entity_status_rejected);
                }else if (status == UNCHECKED) {
                    tv_status.setText(R.string.entity_status_unchecked);
                }
                Object o4 = map.get("del");
                if (Boolean.parseBoolean(String.valueOf(o4))) {
                    tv_del.setText(R.string.entity_del_deleted);
                }else {
                    tv_del.setText(R.string.entity_del_undeleted);
                }

                Object o5 = map.get("creator");
                tv_creator.setText(String.valueOf(o5));

                Object o6 = map.get("creator_type");
                int i = Integer.parseInt(String.valueOf(o6));
                if (i == 0) {
                    tv_creator_type.setText("管理员");
                }else {
                    tv_creator_type.setText("用户");
                }

                tv_title.setText(map.get("title"));
                tv_event_title.setText(map.get("event_title"));
                tv_happen_time.setText(map.get("happen_time").substring(0,map.get("happen_time").length()-2));
                tv_create_time.setText(map.get("create_time").substring(0,map.get("create_time").length()-2));
                tv_creator_nick_name.setText(map.get("nick_name"));

            }

            @Override
            public void onRespError(int source) {
                ViewHandler.toastShow(TimelineViewActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object,int source) {
                ViewHandler.toastShow(TimelineViewActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(TimelineViewActivity.this);
            }
        },this,1);
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
                    Intent intent = new Intent(TimelineViewActivity.this,TimelineEditActivity.class);
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
                        TimelineViewActivity.this.finish();
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
            public void onRespStatus(String body,int source) {
                switch (NetRespStatType.dealWithRespStat(body)) {
                    case SUCCESS:
                        ViewHandler.toastShow(TimelineViewActivity.this,BaseActivity.OPERATE_SUCCESS);
                        setResult(1);
                        TimelineViewActivity.this.finish();
                        break;
                    case FAIL:
                        ViewHandler.toastShow(TimelineViewActivity.this,BaseActivity.OPERATE_FAIL);
                        break;
                    case EMPTY:
                        break;
                    default:break;
                }
            }

            @Override
            public void onRespMapList(String body,int source) throws IOException {

            }

            @Override
            public void onRespError(int source) {
                ViewHandler.toastShow(TimelineViewActivity.this,NetUtil.UNKNOWN_ERROR);
            }

            @Override
            public void onReqFailure(Object object,int source) {
                ViewHandler.toastShow(TimelineViewActivity.this,NetUtil.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onRespSessionExpired(int source) {
                ViewHandler.alertShowAndExitApp(TimelineViewActivity.this);
            }
        },this,1);
        NetUtil.reqSendGet(this,builder.toString(),callback);
    }
}
