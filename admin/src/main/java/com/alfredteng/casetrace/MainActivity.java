package com.alfredteng.casetrace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alfredteng.casetrace.admin.AdminInfoActivity;
import com.alfredteng.casetrace.company.CompanyInfoActivity;
import com.alfredteng.casetrace.event.EventInfoActivity;
import com.alfredteng.casetrace.product.ProductInfoActivity;
import com.alfredteng.casetrace.user.UserInfoActivity;
import com.alfredteng.casetrace.util.BaseActivity;
import com.alfredteng.casetrace.util.MainFragment;
import com.alfredteng.casetrace.util.ViewHandler;
import com.alfredteng.casetrace.util.adaptor.MainActivityPagerAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements MainFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private MainActivityPagerAdapter mainActivityPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager fragmentManager;
    private MenuItem prevMenuItem;

    private static final String TOOLBAR_TITLE="事件";
    private int selected_position = 0;

    private String[] bnb_title = new String[]{"事件","公司","用户"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewHandler.initToolbar(MainActivity.this,toolbar,TOOLBAR_TITLE);
        viewPager = (ViewPager)findViewById(R.id.vp_a_main);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bnb_a_main);
        fragmentManager = getSupportFragmentManager();
        initFragments();
        initViewPager();
        initBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,101,1,"新增事件");
        menu.add(1,102,2,"新增时间线");
        menu.add(1,103,3,"新增案例");
        menu.add(2,104,4,"新增公司");
        menu.add(2,105,5,"新增产品");
        menu.add(3,106,6,"新增管理员");
        menu.add(3,107,7,"新增用户");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case 101:
                intent = new Intent(this, EventInfoActivity.class);
                break;
            case 104:
                intent = new Intent(this,CompanyInfoActivity.class);
                break;
            case 105:
                intent = new Intent(this, ProductInfoActivity.class);
                break;
            case  106:
                intent = new Intent(this,AdminInfoActivity.class);
                break;
            case 107:
                intent = new Intent(this, UserInfoActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("is_add",true);
            startActivity(intent);
        }
        return true;
    }

    /**
     * 初始化Fragment
     * @return
     */
    private List<Fragment> initFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (fragments.size() == 0) {
            fragments.add(MainFragment.newInstance(1,"事件"));
            fragments.add(MainFragment.newInstance(2,"公司"));
            fragments.add(MainFragment.newInstance(3,"用户"));
        }
        return fragments;
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        fragments = initFragments();
        if (viewPager == null) {
            viewPager = (ViewPager)findViewById(R.id.vp_a_main);
        }
        if (mainActivityPagerAdapter == null) {
            mainActivityPagerAdapter = new MainActivityPagerAdapter(fragmentManager,fragments);
        }
        if (viewPager.getAdapter() == null) {
            viewPager.setAdapter(mainActivityPagerAdapter);
        }
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(bnb_title[position]);
                selected_position = position;
                if(prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initBottomNavigationBar() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bnb_a_main_1:
                        viewPager.setCurrentItem(0);
                        selected_position = 0;
                        break;
                    case R.id.bnb_a_main_2:
                        viewPager.setCurrentItem(1);
                        selected_position = 1;
                        break;
                    case R.id.bnb_a_main_3:
                        viewPager.setCurrentItem(2);
                        selected_position = 2;
                        break;
                    default:break;
                }
                return true;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
