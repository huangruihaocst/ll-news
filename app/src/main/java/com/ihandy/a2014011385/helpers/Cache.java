package com.ihandy.a2014011385.helpers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangruihao on 16-8-31.
 */
public class Cache {
    HashMap<String, String> categories;

    ArrayList<News> newsArrayList;

    private static Cache ourInstance = new Cache();

    public static Cache getInstance() {
        return ourInstance;
    }

    private Cache() {}
}
