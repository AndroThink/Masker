package com.androthink.masker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Masker {

    private Context context;
    private String imageUrl;
    private Bitmap sourceBitmapImage;
    private ImageView imageView;

    private int maskDrawableId,maskBorderDrawableId;

    @NonNull
    public static Masker getInstance(Context context, int maskDrawableId){
        return new Masker(context,maskDrawableId);
    }

    @NonNull
    public static Masker getInstance(Context context, int maskDrawableId, int maskBorderDrawableId){
        return new Masker(context,maskDrawableId,maskBorderDrawableId);
    }

    private Masker(Context context, int maskDrawableId){
        this.context = context;
        this.maskDrawableId = maskDrawableId;
        this.maskBorderDrawableId = -1;
    }

    private Masker(Context context, int maskDrawableId, int maskBorderDrawableId){
        this.context = context;
        this.maskDrawableId = maskDrawableId;
        this.maskBorderDrawableId = maskBorderDrawableId;
    }

    public Masker load(String imageUrl){
        this.imageUrl = imageUrl;
        return this;
    }

    public Masker load(Bitmap bitmap){
        this.imageUrl = null;
        this.sourceBitmapImage = bitmap;
        return this;
    }

    public void into(ImageView imageView){
        if(this.imageUrl == null){
            imageView.setImageBitmap(ImageUtils.applyMask(context,sourceBitmapImage,maskDrawableId,maskBorderDrawableId));
        }else {
            this.imageView = imageView;
            new AsyncGettingBitmapFromUrl().execute(imageUrl);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            imageView.setImageBitmap(ImageUtils.applyMask(context,
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_place_holder),
                    maskDrawableId,maskBorderDrawableId));
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if(bitmap != null){
                imageView.setImageBitmap(ImageUtils.applyMask(context,bitmap,maskDrawableId,maskBorderDrawableId));
            }else {
                imageView.setImageBitmap(ImageUtils.applyMask(context,
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_place_holder),
                        maskDrawableId,maskBorderDrawableId));
            }
        }
    }
}
