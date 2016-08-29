package com.ihandy.a2014011385.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihandy.a2014011385.R;
import com.ihandy.a2014011385.helpers.News;

import java.util.ArrayList;

/**
 * Created by huangruihao on 16-8-29.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter {
    TextView titleTextView;
    TextView timeTextView;
    TextView sourceTextView;
    ImageView imageView;

    class NewsViewHolder extends RecyclerView.ViewHolder {
        public NewsViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            sourceTextView = (TextView) itemView.findViewById(R.id.source);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    private Context context;
    private ArrayList<News> newsArrayList;
    public NewsRecyclerAdapter(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
        return new NewsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        News news = newsArrayList.get(position);
        titleTextView.setText(news.getTitle());
        timeTextView.setText(String.valueOf(news.getFetchedTime()));
        sourceTextView.setText(news.getSourceURL());
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_camera));
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }
}
