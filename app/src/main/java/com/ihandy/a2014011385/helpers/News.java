package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.media.Image;
import android.util.Log;

import org.json.*;

import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-27.
 */
public class News {
    private String category;
    private String country;
    private long fetchedTime; // timestamp
    private String[] imageURLs;
    private long ID;
    private String origin;
    private News[] relativeNews;
    private String sourceName;
    private String sourceURL;
    private String title;
    private long updatedTime; // timestamp
    Context context;

    private final String NEWS_TAG = "News";

    public News() {
        // Required empty public constructor
    }

    public News(String newsJSON) { // DataAccessor has permit the newsJSON to be valid
        try {
            JSONObject newsObject = new JSONObject(newsJSON);
            category = newsObject.getString("category");
            country = newsObject.getString("country");
            fetchedTime = newsObject.getLong("fetched_time");
            imageURLs = (String[])toArray(newsObject.getJSONArray("imgs"));
            ID = newsObject.getLong("news_id");
            origin = newsObject.getString("origin");
            relativeNews = (News[])toArray(newsObject.getJSONArray("relative_news"));
            sourceName = newsObject.getJSONObject("source").getString("name");
            sourceURL = newsObject.getJSONObject("source").getString("url");
            title = newsObject.getString("title");
            updatedTime = newsObject.getLong("updated_time");
        } catch (org.json.JSONException e) {
            Log.e(NEWS_TAG, e.getMessage());
        }

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
    public News[] getRelativeNews() {
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

    public Image[] getImages() {
        // TODO: add real getImages method
        return null;
    }

    private Object[] toArray(JSONArray jsonArray) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            objectArrayList.add(objectArrayList.get(i));
        }
        return objectArrayList.toArray();
    }
}
