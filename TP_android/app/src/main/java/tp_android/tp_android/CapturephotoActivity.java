package tp_android.tp_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.LinearLayout;
import android.provider.MediaStore;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.StrictMode;
import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Toast;


public class CapturephotoActivity extends Activity {
    private Context context;

    private CoordinatorLayout mLinearLayout;
    private Button sendButton;
    private ImageView imageView;

    private Bitmap photo;
    private Bitmap photo_send;
    private String path;
    private File file;
    private float ratio = 1;
    private String app_dir = "spz_app";
    private float file_length;

    private boolean imageInit = false;

    //private static final int CAMERA_REQUEST = 1888;
    private int CAMERA_REQUEST;
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private float comprimation;
    private boolean colored;
    private boolean help;
    private String user;
    final int MyVersion = Build.VERSION.SDK_INT;
    private Intent cameraIntent;
    private FloatingActionButton fb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_capturephoto);
                mLinearLayout =  (CoordinatorLayout) findViewById(R.id.rl_captured_photo);
                context = getApplicationContext();

                prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                comprimation = prefs.getFloat("comprimation", 2);
                colored = prefs.getBoolean("colored", true);
                help = prefs.getBoolean("help", true);
                user = prefs.getString("user", "");
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("posielanie",false);
                editor.apply();

                imageView = (ImageView) this.findViewById(R.id.imageView1);
                sendButton = (Button) findViewById(R.id.button2);

                String model =  android.os.Build.MANUFACTURER;
                if(model.equalsIgnoreCase("samsung")){
                    CAMERA_REQUEST = 0;
                }
                else{
                    CAMERA_REQUEST = 1337;
                }

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                cameraInit();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("posielanie",true);
                editor.apply();
                ApiPostRequest.PostApi1(photo_send, photo, ratio, user, CapturephotoActivity.this, context, mLinearLayout);

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageInit==true) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                    photo_send = Bitmap.createBitmap(photo_send, 0, 0, photo_send.getWidth(), photo_send.getHeight(), matrix, true);
                    imageView.setImageBitmap(photo);
                }
            }
        });
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

    private void cameraInit(){
        String out = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        out =  File.separator + app_dir + File.separator+"spz" + out + ".jpg";

        if (MyVersion >= Build.VERSION_CODES.M) {
            if (!PermissionsAllow.checkIfAlreadyhavePermissionWrite(this)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                readOrCreateDir();
            }
            else{
                readOrCreateDir();
            }
        }
        if (MyVersion < Build.VERSION_CODES.M) {
            readOrCreateDir();
        }

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ out);
        path = file.toString();
        cameraIntent = null;
        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            sendButton.setVisibility(View.VISIBLE);
            if (MyVersion >= Build.VERSION_CODES.M) {
                if (!PermissionsAllow.checkIfAlreadyhavePermissionRead(this)) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    readImage();
                } else {
                    readImage();
                }
            }
            if (MyVersion < Build.VERSION_CODES.M) {
                readImage();
            }


            if (file_length >= 0.9) {
                if (file_length > comprimation) {
                    if (colored == true) {
                        photo = BitmapHelper.decodePathMaxSize(path, 32, 0.9);
                        photo_send = BitmapHelper.decodePathMaxSize(path, 32, comprimation);
                    } else {
                        photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                        photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, comprimation));
                    }
                    float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                    float ratio1 = BitmapHelper.decodePathMaxSizeRatio(path, 32, comprimation);
                    ratio = ratio2 / ratio1;
                } else {
                    if (colored == true) {
                        photo = BitmapHelper.decodePathMaxSize(path, 32, 0.9);
                        photo_send = BitmapFactory.decodeFile(path);
                    } else {
                        photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                        photo_send = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
                    }
                    float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                    float ratio1 = 1;
                    ratio = ratio2 / ratio1;
                }

            } else {
                if (colored == true) {
                    photo = BitmapFactory.decodeFile(path);
                    photo_send = BitmapFactory.decodeFile(path);
                } else {
                    photo = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
                    photo_send = BitmapHelper.toGrayscale(BitmapFactory.decodeFile(path));
                }
            }

            imageView.setImageBitmap(photo);
            imageInit = true;

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
        else{
           this.finish();
       }
    }

    private void readImage(){
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getPath());
        imageView.setImageBitmap(myBitmap);
        file_length = file.length()/(1024*1024);
    }

    private void readOrCreateDir(){
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + app_dir);
        if (!directory.exists()) {
            directory.mkdirs();
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

