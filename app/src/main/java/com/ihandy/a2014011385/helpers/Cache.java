package com.ihandy.a2014011385.helpers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huangruihao on 16-8-31.
 */
public class Cache {
    public Category[] categories;

    public HashMap<String, ArrayList<News>> newsArrayListHashMap = new HashMap<>();

    private static Cache ourInstance = new Cache();

    public static Cache getInstance() {
        return ourInstance;
    }

    boolean newListIsAvailable(String categoryName) {
        return newsArrayListHashMap != null
                && newsArrayListHashMap.size() != 0
                && newsArrayListHashMap.get(categoryName)!= null
                && newsArrayListHashMap.get(categoryName).size() != 0;
    }

    private Cache() {}
}
