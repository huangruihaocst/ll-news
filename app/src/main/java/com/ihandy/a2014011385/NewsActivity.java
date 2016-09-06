package com.ihandy.a2014011385;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihandy.a2014011385.helpers.CallBack;
import com.ihandy.a2014011385.helpers.DataAccessor;
import com.ihandy.a2014011385.helpers.News;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private final String NEWS_ACTIVITY_TAG = "NewsActivity";

    private final int SIMPLIFY_SUCCESS_WHAT = 0;

    News news;
    String html = "";
    WebView contentWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        news = (News) data.getSerializable(getString(R.string.key_main_news));

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SIMPLIFY_SUCCESS_WHAT:
                        contentWebView.getSettings().setJavaScriptEnabled(true);
                        contentWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                        break;
                    default:
                        // nothing
                }
            }
        };

        if (news != null) {
            toolbar.setTitle(news.getTitle()); // set title
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            contentWebView = (WebView) findViewById(R.id.content);
            contentWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // handle SDK level
                        view.loadUrl(request.getUrl().toString());
                    } else {
                        contentWebView.setWebViewClient(new WebViewClient(){
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                        });
                    }
                    return true;
                }
            });
            // TODO: add scroll bar to WebView
            // TODO: fix the bug of not WebView displaying the web thoroughly on API24
            contentWebView.loadUrl(news.getSourceURL()); // set content

            ImageView imageView = (ImageView) findViewById(R.id.image);
            if (news.getImageURLsJSON() != null) {
                Picasso.with(getApplicationContext()).load(news.getFirstImageUrl()).into(imageView);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_error_black_48dp));
            }

        } else {
            Log.w(NEWS_ACTIVITY_TAG, "News is NullPointer");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                DataAccessor accessor = DataAccessor.getInstance();
                accessor.setContext(getApplicationContext());
                accessor.simplifyWebsite(news.getSourceURL(), new CallBack<String>() {
                    @Override
                    public void onCallBack(String response) {
                        html = response;
                        Message message = new Message();
                        message.what = SIMPLIFY_SUCCESS_WHAT;
                        handler.sendMessage(message);
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        MenuItem item = menu.findItem(R.id.action_favorites);
        if (news.isFavorite()){
            item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_48dp));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            String imageUrl = "";
            if (news.getImageURLsJSON() != null) {
                try {
                    JSONArray imageUrls = new JSONArray(news.getImageURLsJSON());
                    imageUrl = imageUrls.getJSONObject(0).getString("url");
                } catch (JSONException e) {
                    Log.e(NEWS_ACTIVITY_TAG, e.getMessage());
                }
            } else {
                imageUrl = getString(R.string.no_picture);
            }
            String shareContent = getString(R.string.title) + ":" + news.getTitle() + " "
                    + getString(R.string.source) + ":" + news.getSourceURL() + " " +
                    getString(R.string.picture) + ":" + imageUrl;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
            return true;
        } else if (id == R.id.action_favorites) {
            if (news.isFavorite()) { // change to not favorite
                item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white_48dp));
                Toast.makeText(getApplicationContext(), getString(R.string.remove_from_favorites), Toast.LENGTH_LONG).show();
                List<News> tempNewsList = Select.from(News.class).where(Condition.prop("news_id").eq(news.getNewsId())).list();
                if (tempNewsList.size() != 0) { // actually it should be 1
                    News tempNews = tempNewsList.get(0);
                    tempNews.delete();
                    news.toggleFavorite();
                    news.save();
                } else {
                    Log.e(NEWS_ACTIVITY_TAG, "No news found in database");
                }
            } else { // change to favorite
                item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_48dp));
                Toast.makeText(getApplicationContext(), getString(R.string.add_to_favorites), Toast.LENGTH_LONG).show();
                List<News> tempNewsList = Select.from(News.class).where(Condition.prop("news_id").eq(news.getNewsId())).list();
                if (tempNewsList.size() != 0) { // actually it should be 1
                    News tempNews = tempNewsList.get(0);
                    tempNews.delete();
                    news.toggleFavorite();
                    news.save();
                } else {
                    Log.e(NEWS_ACTIVITY_TAG, "No news found in database");
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}