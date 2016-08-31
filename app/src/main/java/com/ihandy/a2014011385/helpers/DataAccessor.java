package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;
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

    public void getCategories(long timestamp, final CallBack<String> callBack) {
        // TODO: add 2 levels of cache
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = GET_CATEGORIES_URL + timestamp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.onCallBack(response);
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

    public void getNewsList(String category, final CallBack<String> callBack) {
        // TODO: handle amount of news
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = GET_NEWS_LIST_URL.replace("<category>", category); // ignore <max_news_id>
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.onCallBack(response);
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

    public void getImage(String url, final CallBack<ImageLoader.ImageContainer> callBack) {
        RequestQueue queue = Volley.newRequestQueue(context);
        ImageLoader loader = new ImageLoader(queue, new LruBitmapCache());
        loader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                callBack.onCallBack(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(DATA_ACCESSOR_TAG, error.getMessage());
            }
        });
    }
}
