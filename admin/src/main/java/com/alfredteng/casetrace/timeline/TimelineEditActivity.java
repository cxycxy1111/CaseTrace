package com.alfredteng.casetrace.timeline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alfred.casetrace.editor.AlfredText;
import com.alfredteng.casetrace.R;
import com.example.alfredtools.BaseActivity;
import com.example.alfredtools.Tool;
import com.example.alfredtools.ViewHandler;


public class TimelineEditActivity extends BaseActivity {

    private boolean isAdd = false;
    private long event_id = 0;
    private long timeline_id = 0;
    private String title = "";
    private String happen_time;
    private Toolbar toolbar;
    private AlfredText knife;
    private static final String TAG="TimelineEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_add);
        ViewHandler.initToolbarWithBackButton(this,toolbar, R.string.toolbar_tilte_timeline_add,R.id.toolbar_general);
        knife = (AlfredText) findViewById(R.id.kt_a_add_timeline);
        knife.setSelection(knife.getEditableText().length());
        isAdd = getIntent().getBooleanExtra("is_add",false);
        //是新增时，检查是否存在草稿，如果有，加载草稿
        if (isAdd) {
            if (Tool.getStringFromPref(this,"timeline_draft","content") != null) {
                if (!Tool.getStringFromPref(this,"timeline_draft","content").equals("")) {
                    knife.fromHtml(Tool.getStringFromPref(this,"timeline_draft","content"));
                }
            }
        }else {//不是新增时，从页面中传过来
            String content = getIntent().getStringExtra("content");
            timeline_id = getIntent().getLongExtra("id",0);
            event_id = getIntent().getLongExtra("event_id",0);
            happen_time = getIntent().getStringExtra("happen_time");
            title = getIntent().getStringExtra("title");
            knife.fromHtml(content);
        }
        setUpAlfredText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,101,1,"保存");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //不是新增时，返回时保存
                if (isAdd) {
                    saveBeforeQuit();
                }
                this.finish();
                break;
            case 101:
                submit();
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
                        setResult(1);
                        TimelineEditActivity.this.finish();
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    private void submit() {
        String body = knife.toHtml();
        if (body.length() <= 5000) {
            Intent intent = new Intent(TimelineEditActivity.this,TimelineEditExtInfoActivity.class);
            intent.putExtra("content",body);
            intent.putExtra("isAdd",isAdd);
            if(!isAdd) {
                intent.putExtra("event_id",event_id);
                Log.d(TAG, "submit: event_id:" + event_id);
                intent.putExtra("timeline_id",timeline_id);
                intent.putExtra("title",title);
                intent.putExtra("happen_time",happen_time);
            }
            startActivityForResult(intent,1);
        }else {
            ViewHandler.toastShow(this,"字数已超过最大限制。");
        }
    }

    private void saveBeforeQuit() {
        if (isAdd) {
            if (!knife.toHtml().equals("")) {
                SharedPreferences sharedPreferences = getSharedPreferences("timeline_draft",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("content",knife.toHtml());
                editor.apply();
            }
        }
    }

    private void setUpAlfredText() {
        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();
    }

    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold_a_add_timeline);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bold(!knife.contains(AlfredText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic_a_add_timeline);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.italic(!knife.contains(AlfredText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline_a_add_timeline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.underline(!knife.contains(AlfredText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough_a_add_timeline);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.strikethrough(!knife.contains(AlfredText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet_a_add_timeline);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bullet(!knife.contains(AlfredText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote_a_add_timeline);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.quote(!knife.contains(AlfredText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link_a_add_timeline);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear_a_add_timeline);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TimelineEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
