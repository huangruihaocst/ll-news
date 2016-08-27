package com.ihandy.a2014011385;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.ihandy.a2014011385.Helpers.BundleHelper;
import com.ihandy.a2014011385.Helpers.DataAccessor;
import com.ihandy.a2014011385.Helpers.News;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        BundleHelper bundleHelper = (BundleHelper) data.getSerializable(getString(R.string.key_main_news));
        if (bundleHelper != null) {
            long id = bundleHelper.getID();
            News news = DataAccessor.getInstance().getNews(id);
            setView(news);
        } else {
            System.out.println("BundleHelper is null");
        }
    }

    void setView(News news) {
        // TODO: set view according to news
    }
}