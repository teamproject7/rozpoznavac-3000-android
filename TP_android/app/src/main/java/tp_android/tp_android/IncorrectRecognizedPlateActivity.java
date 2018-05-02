package tp_android.tp_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Toast;


public class IncorrectRecognizedPlateActivity extends AppCompatActivity {
    private Context context;

    private CoordinatorLayout mLinearLayout;
    private Button sendButton;
    private ImageView image;
    private EditText ecv;
    private FloatingActionButton fb;

    private Long recordID;
    private Bitmap photo;
    private String photoSend;

    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;
    private Boolean help;
    private boolean posielanie = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incorrect_recognized_plate);
        Intent intent = getIntent();
        context = getApplicationContext();
        photoSend = intent.getStringExtra("photoSend");
        recordID = intent.getLongExtra("recordID", -1);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user = prefs.getString("user","");
        saving = prefs.getBoolean("saving", true);
        help = prefs.getBoolean("help",true);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("posielanie",false);
        editor.apply();

        image = (FloatingActionButton) findViewById(R.id.info);
        image = (ImageView) findViewById(R.id.image1);
        mLinearLayout = (CoordinatorLayout) findViewById(R.id.rl_incorrect);
        sendButton = (Button) findViewById(R.id.send);
        ecv = (EditText) findViewById(R.id.ecv);

        byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
        photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        image.setImageBitmap(photo);
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("posielanie",true);
                editor.apply();
                ApiPostRequest.PostApi2(ecv.getText().toString(),saving, user, recordID, IncorrectRecognizedPlateActivity.this,context, mLinearLayout, photoSend);
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


    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Zadajte správnu značku v tvare bez medzier a pomlčky");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    }
