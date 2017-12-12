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
import android.graphics.Matrix;
import android.widget.LinearLayout;
import android.provider.MediaStore;
import android.content.SharedPreferences;

import java.io.ByteArrayOutputStream;

public class CapturephotoActivity extends Activity {
    private Context context;

    private LinearLayout mLinearLayout;
    private Button sendButton;
    private ImageView imageView;

    private Bitmap photoSend;
    private Bitmap photo;

    private boolean imageInit = false;

    private static final int CAMERA_REQUEST = 1888;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private int comprimation;
    private boolean colored;
    private String user;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturephoto);
        mLinearLayout = (LinearLayout) findViewById(R.id.rl_captured_photo);
        context = getApplicationContext();

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getInt("comprimation", 100);
        colored = prefs.getBoolean("colored",true);
        user = prefs.getString("user","");

        imageView = (ImageView)this.findViewById(R.id.imageView1);
        sendButton = (Button) findViewById(R.id.button2);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiPostRequest.PostApi1(photo, comprimation, user, CapturephotoActivity.this, context, mLinearLayout);
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

}