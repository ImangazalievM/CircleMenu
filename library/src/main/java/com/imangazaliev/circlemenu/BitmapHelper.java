package com.imangazaliev.circlemenu;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * @author NetoDevel
 */
public class BitmapHelper {

    public static void transformCircularBitmap(Canvas canvas, Bitmap newBitmap) {
        BitmapShader shader;
        shader = new BitmapShader(newBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        float r = newBitmap.getWidth() / 2f;

        canvas.drawCircle(r, r, r, paint);
    }

    public static RectF createRectFFromBitmap(Bitmap bitmap, int mBorderWidth) {
        RectF fBounds = new RectF();

        int offset = (bitmap.getWidth() - bitmap.getHeight()) / 2;

        int left = offset + mBorderWidth;
        int right = bitmap.getWidth() - offset - mBorderWidth;
        int bottom = bitmap.getHeight() - mBorderWidth;
        int top = mBorderWidth;

        fBounds.left = left + mBorderWidth / 2f + .5f;
        fBounds.right = right - mBorderWidth / 2f - .5f;
        fBounds.top = top + mBorderWidth / 2f + .5f;
        fBounds.bottom = bottom - mBorderWidth / 2f - .5f;

        return fBounds;
    }

    public static Bitmap resizeBitmap(Bitmap source, int width, int height) {
        return Bitmap.createScaledBitmap(source, width, height, false);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}


