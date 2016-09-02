package com.ihandy.a2014011385.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;

import com.ihandy.a2014011385.helpers.Category;

import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-26.
 */
public class CategoriesPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Pair<Fragment, Category>> categoriesList = new ArrayList<>();
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
        return categoriesList.get(position).second.title;
    }

    /**
     * Add fragment if the category does not exist
     * @param fragment
     * @param category
     */
    public void addFragment(Fragment fragment, Category category) {
        boolean isNews = true;
        for (Pair<Fragment, Category> pair: categoriesList) {
            if (pair.second.name.equals(category.name)) {
                isNews = false;
            }
        }
        if (isNews) {
            Pair<Fragment, Category> pair = new Pair<>(fragment, category);
            categoriesList.add(pair);
            notifyDataSetChanged();
        }
    }

    /**
     * Remove fragment if the category exists
     * @param category
     */
    public void removeFragment(Category category) {
        for (Pair<Fragment, Category> pair: categoriesList) {
            if (pair.second.name.equals(category.name)) {
                categoriesList.remove(pair);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
