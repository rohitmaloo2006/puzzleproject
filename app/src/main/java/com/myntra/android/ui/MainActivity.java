package com.myntra.android.ui;

import java.util.ArrayList;
import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myntratutorial.R;
import com.myntra.android.service.PhotoService;
import com.myntra.android.util.Utils;

public class MainActivity extends Activity {
    private GridView gridview;
    private ArrayList<String> mImageURLList;
    private ArrayList<Bitmap> mBitmapList;
    private ProgressDialog progressDialog;
    private ImageView desiredImage;
    private Handler mHandler = new Handler();
    private CustomGridPhotoAdapter mAdapter;
    private int mRandomPosition;
    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRandom = new Random();
        gridview = (GridView) findViewById(R.id.grid);
        desiredImage = (ImageView) findViewById(R.id.desired_image);
        mBitmapList = new ArrayList<Bitmap>();
        mAdapter = new CustomGridPhotoAdapter(MainActivity.this);
        gridview.setAdapter(mAdapter);

        start();
    }

    private void start() {
        desiredImage.setImageResource(R.drawable.photo_unavailable_m);
        gridview.setOnItemClickListener(null);
        if (Utils.isConnectedToInternet(MainActivity.this)) {
            mAdapter.setData(null);
            mAdapter.notifyDataSetInvalidated();
            new PhotoLoadAsync().execute();
        } else {
            Toast.makeText(
                    MainActivity.this,
                    getResources()
                            .getString(R.string.check_internet_connection),
                    Toast.LENGTH_LONG).show();
        }
    }

    private class PhotoLoadAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            PhotoService photoService = new PhotoService();
            mImageURLList = photoService.getPhotoFromServer();
            mBitmapList.clear();
            for (String URL : mImageURLList) {
                Bitmap bitmap = Utils.downloadBitmap(URL);
                mBitmapList.add(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.drawable.no_image) : bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception e) {
                progressDialog = null;
                // TODO: handle exception
            }
            mAdapter.setData(mBitmapList);
            mAdapter.notifyDataSetChanged();

            if (mImageURLList.size() <= 0) {
                Toast.makeText(MainActivity.this, "Unable to Load data",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(MainActivity.this, "Images will be displayed for 15 seconds only",
                        Toast.LENGTH_LONG).show();
            }

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getRandomImageToDisplay();
                    mAdapter.hide(true);
                    mAdapter.notifyDataSetChanged();
                    gridview.setOnItemClickListener(mGridItemClickListener);
                }
            }, 15000);

            // TODO get the Random position

        }
    }

    private void getRandomImageToDisplay() {
        ArrayList<Integer> excludeList = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (mAdapter.getRecognized(i)) {
                excludeList.add(i);
            }
        }
        mRandomPosition = getRandomInt(excludeList);

        desiredImage.setImageBitmap(mBitmapList.get(mRandomPosition));
    }

    private int getRandomInt(ArrayList<Integer> excludeList) {
        int randomInt = mRandom.nextInt(9);
        if (excludeList.contains(randomInt)) {
            randomInt = getRandomInt(excludeList);
        }

        return randomInt;
    }

    private OnItemClickListener mGridItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            if (mAdapter.getRecognized(position)) {
                return;
            }

            final ImageView image = (ImageView) view.findViewById(R.id.grid_image);
            final ImageView backImage = (ImageView) view.findViewById(R.id.back_image);
            Animator rotate_anim1 = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.flip1);
            rotate_anim1.setTarget(backImage);
            rotate_anim1.setDuration(1000);
            rotate_anim1.start();
            Animator rotate_anim2 = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.flip2);
            rotate_anim2.setTarget(image);
            rotate_anim2.setDuration(1000);
            rotate_anim2.start();


            if (mRandomPosition == position) {
                mAdapter.setRecognized(position);
                //mAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Recognize Success",
                        Toast.LENGTH_SHORT).show();
                boolean allRecognized = true;
                for (int i = 0; i < 9; i++) {
                    if (!mAdapter.getRecognized(i)) {
                        allRecognized = false;
                        break;
                    }
                }
                if (allRecognized) {
                    Toast.makeText(MainActivity.this, "Congratulation!!! You Win!!!",
                            Toast.LENGTH_SHORT).show();
                    start();
                } else {
                    getRandomImageToDisplay();
                }

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animator rotate_anim3 = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.flip3);
                        rotate_anim3.setTarget(backImage);
                        rotate_anim3.setDuration(1000);
                        rotate_anim3.start();
                        Animator rotate_anim4 = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.flip4);
                        rotate_anim4.setTarget(image);
                        rotate_anim4.setDuration(1000);
                        rotate_anim4.start();
                    }
                }, 1500);

                Toast.makeText(MainActivity.this, "Wrong image. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
