package tp_android.tp_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.content.SharedPreferences;
import java.io.File;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Toast;


public class GalleryItemViewActivity extends AppCompatActivity {

    private Context context;
    private CoordinatorLayout mLinearLayout;
    private ImageView imageView;
    private Button sendButton;
    private FloatingActionButton fb;

    private Bitmap photo;
    private Bitmap photo_send;
    private String path;
    private float ratio = 1;
    private float file_length;

    public static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private float comprimation;
    private boolean colored;
    private boolean help;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item_view);
        Intent intent = getIntent();
        context = getApplicationContext();
        path = intent.getStringExtra("path");

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getFloat("comprimation", 2);
        colored = prefs.getBoolean("colored",true);
        help = prefs.getBoolean("help",true);
        user = prefs.getString("user","");
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("posielanie",false);
        editor.apply();

        fb = (FloatingActionButton)  findViewById(R.id.info);
        mLinearLayout = (CoordinatorLayout) findViewById(R.id.rl_galelry_item_view);
        imageView = (ImageView) findViewById(R.id.image1);
        sendButton = (Button) findViewById(R.id.send);

        File file = new File(path);
        file_length = file.length()/(1024/1024);

        if(file_length>=0.9){
            if(file_length>comprimation){
                if(colored==true){
                    photo = BitmapHelper.decodePathMaxSize(path,32,0.9);
                    photo_send = BitmapHelper.decodePathMaxSize(path,32,comprimation);
                }
                else{
                    photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,0.9));
                    photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,comprimation));
                }
                float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                float ratio1 = BitmapHelper.decodePathMaxSizeRatio(path, 32, comprimation);
                ratio = ratio2/ratio1;
            }
            else{
                if(colored==true){
                    photo = BitmapHelper.decodePathMaxSize(path,32,0.9);
                    photo_send = BitmapFactory.decodeFile(path);
                }
                else{
                    photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,0.9));
                    photo_send = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
                }
                float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                float ratio1 = 1;
                ratio = ratio2/ratio1;
            }

        }
        else {
            if (colored == true) {
                photo = BitmapFactory.decodeFile(path);
                photo_send = BitmapFactory.decodeFile(path);
            }
            else {
                photo = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
                photo_send = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
            }
        }

        imageView.setImageBitmap(photo);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                photo_send = Bitmap.createBitmap(photo_send, 0, 0, photo_send.getWidth(), photo_send.getHeight(), matrix, true);
                imageView.setImageBitmap(photo);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("posielanie",true);
                editor.apply();
                ApiPostRequest.PostApi1(photo_send, photo, ratio, user, GalleryItemViewActivity.this, context, mLinearLayout);
            }
        });
        fb = findViewById(R.id.info);
        if(help==true) {
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog();
                }
            });
        }
        else {
            fb.hide();
        }

    }

    @Override
    public void onBackPressed() {
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean posielanie = prefs.getBoolean("posielanie", true);
        if(posielanie==false){
            this.finish();
        }
        else {
            Toast.makeText(getApplicationContext(), ("Najprv zastavte posielanie..."), Toast.LENGTH_SHORT).show();
        }
    }


    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Obrázok sa po kliknutí obráti o 90° ");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

}
