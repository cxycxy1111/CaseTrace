package com.alfredteng.casetrace.utils.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragments;

    public MainActivityPagerAdapter(FragmentManager fragmentManager,List<Fragment> fragments) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.fragments = fragments;

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
