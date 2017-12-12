package tp_android.tp_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.content.SharedPreferences;

public class GalleryItemViewActivity extends AppCompatActivity {

    private Context context;
    private LinearLayout mLinearLayout;
    private ImageView imageView;
    private Button sendButton;

    private Bitmap photo;
    private String path;

    public static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private int comprimation;
    private boolean colored;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item_view);
        Intent intent = getIntent();
        context = getApplicationContext();
        path = intent.getStringExtra("path");

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getInt("comprimation", 100);
        colored = prefs.getBoolean("colored",true);
        user = prefs.getString("user","");


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
                ApiPostRequest.PostApi1(photo, comprimation, user, GalleryItemViewActivity.this, context, mLinearLayout);
            }
        });

    }

}
