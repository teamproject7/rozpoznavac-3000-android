package tp_android.tp_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.FloatProperty;
import android.util.Log;

public abstract class BitmapHelper {

    public static Bitmap decodePathMaxSize(String path, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeFile(path);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        Log.d("first",  Integer.toString(maxImageSize));
        Log.d("x", Integer.toString(image.getWidth()));
        Log.d("y",Integer.toString(image.getHeight()));
        float ratio = Math.min(
                    (float) maxImageSize / image.getWidth(),
                    (float) maxImageSize / image.getHeight());
            int width = Math.round((float) ratio * image.getWidth());
            int height = Math.round((float) ratio * image.getHeight());

        Log.d("second", Integer.toString(width*height));

        if((width>image.getWidth()) || (height>image.getHeight())){
            return Bitmap.createScaledBitmap(image, image.getWidth(),image.getHeight(),true);
        }

        return Bitmap.createScaledBitmap(image, width,height,true);
    }

    public static Bitmap decodeByteArrayMaxSize(byte[] byteArray, int bitDeep, double MBsize) {

        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        int maxImageSize = (int)(Math.sqrt((MBsize*(8.0/bitDeep)*1024.0*1024.0)));
        Log.d("first", Integer.toString(maxImageSize));
        Log.d("x", Integer.toString(image.getWidth()));
        Log.d("y",Integer.toString(image.getHeight()));
        float ratio = Math.min(
                (float) maxImageSize / image.getWidth(),
                (float) maxImageSize / image.getHeight());
        int width = Math.round((float) ratio * image.getWidth());
        int height = Math.round((float) ratio * image.getHeight());

        Log.d("second", Integer.toString(width*height));

        if((width>image.getWidth()) || (height>image.getHeight())){
            return Bitmap.createScaledBitmap(image, image.getWidth(),image.getHeight(),true);
        }

        return Bitmap.createScaledBitmap(image, width,height,true);
    }

    public static Bitmap decodeBitmapFromFile(String imagePath,
                                              int reqWidth,
                                              int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Bitmap decodeBitmapFromFile2(byte[] image,
                                              int reqWidth,
                                              int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image,0,image.length,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image,0,image.length,options);
    }


    private static int calculateSampleSize(BitmapFactory.Options options,
                                           int reqHeight,
                                           int reqWidth) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
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
