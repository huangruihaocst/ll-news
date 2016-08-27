package com.ihandy.a2014011385.Helpers;
/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    private static DataAccessor ourInstance = new DataAccessor();

    public static DataAccessor getInstance() {
        return ourInstance;
    }

    private DataAccessor() {}

    public static String[] getChannels() {
        return new String[]{"Sports", "Arts", "Biology"};
    }

    public static String[] getNews(String channel) {
        return null;
    }
}
