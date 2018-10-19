package com.androthink.masker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

class ImageUtils {

    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);

        if (!bm.isRecycled()) {
            bm.recycle();
        }

        return resizedBitmap;
    }

    static Bitmap applyMask(Context context, Bitmap mainImage, int maskDrawableId, int maskBorderDrawableId){
        Canvas canvas = new Canvas();

        Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mask_background);
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), maskDrawableId);

        Bitmap maskBorder = null;

        if(maskBorderDrawableId != -1) {
            maskBorder = BitmapFactory.decodeResource(context.getResources(),maskBorderDrawableId);
        }

        mainImage = ImageUtils.getResizedBitmap(mainImage,mask.getWidth(),mask.getHeight());
        background = ImageUtils.getResizedBitmap(background,mask.getWidth(),mask.getHeight());

        Bitmap result = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);

        canvas.setBitmap(result);
        Paint paint = new Paint();
        paint.setFilterBitmap(false);

        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawBitmap(mainImage, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        if(maskBorder != null) {
            canvas.drawBitmap(maskBorder, 0, 0, paint);
        }
        return result;
    }
}
