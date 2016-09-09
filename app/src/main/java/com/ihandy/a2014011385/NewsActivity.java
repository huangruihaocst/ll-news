package com.ihandy.a2014011385;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.ihandy.a2014011385.helpers.CallBack;
import com.ihandy.a2014011385.helpers.DataAccessor;
import com.ihandy.a2014011385.helpers.News;
import com.ihandy.a2014011385.helpers.ParseHelper;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {

    private final String NEWS_ACTIVITY_TAG = "NewsActivity";

    private final int SIMPLIFY_SUCCESS_WHAT = 0;
    private final int ONLY_GET_HTML_WHAT = 1;

    private final String APP_ID = "wxee23946817108b5f";
    private IWXAPI api;

    Handler handler = new Handler();

    TextToSpeech tts;
    boolean ttsIsWorking = false;

    News news;
    String html = "";
    String content = "";
    WebView contentWebView;

    enum Mode{
        Original,
        Reading
    }

    Mode mode = Mode.Original;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        news = (News) data.getSerializable(getString(R.string.key_main_news));

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SIMPLIFY_SUCCESS_WHAT:
                        contentWebView.getSettings().setJavaScriptEnabled(true);
                        contentWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                        mode = Mode.Reading;
                        Toast.makeText(getApplicationContext(), getString(R.string.transfer_to_reading), Toast.LENGTH_LONG).show();
                        break;
                    case ONLY_GET_HTML_WHAT:
                        content = ParseHelper.getContentFromHtml(html);
                        tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
                        ttsIsWorking = true;
                        break;
                    default:
                        // nothing
                }
            }
        };

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

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
                if (mode == Mode.Original) {
                    if (html.equals("")) {
                        DataAccessor accessor = DataAccessor.getInstance();
                        accessor.setContext(getApplicationContext());
                        accessor.simplifyWebsite(news.getSourceURL(), new CallBack<String>() {
                            @Override
                            public void onCallBack(String response) {
                                if (response != null) { // equaling null means failing
                                    html = response;
                                    Message message = new Message();
                                    message.what = SIMPLIFY_SUCCESS_WHAT;
                                    handler.sendMessage(message);
                                }
                            }
                        });
                    } else {
                        Message message = new Message();
                        message.what = SIMPLIFY_SUCCESS_WHAT;
                        handler.sendMessage(message);
                    }
                } else { // reading mode
                    contentWebView.loadUrl(news.getSourceURL()); // set content
                    mode = Mode.Original;
                    Toast.makeText(getApplicationContext(), getString(R.string.transfer_to_original), Toast.LENGTH_LONG).show();
                }

            }
        });

        // register WeChat
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
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
        // Sharing is really simple and not easy to use:
        // If WeChat is installed, only share through WeChat, else share plain text
        // Do not support a list for user to select the app to share to
        // The thumb is a fixed image and can only share to friends
        // What is even worse, I did not have a Android phone when I write this part of code, so I did not checked out if it works
        // It is likely to crash or has no reaction. lol
        // Sharing is only a demo, to show that my app offers this function and I know how to use WeChat API
        if (id == R.id.action_share) {
            if (news.getSourceURL() != null) {
                if (api.isWXAppInstalled()) {
                    WXWebpageObject page = new WXWebpageObject();
                    page.webpageUrl = news.getSourceURL();
                    WXMediaMessage message = new WXMediaMessage(page);
                    message.title = news.getTitle();
                    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_error_black_48dp);
                    message.setThumbImage(thumb);
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = message;
                    req.scene = 1; // to friends
                    api.sendReq(req);
                } else {
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
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.url_is_null), Toast.LENGTH_LONG).show();
            }

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
        } else if (id == R.id.action_tts) {
            if (html.equals("")) { // if not simplified yet, first simplify
                DataAccessor accessor = DataAccessor.getInstance();
                accessor.setContext(getApplicationContext());
                accessor.simplifyWebsite(news.getSourceURL(), new CallBack<String>() {
                    @Override
                    public void onCallBack(String response) {
                        if (response != null) { // equaling null means failing
                            html = response;
                            Message message = new Message();
                            message.what = ONLY_GET_HTML_WHAT;
                            handler.sendMessage(message);
                        }
                    }
                });
            } else {
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null);
                ttsIsWorking = true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            ttsIsWorking = false;
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (ttsIsWorking) {
            ttsIsWorking = false;
            tts.stop();
        } else {
            super.onBackPressed();
        }
    }
}