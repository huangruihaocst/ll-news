package com.ihandy.a2014011385.helpers;

import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    private static DataAccessor ourInstance = new DataAccessor();

    public static DataAccessor getInstance() {
        return ourInstance;
    }

    private DataAccessor() {}

    public String[] getCategories() {
        return new String[]{"Sports", "Arts", "Biology"};
    }

    public ArrayList<News> getNewsArrayList(String category) {
        return null;
    }

    public News getNews(long ID) {return null;}
}
