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

public class DiamondMask {

    private Context context;
    private String imageUrl;
    private Bitmap sourceBitmapImage;
    private ImageView imageView;

    @NonNull
    public static DiamondMask getInstance(Context context){
        return new DiamondMask(context);
    }

    private DiamondMask(Context context){
        this.context = context;
    }

    public DiamondMask load(String imageUrl){
        this.imageUrl = imageUrl;
        return this;
    }

    public DiamondMask load(Bitmap bitmap){
        this.imageUrl = null;
        this.sourceBitmapImage = bitmap;
        return this;
    }

    public void into(ImageView imageView){
        if(this.imageUrl == null){
            imageView.setImageBitmap(ImageUtils.applyMask(context,sourceBitmapImage,
                    R.drawable.ic_diamond_mask_shape,R.drawable.ic_diamond_mask_border));
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
                    R.drawable.ic_diamond_mask_shape,R.drawable.ic_diamond_mask_border));
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
                imageView.setImageBitmap(ImageUtils.applyMask(context,bitmap,
                        R.drawable.ic_diamond_mask_shape,R.drawable.ic_diamond_mask_border));
            }else {
                imageView.setImageBitmap(ImageUtils.applyMask(context,
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_place_holder),
                        R.drawable.ic_diamond_mask_shape,R.drawable.ic_diamond_mask_border));
            }
        }
    }
}
