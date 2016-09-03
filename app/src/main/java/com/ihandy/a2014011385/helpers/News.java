package com.ihandy.a2014011385.helpers;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by huangruihao on 16-8-27.
 */
public class News extends SugarRecord implements Serializable {
    String categoryName;
    String country;
    long fetchedTime; // timestamp
    String imageURLsJSON; // in order to store in database with SugarORM
    long newsId;
    String origin;
    long[] relativeNews; // the IDs of relative news
    String sourceName;
    String sourceURL;
    String title;
    long updatedTime; // timestamp
    boolean favorite = false; // default: not added to favorites

    private final String NEWS_TAG = "News";

    public News() {}

    public News(String newsJSON) { // DataAccessor has permit the newsJSON to be valid
        ParseHelper.parseNews(this, newsJSON);
    }

    public String getCategoryName() {
        return categoryName;
    }
    public String getCountry() {
        return country;
    }
    public long getFetchedTime() {
        return fetchedTime;
    }
    public String getImageURLsJSON() {
        return imageURLsJSON;
    }
    public long getNewsId() {
        return newsId;
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
    public boolean isFavorite(){
        return favorite;
    }
    public void toggleFavorite() {
        favorite = !favorite;
    }
}
