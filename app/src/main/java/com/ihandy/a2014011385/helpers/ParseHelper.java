package com.ihandy.a2014011385.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by huangruihao on 16-8-30.
 */
public class ParseHelper {

    private final static String PARSE_HELPER_TAG = "ParseHelper";

    public static HashMap<String, String> parseCategoriesHashMap(String categoriesJSON) {
        HashMap<String, String> categories = new HashMap<>();
        try {
            JSONObject responseObject = new JSONObject(categoriesJSON);
            JSONObject dataObject = responseObject.getJSONObject("data"); // volley permits the status code to be 200
            JSONObject categoriesObject = dataObject.getJSONObject("categories");
            Iterator<String> categoriesIterator = categoriesObject.keys();
            while (categoriesIterator.hasNext()) {
                String key = categoriesIterator.next();
                categories.put(key, categoriesObject.getString(key));
            }
        } catch (JSONException e) {
            Log.e(PARSE_HELPER_TAG, e.getMessage());
        }
        return categories;
    }

    public static void parseNews(News news, String newsJSON) {
        try {
            JSONObject newsObject = new JSONObject(newsJSON);
            news.category = newsObject.getString("category");
            news.country = newsObject.getString("country");
            news.fetchedTime = newsObject.getLong("fetched_time");
            if (!newsObject.isNull("imgs")) {
                JSONArray imageUrlArray = newsObject.getJSONArray("imgs");
                news.imageURLs = new String[imageUrlArray.length()];
                for (int i = 0; i < imageUrlArray.length(); ++i) {
                    news.imageURLs[i] = imageUrlArray.getJSONObject(i).getString("url");
                }
            } else {
                news.imageURLs = null;
            }
            news.ID = newsObject.getLong("news_id");
            news.origin = newsObject.getString("origin");
            if (!newsObject.isNull("relative_news")) {
                news.relativeNews = toLongArray(newsObject.getJSONArray("relative_news"));
            } else {
                news.relativeNews = null;
            }
            news.sourceName = newsObject.getJSONObject("source").getString("name");
            news.sourceURL = newsObject.getJSONObject("source").getString("url");
            news.title = newsObject.getString("title");
            news.updatedTime = newsObject.getLong("updated_time");
        } catch (JSONException e) {
            Log.e(PARSE_HELPER_TAG, e.getMessage());
        }
    }

    public static ArrayList<News> parseNewsList(String newsListJSON) {
        ArrayList<News> newsArrayList = new ArrayList<>();
        try {
            JSONObject responseListObject = new JSONObject(newsListJSON);
            JSONObject dataObject = responseListObject.getJSONObject("data");
            JSONArray newsListArray = dataObject.getJSONArray("news");
            for (int i = 0; i < newsListArray.length(); ++i) {
                JSONObject newsObject = newsListArray.getJSONObject(i);
                News news = new News(newsObject.toString());
                newsArrayList.add(news);
            }
        } catch (JSONException e) {
            Log.e(PARSE_HELPER_TAG, e.getMessage());
        }
        return newsArrayList;
    }

    private static String[] toStringArray(JSONArray jsonArray) throws JSONException{
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); ++i) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

    private static long[] toLongArray(JSONArray jsonArray) throws JSONException{
        long[] longArray = new long[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); ++i) {
            longArray[i] = jsonArray.getLong(i);
        }
        return longArray;
    }
}
