package com.ihandy.a2014011385;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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

import com.ihandy.a2014011385.helpers.News;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private final String NEWS_ACTIVITY_TAG = "NewsActivity";

    News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        news = (News) data.getSerializable(getString(R.string.key_main_news));

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

            ImageView imageView = (ImageView) findViewById(R.id.image);
            if (news.getImageURLsJSON() != null) {
                Picasso.with(getApplicationContext()).load(news.getFirstImageUrl()).into(imageView);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.ic_error_black_48dp));
            }

//            TextView textView = (TextView) findViewById(R.id.html);
//            Spanned result;
//            String roughHtml = "<div class=\"x-wiki-content\"><span>\n" +
//                    "        </span><p>这是小白的Python新手教程，具有如下特点：</p><span>\n" +
//                    "</span><span>\n" +
//                    "</span><p>Python是一种计算机程序设计语言。你可能已经听说过很多种流行的编程语言，比如非常难学的C语言，非常流行的Java语言，适合初学者的Basic语言，适合网页编程的JavaScript语言等等。</p><span>\n" +
//                    "</span><p>那Python是一种什么语言？</p><span>\n" +
//                    "</span><p>首先，我们普及一下编程语言的基础知识。用任何编程语言来开发程序，都是为了让计算机干活，比如下载一个MP3，编写一个文档等等，而计算机干活的CPU只认识机器指令，所以，尽管不同的编程语言差异极大，最后都得“翻译”成CPU可以执行的机器指令。而不同的编程语言，干同一个活，编写的代码量，差距也很大。</p><span>\n" +
//                    "</span><p>比如，完成同一个任务，C语言要写1000行代码，Java只需要写100行，而Python可能只要20行。</p><span>\n" +
//                    "</span><p>所以Python是一种相当高级的语言。</p><span>\n" +
//                    "</span><p>你也许会问，代码少还不好？代码少的代价是运行速度慢，C程序运行1秒钟，Java程序可能需要2秒，而Python程序可能就需要10秒。</p><span>\n" +
//                    "</span><p>那是不是越低级的程序越难学，越高级的程序越简单？表面上来说，是的，但是，在非常高的抽象计算中，高级的Python程序设计也是非常难学的，所以，高级程序语言不等于简单。</p><span>\n" +
//                    "</span><p>但是，对于初学者和完成普通任务，Python语言是非常简单易用的。连Google都在大规模使用Python，你就不用担心学了会没用。</p><span>\n" +
//                    "</span><p>用Python可以做什么？可以做日常任务，比如自动备份你的MP3；可以做网站，很多著名的网站包括YouTube就是Python写的；可以做网络游戏的后台，很多在线游戏的后台都是Python开发的。总之就是能干很多很多事啦。</p><span>\n" +
//                    "</span><p>Python当然也有不能干的事情，比如写操作系统，这个只能用C语言写；写手机应用，只能用Swift/Objective-C（针对iPhone）和Java（针对Android）；写3D游戏，最好用C或C++。</p><span>\n" +
//                    "</span><p>如果你是小白用户，满足以下条件：</p><span>\n" +
//                    "</span><ul>\n" +
//                    "    <li>会使用电脑，但从来没写过程序；</li>\n" +
//                    "    <li>还记得初中数学学的方程式和一点点代数知识；</li>\n" +
//                    "    <li>想从编程小白变成专业的软件架构师；</li>\n" +
//                    "    <li>每天能抽出半个小时学习。</li>\n" +
//                    "</ul><span>\n" +
//                    "</span><p>不要再犹豫了，这个教程就是为你准备的！</p><span>\n" +
//                    "</span><p>准备好了吗？</p><span>\n" +
//                    "</span><p><img src=\"http://www.liaoxuefeng.com/files/attachments/00138676512923004999ceca5614eb2afc5c0efdd2e4640000/0\" alt=\"challenge-accepted\"></p><span>\n" +
//                    "</span><h3 id=\"-\">关于作者</h3><span>\n" +
//                    "</span><p><a href=\"http://weibo.com/liaoxuefeng\">廖雪峰</a>，十年软件开发经验，业余产品经理，精通Java/Python/Ruby/Scheme/Objective C等，对开源框架有深入研究，著有《Spring 2.0核心技术与最佳实践》一书，多个业余开源项目托管在<a href=\"https://github.com/michaelliao\">GitHub</a>，欢迎微博交流：</p><span>\n" +
//                    "</span><p><a href=\"http://weibo.com/u/1658384301?s=6uyXnP\" target=\"_blank\"><img border=\"0\" src=\"http://service.t.sina.com.cn/widget/qmd/1658384301/078cedea/2.png\"></a></p><span>\n" +
//                    "\n" +
//                    "    </span></div>";
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                result = Html.fromHtml(roughHtml, Html.FROM_HTML_MODE_LEGACY);
//            } else {
//                result = Html.fromHtml(roughHtml);
//            }
//            textView.setMovementMethod(LinkMovementMethod.getInstance());
//            textView.setText(result);
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