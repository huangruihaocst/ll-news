package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ihandy.a2014011385.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    Context context;

    private final String DATA_ACCESSOR_TAG = "DataAccessor";
    private final String GET_CATEGORY_URL = "http://assignment.crazz.cn/news/en/category?timestamp=";

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
        String url = GET_CATEGORY_URL + timestamp;
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

    public ArrayList<News> getNewsArrayList(String category) {
        // TODO: add real getNewsList method
        ArrayList<News> newsArrayList = new ArrayList<>();
        newsArrayList.add(News.newSimpleNewsInstance(context, "fuck", 12345678, "www.sina.com"));
        newsArrayList.add(News.newSimpleNewsInstance(context, "wtf", 87654321, "www.baidu.com"));
        newsArrayList.add(News.newSimpleNewsInstance(context, "www", 9876543, "www.toutiao.com"));
        return newsArrayList;
    }

    public News getNews(long ID) {return null;}
}
