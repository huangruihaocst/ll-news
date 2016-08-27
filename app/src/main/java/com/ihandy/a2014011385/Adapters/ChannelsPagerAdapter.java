package com.ihandy.a2014011385.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by huangruihao on 16-8-26.
 */
public class ChannelsPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] channels;

    public  ChannelsPagerAdapter(Context context, String[] channels){
        this.context = context;
        this.channels = channels;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return channels[pos];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int pos) {
        TextView tv = new TextView(context);
        tv.setText(String.valueOf(pos));
        tv.setTextSize(50.0f);
        container.addView(tv);

        return tv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return channels.length;
    }
}
