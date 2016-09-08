package com.ihandy.a2014011385.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihandy.a2014011385.NewsActivity;
import com.ihandy.a2014011385.R;
import com.ihandy.a2014011385.fragments.NewsListFragment;
import com.ihandy.a2014011385.helpers.CallBack;
import com.ihandy.a2014011385.helpers.Category;
import com.ihandy.a2014011385.helpers.DataAccessor;
import com.ihandy.a2014011385.helpers.News;
import com.ihandy.a2014011385.helpers.ParseHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangruihao on 16-8-29.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter {
    TextView titleTextView;
    TextView timeTextView;
    TextView sourceTextView;
    ImageView imageView;
    ArrayList<String> filters;
    String content = "";

    private final String NEWS_RECYCLER_VIEW_ADAPTER_TAG = "NewsRecyclerAdapter";
    private final int GET_HTML_WHAT = 0;

    class NewsViewHolder extends RecyclerView.ViewHolder {
        public NewsViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            sourceTextView = (TextView) itemView.findViewById(R.id.source);
            imageView = (ImageView) itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable(context.getString(R.string.key_main_news),
                            newsArrayList.get(getAdapterPosition()));
                    intent.putExtras(data);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    private Context context;
    private ArrayList<News> newsArrayList;

    public NewsRecyclerAdapter(Context context, ArrayList<News> newsArrayList, ArrayList<String> filters) {
        this.context = context;
        this.newsArrayList = newsArrayList;
        this.filters = filters;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
        return new NewsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        News news = newsArrayList.get(position);
        titleTextView.setText(news.getTitle());
        Date date = new Date(news.getUpdatedTime());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        timeTextView.setText(formatter.format(date));
        sourceTextView.setText(news.getSourceName());
        if (news.getImageURLsJSON() != null) {
            Picasso.with(context).load(news.getFirstImageUrl()).into(imageView);
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_black_48dp));
        }
        if (filters != null && filters.size() > 0) {
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case GET_HTML_WHAT:
                            content = ParseHelper.getContentFromHtml(content);
                            ArrayList<String> words = new ArrayList<>();
                            Pattern pattern = Pattern.compile("(\\w+)");
                            Matcher matcher = pattern.matcher(content);
                            while (matcher.find()) {
                                String word = matcher.group();
                                words.add(word);
                            }
                            for (String filer: filters) {
                                int frequency = Collections.frequency(words, filer);
                                Log.d(NEWS_RECYCLER_VIEW_ADAPTER_TAG, "reach here");
                                if (frequency > context.getResources().getInteger(R.integer.filter_threshold)) {
                                    holder.itemView.setBackgroundColor(Color.RED);
                                    break;
                                } else {
                                    holder.itemView.setBackgroundColor(Color.WHITE);
                                }
                            }
                            break;
                        default:
                            // nothing
                    }
                }
            };
            DataAccessor accessor = DataAccessor.getInstance();
            accessor.setContext(context);
            accessor.simplifyWebsite(news.getSourceURL(), new CallBack<String>() {
                @Override
                public void onCallBack(String response) {
                    if (response != null) { // equaling null means failing
                        content = response;
                        Message message = new Message();
                        message.what = GET_HTML_WHAT;
                        handler.sendMessage(message);
                    }
                }
            });
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setFilters(ArrayList<String> filters) {
        if (filters != null) {
            this.filters = new ArrayList<>();
            for (String filter: filters) {
                this.filters.add(filter);
            }
        }
        notifyDataSetChanged();
    }
}
