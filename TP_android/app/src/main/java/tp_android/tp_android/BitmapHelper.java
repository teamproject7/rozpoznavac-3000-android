package tp_android.tp_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

public abstract class BitmapHelper {

    public static Bitmap decodePathMaxSize(String path, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeFile(path);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        float ratio = Math.min(
                    (float) maxImageSize / image.getWidth(),
                    (float) maxImageSize / image.getHeight());
            int width = Math.round((float) ratio * image.getWidth());
            int height = Math.round((float) ratio * image.getHeight());


        if((width>image.getWidth()) || (height>image.getHeight())){
            return Bitmap.createScaledBitmap(image, image.getWidth(),image.getHeight(),true);
        }

        return Bitmap.createScaledBitmap(image, width,height,true);
    }

    public static Bitmap decodeByteArrayMaxSize(byte[] byteArray, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        float ratio = Math.min(
                (float) maxImageSize / image.getWidth(),
                (float) maxImageSize / image.getHeight());
        int width = Math.round((float) ratio * image.getWidth());
        int height = Math.round((float) ratio * image.getHeight());


        if((width>image.getWidth()) || (height>image.getHeight())){
            return Bitmap.createScaledBitmap(image, image.getWidth(),image.getHeight(),true);
        }

        return Bitmap.createScaledBitmap(image, width,height,true);
    }

    public static float decodePathMaxSizeRatio(String path, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeFile(path);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        float ratio = Math.min(
                (float) maxImageSize / image.getWidth(),
                (float) maxImageSize / image.getHeight());
        int width = Math.round((float) ratio * image.getWidth());
        int height = Math.round((float) ratio * image.getHeight());

        if((width>image.getWidth()) || (height>image.getHeight())){
            return 1;
        }

        return ratio;
    }

    public static float decodeByteArrayMaxSizeRatio(byte[] byteArray, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        float ratio = Math.min(
                (float) maxImageSize / image.getWidth(),
                (float) maxImageSize / image.getHeight());
        int width = Math.round((float) ratio * image.getWidth());
        int height = Math.round((float) ratio * image.getHeight());

        if((width>image.getWidth()) || (height>image.getHeight())){
            return 1;
        }

        return ratio;
    }

    public static Bitmap decodeBitmapFromFile(String imagePath,
                                              int reqWidth,
                                              int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Bitmap decodeBitmapFromFile2(byte[] image,
                                              int reqWidth,
                                              int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image,0,image.length,options);

        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image,0,image.length,options);
    }

    private static int calculateSampleSize(BitmapFactory.Options options,
                                           int reqHeight,
                                           int reqWidth) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
