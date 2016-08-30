package com.ihandy.a2014011385.helpers;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    Context context;

    private static DataAccessor ourInstance = new DataAccessor();

    public static DataAccessor getInstance() {
        return ourInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private DataAccessor() {}

    public String[] getCategories() {
        return new String[]{"Sports", "Arts", "Biology"};
    }

    public ArrayList<News> getNewsArrayList(String category) {
        // TODO: add real getNewsList method
        ArrayList<News> newsArrayList = new ArrayList<>();
        newsArrayList.add(News.newSimpleNewsInstance(context, "fuck", 12345678, "www.sina.com"));
        newsArrayList.add(News.newSimpleNewsInstance(context, "wtf", 87654321, "www.baidu.com"));
        newsArrayList.add(News.newSimpleNewsInstance(context, "www", 9876543, "www.toutiao.com"));
        return newsArrayList;
    }

    public News getNews(long ID) {return null;}
}
