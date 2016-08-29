package com.ihandy.a2014011385.helpers;
/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    private static DataAccessor ourInstance = new DataAccessor();

    public static DataAccessor getInstance() {
        return ourInstance;
    }

    private DataAccessor() {}

    public String[] getChannels() {
        return new String[]{"Sports", "Arts", "Biology"};
    }

    public News[] getNewsList(String channel) {
        return null;
    }

    public News getNews(long ID) {return null;}
}
