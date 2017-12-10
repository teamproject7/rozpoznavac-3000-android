package tp_android.tp_android;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import android.content.SharedPreferences;

public class CapturephotoActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Bitmap photo;
    private Bitmap photoSend;
    private Button sendButton;
    private Context context;
    private boolean imageInit = false;
    private LinearLayout mLinearLayout;
    private String url = "http://108.61.179.124:80/spz_img/";
    private int comprimation;
    private boolean colored;
    private boolean saving;
    public static final String MY_PREFS_NAME = "Setting";
    private Database db;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturephoto);
        mLinearLayout = (LinearLayout) findViewById(R.id.rl_captured_photo);
        context = getApplicationContext();
		db = new Database(this);
        db.open();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getInt("comprimation", 100);
        colored = prefs.getBoolean("colored",true);
        saving = prefs.getBoolean("saving", true);

        imageView = (ImageView)this.findViewById(R.id.imageView1);
        sendButton = (Button) findViewById(R.id.button2);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostImage();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageInit==true) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                    //photoSend = Bitmap.createBitmap(photoSend, 0, 0, photoSend.getWidth(), photoSend.getHeight(), matrix, true);
                    imageView.setImageBitmap(photo);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            sendButton.setVisibility(View.VISIBLE);

            Bundle extras = data.getExtras();
            photoSend = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoSend.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //photoSend = BitmapHelper.decodeByteArrayMaxSize(byteArray,32,0.9);

            if(colored==true){
                photo = BitmapHelper.decodeByteArrayMaxSize(byteArray,32,0.9);
            }
            else{
                photo = BitmapHelper.toGrayscale(BitmapHelper.decodeByteArrayMaxSize(byteArray,32,0.9));
            }


            imageView.setImageBitmap(photo);
            imageInit = true;
        }
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
                        ResponceDecision.Responce(response.toString(),encodedImage, CapturephotoActivity.this);

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rs.stop();
                ResponceDecision.notResponce("problem", CapturephotoActivity.this);
            }
        });
        queue.add(postRequest);
        rs.stopButton(queue);


    }
            db.addRecord(new Date(), splitresponse[2], response, splitresponse[0]);
}