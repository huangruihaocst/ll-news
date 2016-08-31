package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ihandy.a2014011385.R;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void getCategories(long timestamp, final CallBack<HashMap<String, String>> callBack) {
        // first level of cache: from memory
        Toast.makeText(context, "getting categories", Toast.LENGTH_LONG).show();
        final Cache cache = Cache.getInstance();
        if (cache.categories != null && cache.categories.size() != 0) { // successfully get categories from memory
            Toast.makeText(context, "in memory", Toast.LENGTH_LONG).show();
            callBack.onCallBack(cache.categories);
        } else { // nothing in memory, into the second level of cache: database
            DatabaseHelper databaseHelper = new DatabaseHelper(context,
                    context.getString(R.string.app_name), null, 1);
            final SQLiteDatabase database = databaseHelper.getWritableDatabase();

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = GET_CATEGORIES_URL + timestamp;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    HashMap<String, String> categories = ParseHelper.parseCategoriesHashMap(response);

                    cache.categories = categories; // refresh the first level of cache

                    // TODO: refresh the second level of cache
                    final String NEW_TABLE = "CREATE TABLE IF NOT EXISTS categories (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT," +
                            "title TEXT," +
                            "subscription INTEGER);";
                    database.execSQL(NEW_TABLE);

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

    public void getNewsList(String category, final CallBack<ArrayList<News>> callBack) {
        // TODO: handle amount of news
        // TODO: add 2 levels of cache
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = GET_NEWS_LIST_URL.replace("<category>", category); // ignore <max_news_id>
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<News> newsArrayList = ParseHelper.parseNewsList(response);
                callBack.onCallBack(newsArrayList);
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
