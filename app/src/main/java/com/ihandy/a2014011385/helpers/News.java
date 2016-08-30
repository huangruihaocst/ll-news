package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import com.ihandy.a2014011385.R;

import org.json.*;

/**
 * Created by huangruihao on 16-8-27.
 */
public class News {
    private String category;
    private String country;
    private long fetchedTime;
    private String[] imageURLs;
    private long ID;
    private String origin;
    private News[] relativeNews;
    private String sourceURL; // TODO: add conversion from sourceURL to sourceName
    private String title;
    private long updatedTime;
    Context context;

    public News() {
        // Required empty public constructor
    }

    public News(String newsJSON) {
        try {
            JSONObject newsObject = new JSONObject(newsJSON);
            // TODO: parse json and initialize News object
        } catch (org.json.JSONException e) {
            System.out.println(e.getMessage());
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
}
