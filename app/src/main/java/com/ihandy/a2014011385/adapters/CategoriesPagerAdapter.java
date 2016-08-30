package com.ihandy.a2014011385.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-26.
 */
public class CategoriesPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Pair<Fragment, String>> categoriesList = new ArrayList<>();
    public CategoriesPagerAdapter(FragmentManager manager) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        return categoriesList.get(position).first;
    }
    @Override
    public int getCount() {
        return categoriesList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return categoriesList.get(position).second;
    }
    public void addFragment(Fragment fragment, String title) {
        Pair<Fragment, String> category = new Pair<>(fragment, title);
        categoriesList.add(category);
        notifyDataSetChanged();
    }
}
