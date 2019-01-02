package com.example.android.popularmoviesstage1;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmoviesstage1.MainActivity.baseImageUrl;
import static com.example.android.popularmoviesstage1.MainActivity.urls;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;


    public ImageAdapter(Context c, ArrayList<String> urls) {
        mContext = c;
    }

    public int getCount() {
        return urls.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290, mContext.getResources().getDisplayMetrics());
            imageView.setMinimumHeight(y);
            imageView.setMaxHeight(y);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext)
                .load(baseImageUrl + urls.get(position) )
                .into(imageView);
        return imageView;
    }

}