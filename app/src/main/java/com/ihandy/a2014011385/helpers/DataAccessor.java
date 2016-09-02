package com.ihandy.a2014011385.helpers;

import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangruihao on 16-8-26.
 */
public class DataAccessor {
    Context context;

    private final String DATA_ACCESSOR_TAG = "DataAccessor";
    private final String GET_CATEGORIES_URL = "http://assignment.crazz.cn/news/en/category?timestamp=";
    private final String GET_MORE_NEWS_URL = "http://assignment.crazz.cn/news/query?locale=en&category=<category>&max_news_id=";
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
            List<Category> categoryList = Select.from(Category.class).list();
            if (categoryList != null && categoryList.size() != 0) {
                Category[] categories = toCategoryArray(categoryList);
                cache.categories = categories;
                callBack.onCallBack(categories);
            } else { // nothing in database, try to access from the Internet
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

    /**
     * When reading from cache, read all the news from it. (As long as the process is not killed, they are there)
     * When reading from database, read all the news from it. They were saved to database before when they were gotten from the Internet.
     * When reading from the Internet, get <item_count> of news (we do not know its value). It is called only once when the user first open the application.
     * For further latest news, forceAccessFromInternet will get more news and save them to database.
     * For further past news, getMoreNews() will get more past news  and save them to database.
     * No matter how news are saved to database, they are displayed in the reverse chronological order.
     * @param categoryName the name of the category (not title)
     * @param callBack need to override onCallBack
     */
    public void getNewsList(final String categoryName, final CallBack<ArrayList<News>> callBack) {
        // TODO: handle amount of news
        // the first level of cache: from memory
        final Cache cache = Cache.getInstance();
        if (cache.newListIsAvailable(categoryName)) {
            Log.w(DATA_ACCESSOR_TAG, "first: " + String.valueOf(cache.newsArrayListHashMap.get(categoryName).size()) + " from cache");
            callBack.onCallBack(cache.newsArrayListHashMap.get(categoryName));
        } else { // nothing in cache, access from database (the second level of cache: database)
            List<News> newsList = Select.from(News.class)
                    .where(Condition.prop("category_name").eq(categoryName))
                    .orderBy("news_id").list();
            Collections.reverse(newsList);
            Log.w(DATA_ACCESSOR_TAG, "first: " + String.valueOf(newsList.size()) + " from database");
            if (newsList != null && newsList.size() != 0) {
                cache.newsArrayListHashMap.put(categoryName, new ArrayList<>(newsList));
                callBack.onCallBack(new ArrayList<>(newsList));
            } else { // nothing in database, access from the Internet
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = GET_NEWS_LIST_URL.replace("<category>", categoryName); // ignore <max_news_id>
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ArrayList<News> newsArrayList = ParseHelper.parseNewsList(response);
                                Collections.sort(newsArrayList, new Comparator<News>() {
                                    @Override
                                    public int compare(News n1, News n2) {
                                        if (n1.newsId == n2.newsId){
                                            return 0;
                                        } else {
                                            return n1.newsId < n2.newsId ? 1 : -1;
                                        }
                                    }
                                });
                                Log.w(DATA_ACCESSOR_TAG, "first: " + String.valueOf(newsArrayList.size()) + " from Internet");
                                cache.newsArrayListHashMap.put(categoryName, newsArrayList); // refresh the first level of cache
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
                        callBack.onCallBack(null);
                    }
                });
                queue.add(stringRequest);
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

    public void forceAccessFromInternet(final String categoryName, final CallBack<ArrayList<News>> callBack) {
        final Cache cache = Cache.getInstance(); // also refresh cache
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = GET_NEWS_LIST_URL.replace("<category>", categoryName); // ignore <max_news_id>
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<News> newsArrayList = ParseHelper.parseNewsList(response);
                        // refresh the first level of cache, cache has to be bigger
                        removeAll(cache.newsArrayListHashMap.get(categoryName), newsArrayList);
                        cache.newsArrayListHashMap.get(categoryName).addAll(newsArrayList);
                        // newses have to be in the right order
                        Collections.sort(cache.newsArrayListHashMap.get(categoryName), new Comparator<News>() {
                            @Override
                            public int compare(News n1, News n2) {
                                if (n1.newsId == n2.newsId){
                                    return 0;
                                } else {
                                    return n1.newsId < n2.newsId ? 1 : -1;
                                }
                            }
                        });
                        List<News> existsList = Select.from(News.class)
                                .where(Condition.prop("category_name").eq(categoryName)).list();
                        removeAll(newsArrayList, new ArrayList<>(existsList));
                        for (News news: newsArrayList) { // add news that does not exist in the database
                            news.save();
                        }
                        callBack.onCallBack(cache.newsArrayListHashMap.get(categoryName)); // newsArrayList is incomplete now
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { // something wrong with the network, permitting there will not be something wrong with the server
                Toast.makeText(context, context.getString(R.string.network_error),
                        Toast.LENGTH_LONG).show();
                Log.e(DATA_ACCESSOR_TAG, context.getString(R.string.network_error));
                callBack.onCallBack(null); // notify the caller that the refreshing failed
            }
        });
        queue.add(stringRequest);
    }

    /**
     * Get further past news, pass null to onCallBack if failed, the same ArrayList if no more news.
     * @param categoryName the name of the category (not title)
     * @param callBack need to override onCallBack
     */
    public void getMoreNews(final String categoryName, final CallBack<ArrayList<News>> callBack) {
        final Cache cache = Cache.getInstance();
        final List<News> existsList = Select.from(News.class)
                .where(Condition.prop("category_name").eq(categoryName))
                .orderBy("news_id").list();
        long minNewsId = existsList.get(0).newsId;

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = GET_MORE_NEWS_URL.replace("<category>", categoryName) + minNewsId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<News> newsArrayList = ParseHelper.parseNewsList(response);
                        // refresh the first level of cache, cache has to be bigger
                        removeAll(cache.newsArrayListHashMap.get(categoryName), newsArrayList);
                        cache.newsArrayListHashMap.get(categoryName).addAll(newsArrayList);
                        // newses have to be in the right order
                        Collections.sort(cache.newsArrayListHashMap.get(categoryName), new Comparator<News>() {
                            @Override
                            public int compare(News n1, News n2) {
                                if (n1.newsId == n2.newsId){
                                    return 0;
                                } else {
                                    return n1.newsId < n2.newsId ? 1 : -1;
                                }
                            }
                        });
                        removeAll(newsArrayList, new ArrayList<>(existsList));
                        Log.w(DATA_ACCESSOR_TAG, "more: " + String.valueOf(newsArrayList.size()) + " from Internet");
                        for (News news: newsArrayList) { // add news that does not exist in the database
                            news.save();
                        }
                        // pass the new list, already in the correct order
                        callBack.onCallBack(cache.newsArrayListHashMap.get(categoryName));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { // something wrong with the network, permitting there will not be something wrong with the server
                Toast.makeText(context, context.getString(R.string.network_error),
                        Toast.LENGTH_LONG).show();
                Log.e(DATA_ACCESSOR_TAG, context.getString(R.string.network_error));
                callBack.onCallBack(null);
            }
        });
        queue.add(stringRequest);
    }

    private void removeAll(ArrayList<News> l1, ArrayList<News> l2) { // remove items from l1 if it exists in l2
        Iterator<News> iterator = l1.iterator();
        while (iterator.hasNext()) {
            News n1 = iterator.next();
            boolean exists = false;
            for (News n2: l2) {
                if (n1.newsId == n2.newsId) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                iterator.remove();
            }
        }
    }
}