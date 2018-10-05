package com.alfredteng.casetrace.cases;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

public class CaseEditActivity extends BaseActivity {

    private boolean isAdd = false;
    private boolean del = false;
    private boolean isLoaded = false;
    private int status = 0;
    private long id = 0;
    private long event_id = 0;
    private String case_title = "";
    private Toolbar toolbar;
    private AlfredText alfredText;
    private static final String TAG="CaseEditActivity";
    private ArrayList<String> arrayList_intent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_edit);
        alfredText = (AlfredText)findViewById(R.id.kt_a_add_cases);
        isAdd = getIntent().getBooleanExtra("is_add",false);
        if (isAdd) {

        }else {
            arrayList_intent = getIntent().getStringArrayListExtra("data");
            id = Long.parseLong(arrayList_intent.get(0));
            status = Integer.parseInt(arrayList_intent.get(1));
            del = Boolean.parseBoolean(arrayList_intent.get(2));
            event_id = Long.parseLong(String.valueOf(arrayList_intent.get(3)));
            String content = arrayList_intent.get(4);
            alfredText.fromHtml(content);
            ViewHandler.initToolbarWithBackButton(this,toolbar,R.string.toolbar_tilte_case_edit,R.id.toolbar_general);
        }
        if (isAdd) {
            if (Tool.getStringFromPref(this,"case_draft","content") != null) {
                if (!Tool.getStringFromPref(this,"case_draft","content").equals("")) {
                    alfredText.fromHtml(Tool.getStringFromPref(this,"case_draft","content"));
                }
            }
            ViewHandler.initToolbarWithBackButton(this,toolbar,R.string.toolbar_tilte_case_add,R.id.toolbar_general);
        }else {

            id = getIntent().getLongExtra("id",0);
            event_id = getIntent().getLongExtra("event_id",0);

        }
        setUpAlfredText();
        isLoaded = true;
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
                if (isAdd) {
                    saveBeforeFinish();
                }
                this.finish();
                break;
            case 101:
                submit();
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                setResult(1);
                this.finish();
                break;
            default:break;
        }
    }

    private void saveBeforeFinish() {
        String body = alfredText.toHtml();
        if (body.length()!=0) {
            SharedPreferences sharedPreferences = getSharedPreferences("case_draft",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("content",body);
            editor.apply();
        }
    }

    private void submit() {
        String body = alfredText.toHtml();
        if (body.length() == 0) {
            ViewHandler.toastShow(this,"你还没有输入任何内容");
        }else {
            if (body.length() <= 5000) {
                Intent intent = new Intent(this,CaseEditExtInfoActivity.class);
                arrayList_intent.add(4,body);
                intent.putExtra("content",body);
                intent.putExtra("is_add",isAdd);
                if (isAdd) {
                    saveBeforeFinish();//保存
                    startActivityForResult(intent,1);
                }else {
                    intent.putStringArrayListExtra("data",arrayList_intent);
                    if (isLoaded) {
                        startActivityForResult(intent,1);
                    }else {
                        ViewHandler.toastShow(this,"暂未加载完所有内容，不允许提交");
                    }
                }
            }else {
                ViewHandler.toastShow(this,"字数已超过最大限制");
            }
        }
    }

    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold_a_add_cases);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.bold(!alfredText.contains(AlfredText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic_a_add_cases);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.italic(!alfredText.contains(AlfredText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline_a_add_cases);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.underline(!alfredText.contains(AlfredText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough_a_add_cases);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.strikethrough(!alfredText.contains(AlfredText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet_a_add_cases);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.bullet(!alfredText.contains(AlfredText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote_a_add_cases);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.quote(!alfredText.contains(AlfredText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link_a_add_cases);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear_a_add_cases);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alfredText.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CaseEditActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
