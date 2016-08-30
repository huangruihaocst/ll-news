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
            news.imageURLs = (String[])toArray(newsObject.getJSONArray("imgs"));
            news.ID = newsObject.getLong("news_id");
            news.origin = newsObject.getString("origin");
            news.relativeNews = (News[])toArray(newsObject.getJSONArray("relative_news"));
            news.sourceName = newsObject.getJSONObject("source").getString("name");
            news.sourceURL = newsObject.getJSONObject("source").getString("url");
            news.title = newsObject.getString("title");
            news.updatedTime = newsObject.getLong("updated_time");
        } catch (JSONException e) {
            Log.e(PARSE_HELPER_TAG, e.getMessage());
        }
    }

    private static Object[] toArray(JSONArray jsonArray) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            objectArrayList.add(objectArrayList.get(i));
        }
        return objectArrayList.toArray();
    }
}
