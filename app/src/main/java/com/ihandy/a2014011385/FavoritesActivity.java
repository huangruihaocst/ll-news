package com.ihandy.a2014011385;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ihandy.a2014011385.adapters.NewsRecyclerAdapter;
import com.ihandy.a2014011385.helpers.News;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.action_favorites));
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView favoritesRecyclerView = (RecyclerView) findViewById(R.id.favorites_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        favoritesRecyclerView.setLayoutManager(layoutManager);
        List<News> favoriteNewsList = Select.from(News.class).where(Condition.prop("favorite").eq(1)).list();
        NewsRecyclerAdapter adapter = new NewsRecyclerAdapter(getApplicationContext(), new ArrayList<>(favoriteNewsList), null);
        favoritesRecyclerView.setAdapter(adapter);
    }
}
