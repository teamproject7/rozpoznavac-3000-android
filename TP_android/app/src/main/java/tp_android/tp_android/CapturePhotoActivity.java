package tp_android.tp_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;


public class CapturePhotoActivity extends Activity {

    private Context context;
    private CoordinatorLayout mLinearLayout;
    private Button sendButton;
    private ImageView imageView;
    private FloatingActionButton fb;

    private Bitmap photo;
    private Bitmap photo_send;
    private String path;
    private File file;
    private float ratio = 1;
    private float file_length;
    private float new_file_lenght;

    private boolean imageInit = false;
    private static final String app_dir = "spz_egov";
    private int CAMERA_REQUEST;
    final int MyVersion = Build.VERSION.SDK_INT;
    private static final String MY_PREFS_NAME = "Setting";

    private SharedPreferences prefs;
    private float comprimation;
    private boolean colored;
    private boolean help;
    private String user;

    private Intent cameraIntent;
    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_capture_photo);
                mLinearLayout =  (CoordinatorLayout) findViewById(R.id.capture_photo);
                context = getApplicationContext();

                prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                comprimation = prefs.getFloat("comprimation", 2);
                colored = prefs.getBoolean("colored", true);
                help = prefs.getBoolean("help", true);
                user = prefs.getString("user", "");
                sendingEventRequesting(false);

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
                sendingEventRequesting(true);
                if (toast != null) {
                    toast.cancel();
                }
                ApiPostRequest.PostApi1(photo_send, photo, ratio, user, CapturePhotoActivity.this, context, mLinearLayout);

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageInit) {
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

            if (file_length >= 0.9f) {
                if (file_length > comprimation) {
                    if (colored) {
                        photo = BitmapHelper.decodePathMaxSize(path, 32, 0.9);
                        photo_send = BitmapHelper.decodePathMaxSize(path, 32, comprimation);
                    } else {
                        photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                        photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, comprimation));
                    }
                    float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                    float ratio1 = BitmapHelper.decodePathMaxSizeRatio(path, 32, comprimation);
                    ratio = ratio2 / ratio1;
                    new_file_lenght=comprimation;
                } else {
                    if (colored) {
                        photo = BitmapHelper.decodePathMaxSize(path, 32, 0.9);
                        photo_send = BitmapHelper.decodePathMaxSize(path,32,file_length);
                    } else {
                        photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                        photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path,32,file_length));
                    }
                    float ratio2 = BitmapHelper.decodePathMaxSizeRatio(path, 32, 0.9);
                    float ratio1 = BitmapHelper.decodePathMaxSizeRatio(path, 32, file_length);
                    ratio = ratio2 / ratio1;
                    new_file_lenght=file_length;
                }

            } else {
                if (colored) {
                    photo = (BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                    photo_send = (BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                } else {
                    photo = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                    photo_send = BitmapHelper.toGrayscale(BitmapHelper.decodePathMaxSize(path, 32, 0.9));
                }
                new_file_lenght = file_length;
            }

            imageView.setImageBitmap(photo);
            imageInit = true;

           imageView.setOnLongClickListener(new View.OnLongClickListener() {

               @Override
               public boolean onLongClick(View v) {
                   imageInfo();
                   return true;
               }
           });

           imageInfo();
            fb = findViewById(R.id.info);
            fb.show();
            prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            boolean posielanie = prefs.getBoolean("posielanie", true);
            if(help && !posielanie) {
                fb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogHelp();
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
        file_length = (float) file.length()/(1024*1024);
    }

    private void readOrCreateDir(){
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + app_dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void sendingEventRequesting(boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("posielanie",value);
        editor.apply();
    }


    private void imageInfo(){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), ("Povodná/odosielaná veľkosť obrázku: "+Math.round((double)(file_length*100.0f))/100.0f+" Mb/ "+Math.round((double)(new_file_lenght*100.0f))/100.0f+" Mb"), Toast.LENGTH_SHORT);
        toast.show();
    }


    private void alertDialogHelp() {
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

}

