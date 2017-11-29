package tp_android.tp_android;

import android.content.Intent;
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
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


@SuppressWarnings("deprecation")
public class GalleryActivity extends Activity {
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    List<String> DCMIArray = new ArrayList<String>();
    private String url = "http://10.0.2.2:8080/";
    //private String url = "http://10.10.53.212:8080/";
    private String encodedImage;

    private Integer positionn;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        DCMIArray = getCameraImages(this);
        button = (Button) this.findViewById(R.id.button2);


        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id)
            {
                //Toast.makeText(getBaseContext(), DCMIArray.get(position),
                //        Toast.LENGTH_SHORT).show();
                button.setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView) findViewById(R.id.image1);
                imageView.setImageBitmap(decodeSampledBitmapFromResource(DCMIArray.get(position), 200, 200));
                positionn = position;
            }
        });

    }
    public void PostImage2(View view) {
        Log.d("l",DCMIArray.toString());
        Log.d("p",DCMIArray.get(positionn));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(DCMIArray.get(positionn));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                /*new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", response);
                    }
                },*/
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("res",response);
                        vypis(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("name", "Martin");
                params.put("name", "Martin");
                Log.d("name", encodedImage);
                params.put("image", encodedImage);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void vypis(String response) {
        String[] numberOfResults = response.split("_____");
        Log.d("pocet", Integer.toString(numberOfResults.length));

        if(numberOfResults.length > 1){
            Intent intent = new Intent(this, ResponceListActivity.class);
            intent.putExtra("response", response);
            intent.putExtra("image", encodedImage);
            startActivity(intent);
        }
        else {
            String[] splitresponse = response.split("=====");
            Log.d("pocet", Integer.toString(splitresponse.length));
            Log.d("respon", response);
            Log.d("Response", splitresponse[0] + splitresponse[1]);
            Log.d("Znacka", splitresponse[2]);
            Log.d("poistenie", splitresponse[3]);
            Log.d("STK, EK", splitresponse[4]);
            Log.d("Znacka", splitresponse[5]);
            Log.d("Model", splitresponse[6]);
            Log.d("Rok vyroby", splitresponse[7]);

            Intent intent = new Intent(this, ListItemActivity.class);
            intent.putExtra("response", response);
            intent.putExtra("image", encodedImage);
            startActivity(intent);
        }
    }

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static Bitmap decodeSampledBitmapFromResource(String resId,int reqWidth, int reqHeight) {

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
        final String[] projection = { MediaStore.Images.Media.DATA };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
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
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
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
