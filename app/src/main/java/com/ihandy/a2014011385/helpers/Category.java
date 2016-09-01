package com.ihandy.a2014011385.helpers;

import com.orm.SugarRecord;

/**
 * Created by huangruihao on 16-9-1.
 */
public class Category extends SugarRecord{
    public String name;
    public String title;

    public Category(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public Category(){}

    boolean following = true; // default: following
}
