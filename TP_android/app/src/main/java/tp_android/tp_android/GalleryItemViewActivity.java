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
import android.widget.Button;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Toast;

import java.io.File;

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
    private float new_file_length;
    private float file_length;


    private static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private float comprimation;
    private boolean colored;
    private boolean help;
    private String user;
    private Toast toast;


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        sendingEventRequesting(false);

        fb = (FloatingActionButton)  findViewById(R.id.info);
        mLinearLayout = (CoordinatorLayout) findViewById(R.id.gallery_item_view);
        imageView = (ImageView) findViewById(R.id.imageView1);
        sendButton = (Button) findViewById(R.id.send);

        File file = new File(path);
        file_length = (float) file.length()/(1024*1024);

        if(file_length>=0.9){
            if(file_length>comprimation){
                if(colored){
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
                new_file_length=comprimation;
            }
            else{
                if(colored){
                    photo = BitmapHelper.decodePathMaxSize(path,32,0.9);
                    photo_send = BitmapHelper.decodePathMaxSize(path,32,file_length);
                }
                else{
                    photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,0.9));
                    photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,file_length));
                }
                float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                float ratio1 = BitmapHelper.decodePathMaxSizeRatio(path, 32, file_length);
                ratio = ratio2/ratio1;
                new_file_length=file_length;
            }
        }
        else {
            if (colored) {
                photo = (BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                photo_send = (BitmapHelper.decodePathMaxSize(path, 32, 0.9));
            } else {
                photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
            }
            new_file_length = file_length;
        }

        imageView.setImageBitmap(photo_send);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                photo_send = Bitmap.createBitmap(photo_send, 0, 0, photo_send.getWidth(), photo_send.getHeight(), matrix, true);
                imageView.setImageBitmap(photo_send);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendingEventRequesting(true);
                if (toast != null) {
                    toast.cancel();
                }
                ApiPostRequest.PostApi1(photo_send, photo, ratio, user, GalleryItemViewActivity.this, context, mLinearLayout);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                imageInfo();
                return true;
            }
        });

        fb = findViewById(R.id.info);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        fb.show();
        boolean posielanie = prefs.getBoolean("posielanie", true);
        if(help && !posielanie) {
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
        imageInfo();
    }

    @Override
    public void onBackPressed() {
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean posielanie = prefs.getBoolean("posielanie", true);
        if(!posielanie){
            if (toast != null) {
                toast.cancel();
            }
            this.finish();
        }
        else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getApplicationContext(), ("Najprv zastavte posielanie..."), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void sendingEventRequesting(boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("posielanie",value);
        editor.apply();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Obrázok sa po kliknutí otočí o 90° a po dlhom kliknutí sa zobrazí správa o jeho pôvodnej a odosielanej veľkosti");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public void imageInfo(){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), ("Povodná/odosielaná veľkosť obrázku: "+Math.round((double)(file_length*100.0f))/100.0f+" MB/ "+Math.round((double)(new_file_length*100.0f))/100.0f+" MB"), Toast.LENGTH_SHORT);
        toast.show();
    }

}
