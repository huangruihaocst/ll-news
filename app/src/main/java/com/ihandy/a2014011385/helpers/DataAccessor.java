package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ihandy.a2014011385.R;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    Context context;

    private final String DATA_ACCESSOR_TAG = "DataAccessor";
    private final String GET_CATEGORIES_URL = "http://assignment.crazz.cn/news/en/category?timestamp=";
//    private final String GET_NEWS_LIST_URL = "http://assignment.crazz.cn/news/query?locale=en&category=<category>&max_news_id=";
    private final String GET_NEWS_LIST_URL = "http://assignment.crazz.cn/news/query?locale=en&category=<category>";

    private static DataAccessor ourInstance = new DataAccessor();

    public static DataAccessor getInstance() {
        return ourInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private DataAccessor() {}

    public void getCategories(long timestamp, final CallBack<Category[]> callBack) {
        // the first level of cache: from memory
        final Cache cache = Cache.getInstance();
        if (cache.categories != null && cache.categories.length != 0) { // successfully get categories from memory
            callBack.onCallBack(cache.categories);
        } else { // nothing in memory, try to access from database (the second level of cache: from database)
            boolean isInDatabase = false;
            try {
                List<Category> categoryList = Select.from(Category.class).list();
                if (categoryList != null && categoryList.size() != 0) {
                    isInDatabase = true;
                    Category[] categories = toCategoryArray(categoryList);
                    callBack.onCallBack(categories);
                }
            } catch (SQLiteException e) {
                Log.i(DATA_ACCESSOR_TAG, e.getMessage());
            } finally {
                if (!isInDatabase) { // nothing in database, try to access from the Internet
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = GET_CATEGORIES_URL + timestamp;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Category[] categories = ParseHelper.parseCategories(response);

                            cache.categories = categories; // refresh the first level of cache
                            for (Category category: categories) {
                                category.save(); // refresh the second level of cache
                            }

                            callBack.onCallBack(categories);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { // something wrong with the network, permitting there will not be something wrong with the server
                            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            Log.e(DATA_ACCESSOR_TAG, context.getString(R.string.network_error));
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        }
    }

    public void getNewsList(final String categoryName, final CallBack<ArrayList<News>> callBack) {
        // TODO: handle amount of news
        // the first level of cache: from memory
        final Cache cache = Cache.getInstance();
        if (cache.newsArrayList != null
                && cache.newsArrayList.size() != 0
                && cache.newsArrayList.get(categoryName)!= null
                && cache.newsArrayList.get(categoryName).size() != 0) {
            callBack.onCallBack(cache.newsArrayList.get(categoryName));
        } else {
            boolean isInDatabase = false;
            try {
                List<News> newsList = Select.from(News.class)
                        .where(Condition.prop("category_name").eq(categoryName))
                        .orderBy("news_id").list();
                if (newsList != null && newsList.size() != 0) {
                    isInDatabase = true;
                    callBack.onCallBack(new ArrayList<>(newsList));
                }
            } catch (SQLiteException e) {
                Log.i(DATA_ACCESSOR_TAG, e.getMessage());
            } finally {
                if (!isInDatabase) { // nothing in database, try to access from the Internet
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = GET_NEWS_LIST_URL.replace("<category>", categoryName); // ignore <max_news_id>
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<News> newsArrayList = ParseHelper.parseNewsList(response);
                            cache.newsArrayList.put(categoryName, newsArrayList); // refresh the first level of cache
                            for (News news: newsArrayList) { // refresh the second level of cache
                                news.save();
                            }
                            callBack.onCallBack(newsArrayList);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { // something wrong with the network, permitting there will not be something wrong with the server
                            Toast.makeText(context, context.getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                            Log.e(DATA_ACCESSOR_TAG, context.getString(R.string.network_error));
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        }
    }

    private Category[] toCategoryArray(List<Category> categoryList) {
        Category[] categories = new Category[categoryList.size()];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = categoryList.get(i);
        }
        return categories;
    }
}
