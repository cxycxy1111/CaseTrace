package com.alfredteng.casetrace.cases;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
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
import java.util.Map;
import java.util.PropertyResourceBundle;

public class CaseViewActivity extends BaseActivity implements HttpResultListener {

    private Toolbar toolbar;
    private WebView webView;
    private TextView tv_id,tv_title,tv_event_id,tv_event_title,tv_create_time,tv_update_time,tv_upvote_count,
            tv_downvote_count,tv_read_count,tv_del,tv_status;
    private static final int SOURCE_LOAD_DETAIL = 0;
    private static final int SOURCE_DELETE = 1;
    private static final int SOURCE_RECOVER = 2;
    private static final int SOURCE_PASS = 3;
    private static final int SOURCE_REJECT = 4;
    private boolean del = false;
    private boolean isAdd = false;
    private int status;
    private long id = 0;
    private long event_id = 0;
    private String content = "";
    private ArrayList<Map<String,String>> mapArrayList_case = new ArrayList<>();
    private String[] keys_case = new String[]{"id","title","event_id","event_title","user_id", "creator_icon",
            "case_happen_time","case_create_time","update_time","upvote_count", "downvote_count","view_count",
            "content","status","del"};
    private ArrayList<String> arrayList_intent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_view);
        initViews();
        arrayList_intent = getIntent().getStringArrayListExtra("data");
        isAdd = getIntent().getBooleanExtra("is_add",false);
        id = Long.parseLong(arrayList_intent.get(0));
        status = Integer.parseInt(arrayList_intent.get(1));
        del = Boolean.parseBoolean(arrayList_intent.get(2));
        ViewHandler.initToolbarWithBackButton(this,toolbar, R.string.toolbar_tilte_case_info,R.id.toolbar_general);
        NetUtil.reqSendGet(this,"/admin/case/qry/detail?id=" + arrayList_intent.get(0),new HttpCallback(this,this,
                SOURCE_LOAD_DETAIL));
    }

    private void initViews() {
        webView = (WebView)findViewById(R.id.wv_a_case_view);
        tv_id = (TextView)findViewById(R.id.et_id_a_case_view);
        tv_title = (TextView)findViewById(R.id.et_title_a_case_view);
        tv_event_id = (TextView)findViewById(R.id.et_event_id_a_case_view);
        tv_event_title = (TextView)findViewById(R.id.et_event_title_a_case_view);
        tv_create_time = (TextView)findViewById(R.id.et_create_time_a_case_view);
        tv_update_time = (TextView)findViewById(R.id.et_update_time_a_case_view);
        tv_upvote_count = (TextView)findViewById(R.id.et_upvote_count_a_case_view);
        tv_downvote_count = (TextView)findViewById(R.id.et_down_count_a_case_view);
        tv_read_count = (TextView)findViewById(R.id.et_view_count_a_case_view);
        tv_del = (TextView)findViewById(R.id.et_del_a_case_view);
        tv_status = (TextView)findViewById(R.id.et_status_a_case_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,101,1,R.string.option_menu_edit);
        if (del) {
            menu.add(1,102,2,R.string.option_menu_recover);
        }else {
            menu.add(1,103,3,R.string.option_menu_delete);
            switch (status) {
                case BaseActivity.PASSED:
                    menu.add(1,104,4,R.string.option_menu_reject);
                    break;
                case BaseActivity.UNCHECKED:
                    menu.add(1,105,5,R.string.option_menu_pass);
                    menu.add(1,104,6,R.string.option_menu_reject);
                    menu.add(1,103,7,R.string.option_menu_delete);
                    break;
                case BaseActivity.REJECTED:
                    menu.add(1,105,7,R.string.option_menu_pass);
                    menu.add(1,103,8,R.string.option_menu_delete);
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
                Intent intent = new Intent(this,CaseEditActivity.class);
                intent.putExtra("is_add",false);

                intent.putStringArrayListExtra("data",arrayList_intent);
                startActivityForResult(intent,1);
                break;
            case 102:
                NetUtil.reqSendGet(this,"/admin/case/recover?id=" + id,new HttpCallback(this,this,SOURCE_RECOVER));
                break;
            case 103:
                NetUtil.reqSendGet(this,"/admin/case/delete?id=" + id,new HttpCallback(this,this,SOURCE_DELETE));
                break;
            case 104:
                NetUtil.reqSendGet(this,"/admin/case/reject?id=" + id,new HttpCallback(this,this,SOURCE_REJECT));
                break;
            case 105:
                NetUtil.reqSendGet(this,"/admin/case/pass?id=" + id,new HttpCallback(this,this,SOURCE_PASS));
                break;
            default:break;
        }
        return true;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.finish();
    }

    @Override
    public void onRespStatus(String body, int source) {
        super.onRespStatus(body, source);
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                setResult(1);
                this.finish();
                break;
            case FAIL:
                ViewHandler.toastShow(this,BaseActivity.OPERATE_FAIL);
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        super.onRespMapList(body, source);
        switch (SOURCE_LOAD_DETAIL) {
            case SOURCE_LOAD_DETAIL:
                mapArrayList_case = JsonUtil.strToListMap(body,keys_case);
                if (mapArrayList_case.size()>0) {
                    content = mapArrayList_case.get(0).get("content");
                    id = Long.valueOf(String.valueOf(mapArrayList_case.get(0).get("id")));
                    event_id = Long.valueOf(String.valueOf(mapArrayList_case.get(0).get("event_id")));
                    webView.loadData(str_html_prefix + content + str_html_suffix,"text/html","utf-8");
                    arrayList_intent.add(3,String.valueOf(event_id));
                    arrayList_intent.add(4,content);

                    Map<String,String> map = new HashMap<>();
                    map = mapArrayList_case.get(0);
                    Object o1 = map.get("id");
                    String s1 = String.valueOf(o1);
                    Object o2 = map.get("event_id");
                    String s2 = String.valueOf(o2);
                    Object o3 = map.get("upvote_count");
                    String s3 = String.valueOf(o3);
                    Object o4 = map.get("downvote_count");
                    String s4 = String.valueOf(o4);
                    Object o5 = map.get("view_count");
                    String s5 = String.valueOf(o5);
                    tv_id.setText(s1);
                    tv_title.setText(map.get("title"));
                    tv_event_id.setText(s2);
                    tv_event_title.setText(map.get("event_title"));
                    tv_create_time.setText(map.get("case_create_time").substring(0,map.get("case_create_time").length()-2));
                    tv_update_time.setText(map.get("update_time").substring(0,map.get("update_time").length()-2));
                    tv_upvote_count.setText(s3);
                    tv_downvote_count.setText(s4);
                    tv_read_count.setText(s5);

                    if (del) {
                        tv_del.setText(R.string.entity_del_deleted);
                    }else {
                        tv_del.setText(R.string.entity_del_undeleted);
                    }
                    switch (status) {
                        case BaseActivity.REJECTED:
                            tv_status.setText(R.string.entity_status_rejected);
                            break;
                        case BaseActivity.PASSED:
                            tv_status.setText(R.string.entity_status_passed);
                            break;
                        case BaseActivity.UNCHECKED:
                            tv_status.setText(R.string.entity_status_unchecked);
                            break;
                        default:break;
                    }
                }
                break;
            default:break;
        }
    }

    @Override
    public void onRespError(int source) {
        super.onRespError(source);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        super.onReqFailure(object, source);
    }

    @Override
    public void onRespSessionExpired(int source) {
        super.onRespSessionExpired(source);
    }
}
