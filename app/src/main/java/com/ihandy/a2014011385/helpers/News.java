package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.media.Image;
import android.util.Log;

import org.json.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-27.
 */
public class News implements Serializable{
    String category;
    String country;
    long fetchedTime; // timestamp
    String[] imageURLs;
    long ID;
    String origin;
    long[] relativeNews; // the IDs of relative news
    String sourceName;
    String sourceURL;
    String title;
    long updatedTime; // timestamp
    Context context;

    private final String NEWS_TAG = "News";

    public News() {
        // Required empty public constructor
    }

    public News(String newsJSON) { // DataAccessor has permit the newsJSON to be valid
        ParseHelper.parseNews(this, newsJSON);
    }

    public static News newSimpleNewsInstance(Context context, String title, long time, String sourceURL) {
        News simpleNews = new News();
        simpleNews.context = context;
        simpleNews.title = title;
        simpleNews.fetchedTime = time;
        simpleNews.sourceURL = sourceURL;
        return simpleNews;
    }

    public String getCategory() {
        return category;
    }
    public String getCountry() {
        return country;
    }
    public long getFetchedTime() {
        return fetchedTime;
    }
    public String[] getImageURLs() {
        return imageURLs;
    }
    public long getID() {
        return ID;
    }
    public String getOrigin() {
        return origin;
    }
    public long[] getRelativeNews() {
        return relativeNews;
    }
    public String getSourceName() {
        return sourceName;
    }
    public String getSourceURL() {
        return sourceURL;
    }
    public String getTitle() {
        return title;
    }
    public long getUpdatedTime() {
        return updatedTime;
    }
}
