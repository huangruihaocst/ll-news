package com.ihandy.a2014011385;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.common.base.Joiner;
import com.ihandy.a2014011385.adapters.CategoriesPagerAdapter;
import com.ihandy.a2014011385.fragments.NewsListFragment;
import com.ihandy.a2014011385.helpers.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsListFragment.OnFragmentInteractionListener {

    DataAccessor accessor = DataAccessor.getInstance();
    Category[] categories;
    CategoriesPagerAdapter adapter;
    ViewPager pager;
    TabLayout categoriesTabLayout;
    ArrayList<String> filters;

    private final int GET_CATEGORIES_MESSAGE_WHAT = 0;

    private final String MAIN_ACTIVITY_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.set_filter));
                final EditText editText = new EditText(getApplicationContext());
                if (filters != null && filters.size() != 0) {
                    editText.setText(Joiner.on(' ').join(filters));
                }
                builder.setView(editText);
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filter = editText.getText().toString();
                        if (!filter.equals("")) {
                            String[] temp = filter.split("\\s+");
                            filters = new ArrayList<>(Arrays.asList(temp));
                        } else {
                            filters = new ArrayList<>();
                        }
                        // TODO: still do not know why cannot affect adapters that have not initialized
                        adapter.setFilters(filters);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                });
                builder.create().show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pager = (ViewPager) findViewById(R.id.news_pager);
        adapter = new CategoriesPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        categoriesTabLayout = (TabLayout) findViewById(R.id.categories_tabs);
        categoriesTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        categoriesTabLayout.setupWithViewPager(pager);

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_CATEGORIES_MESSAGE_WHAT:
                        for (Category category: categories) {
                            if (category.subscribing) {
                                adapter.addFragment(NewsListFragment.newInstance(category.name, filters), category);
                            }
                        }
                        break;
                    default:
                        // nothing
                }
            }
        };

        accessor.setContext(getApplicationContext());
        accessor.getCategories(System.currentTimeMillis(), new CallBack<Category[]>() {
            @Override
            public void onCallBack(Category[] response) {
                categories = new Category[response.length];
                for (int i = 0; i < response.length; ++i) {
                    categories[i] = new Category();
                    categories[i].copy(response[i]);
                }
                Message message = new Message();
                message.what = GET_CATEGORIES_MESSAGE_WHAT;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_subscribe) {
            final String[] categoryTitles = new String[categories.length];
            final boolean[] subscribingItems = new boolean[categories.length];
            for (int i = 0; i < categories.length; ++i) {
                categoryTitles[i] = categories[i].title;
                subscribingItems[i] = categories[i].subscribing;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.action_subscribe))
            .setMultiChoiceItems(categoryTitles, subscribingItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        categories[which].subscribing = true;
                    } else {
                        categories[which].subscribing = false;
                    }
                }
            }).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Category.deleteAll(Category.class); // update category information
                    Cache cache = Cache.getInstance();
                    cache.categories = categories;
                    for (Category category: categories) {
                        category.save();
                    }
                    finish();
                    startActivity(getIntent());
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cache cache = Cache.getInstance();
                    for (int i = 0; i < cache.categories.length; ++i) {
                        categories[i].subscribing = cache.categories[i].subscribing;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.action_about));
            builder.setMessage(getString(R.string.info));
            builder.create().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
