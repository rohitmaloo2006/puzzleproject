package com.myntra.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.myntratutorial.R;

public class CustomGridPhotoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Bitmap> mBitmapList;
    private boolean misHide;
    private boolean[] mRecognized = new boolean[9];
    private LayoutInflater mLayoutInflater;

    public CustomGridPhotoAdapter(Context c) {
        mContext = c;
        mLayoutInflater = LayoutInflater.from(c);
    }

    public void setRecognized(int pos) {
        mRecognized[pos] = true;
    }

    public boolean getRecognized(int pos) {
        return mRecognized[pos];
    }

    public void hide(boolean isHide) {
        misHide = isHide;
    }

    public void setData(ArrayList<Bitmap> bitmapList) {
        mBitmapList = bitmapList;
        for (int i = 0; i < 9; i++) {
            mRecognized[i] = false;
        }
        misHide = false;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mBitmapList == null ? 0 : mBitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View view;
        ImageView imageView, backImage;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.grid_row, parent, false);
        } else {
            view = convertView;
        }
        imageView = (ImageView) view.findViewById(R.id.grid_image);
        backImage = (ImageView) view.findViewById(R.id.back_image);
        backImage.setImageResource(R.drawable.photo_unavailable_m);
        imageView.setImageBitmap(mBitmapList.get(position));
        if (misHide && !mRecognized[position]) {
            backImage.setAlpha(1f);
            imageView.setAlpha(0f);
        } else {
            backImage.setAlpha(0f);
            imageView.setAlpha(1f);
        }
        return view;
    }
}