package com.ihandy.a2014011385.Helpers;

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
    private String sourceURL;
    private String title;
    private long updatedTime;

    News(String newsJSON) {
        try {
            JSONObject newsObject = new JSONObject(newsJSON);
            // TODO: parse json and initialize News object
        } catch (org.json.JSONException e) {
            System.out.println(e.getMessage());
        }

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
}
