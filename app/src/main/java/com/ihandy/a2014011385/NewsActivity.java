package com.ihandy.a2014011385;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.ihandy.a2014011385.helpers.CallBack;
import com.ihandy.a2014011385.helpers.DataAccessor;
import com.ihandy.a2014011385.helpers.News;

public class NewsActivity extends AppCompatActivity {

    private final String NEWS_ACTIVITY_TAG = "NewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        News news = (News) data.getSerializable(getString(R.string.key_main_news));

        if (news != null) {
            toolbar.setTitle(news.getTitle()); // set title
            setSupportActionBar(toolbar);

            final WebView contentWebView = (WebView) findViewById(R.id.content);
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

            final ImageView image = (ImageView) findViewById(R.id.image);
            if (news.getImageURLs() == null) {
                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_error_black_48dp));
            } else {
                DataAccessor accessor = DataAccessor.getInstance();
                accessor.setContext(getApplicationContext());
                accessor.getImage(news.getImageURLs()[0], new CallBack<ImageLoader.ImageContainer>() {
                    @Override
                    public void onCallBack(ImageLoader.ImageContainer response) {
                        image.setImageBitmap(response.getBitmap());
                    }
                });
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
            }
        });

    }
}