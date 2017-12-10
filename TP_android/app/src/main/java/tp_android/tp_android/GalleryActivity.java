package tp_android.tp_android;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONObject;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


@SuppressWarnings("deprecation")
public class GalleryActivity extends Activity {
    private static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    private static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);
    private TextView myAwesomeTextView;
    private List<String> DCMIArray = new ArrayList<String>();
    //private String url = "http://10.0.2.2:8080/";
    private String url = "http://108.61.179.124:80/spz_img/";
	public Database db;
    private String encodedImage;
    private ImageView imageView;
    private Bitmap photo;
    private Bitmap photoSend;
    private Boolean imageInit = false;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        DCMIArray = getCameraImages(this);
        myAwesomeTextView = (TextView)findViewById(R.id.myAwesomeTextView);
        button = (Button) this.findViewById(R.id.button2);
        db = new Database(this);
        db.open();

        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(getBaseContext(), DCMIArray.get(position), Toast.LENGTH_SHORT).show();
                button.setVisibility(View.VISIBLE);
                imageView = (ImageView) findViewById(R.id.image1);
                imageView.setImageBitmap(decodeSampledBitmapFromResource(DCMIArray.get(position), 200, 200));
                photo = decodeSampledBitmapFromResource(DCMIArray.get(position), 200, 200);
                photoSend = BitmapFactory.decodeFile(DCMIArray.get(position));
                imageInit = true;
            }
        });

    }
    public void Rotate(View view){
        if(imageInit==true) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
            imageView.setImageBitmap(photo);
            photoSend = Bitmap.createBitmap(photoSend, 0, 0, photoSend.getWidth(), photoSend.getHeight(), matrix, true);
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
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

    public void PostImage2(View view) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toGrayscale(photoSend).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Majo");
        params.put("image", encodedImage);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest= new JsonObjectRequest(Request.Method.POST, url,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Response: ",response.toString());
                        skuska(response);
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error: ", error.getMessage());
                    }
                });
        queue.add(postRequest);
    }

    public void skuska(JSONObject response) {
        myAwesomeTextView.setText(response.toString());
    }

    public void vypis(String response) {
        String[] numberOfResults = response.split("_____");
        if (numberOfResults.length > 1) {
            Intent intent = new Intent(this, ResponceListActivity.class);
            intent.putExtra("response", response);
            intent.putExtra("image", encodedImage);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ListItemActivity.class);
            intent.putExtra("response", response);
            intent.putExtra("image", encodedImage);
            startActivity(intent);
        }
    }

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static List<String> getCameraImages(Context context) {
        final String[] projection = {MediaStore.Images.Media.DATA};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};
        final Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;

        public ImageAdapter(Context c) {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        // returns the number of images
        public int getCount() {
            return DCMIArray.size();
        }

        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }

        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }

        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(decodeSampledBitmapFromResource(DCMIArray.get(position), 400, 400));
            imageView.setLayoutParams(new Gallery.LayoutParams(400, 400));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }

}
