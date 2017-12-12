package tp_android.tp_android;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.BitmapFactory;
import android.content.SharedPreferences;

public class GalleryItemViewActivity extends AppCompatActivity {

    private Context context;
    private String url = "http://108.61.179.124:7486/spz_img/";
    private ImageView imageView;
    private Button sendButton;
    private Bitmap photo;
    private Bitmap photoSend;
    private String path;
    private LinearLayout mLinearLayout;
    public static final String MY_PREFS_NAME = "Setting";
    private int comprimation;
    private boolean colored;
    private boolean saving;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item_view);
        Intent intent = getIntent();
        context = getApplicationContext();
        path = intent.getStringExtra("path");

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getInt("comprimation", 100);
        colored = prefs.getBoolean("colored",true);
        saving = prefs.getBoolean("saving", true);



        mLinearLayout = (LinearLayout) findViewById(R.id.rl_galelry_item_view);
        imageView = (ImageView) findViewById(R.id.image1);
        sendButton = (Button) findViewById(R.id.send);
        //photoSend = BitmapHelper.decodePathMaxSize(path,24,0.9);
        if(colored==true){
            photo = BitmapHelper.decodePathMaxSize(path,32,0.9);
        }
        else{
            photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,0.9));
        }

        imageView.setImageBitmap(photo);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                //photoSend = Bitmap.createBitmap(photoSend, 0, 0, photoSend.getWidth(), photoSend.getHeight(), matrix, true);
                imageView.setImageBitmap(photo);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostImage();
            }
        });

    }


    private void PostImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapHelper.toGrayscale(photo).compress(Bitmap.CompressFormat.JPEG, comprimation, baos);
        final byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Majo");
        params.put("image", encodedImage);


        final RequestQueue queue = Volley.newRequestQueue(this);
        final RequestSending rs = new RequestSending(context, this, mLinearLayout);

        JsonObjectRequest postRequest= new JsonObjectRequest(Request.Method.POST, url,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        rs.stop();
                        ResponceDecision.Responce(response.toString(),encodedImage, GalleryItemViewActivity.this);

                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rs.stop();
                        ResponceDecision.notResponce("problem", GalleryItemViewActivity.this);
                    }
        });
        queue.add(postRequest);
        rs.stopButton(queue);


    }

}
