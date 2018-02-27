package tp_android.tp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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


public class IncorrectRecognizedPlateActivity extends AppCompatActivity {
    private Context context;

    private LinearLayout mLinearLayout;
    private Button sendButton;
    private ImageView image;
    private EditText ecv;

    private Long recordID;
    private Bitmap photo;
    private String photoSend;

    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;




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

        image = (ImageView) findViewById(R.id.image1);
        mLinearLayout = (LinearLayout) findViewById(R.id.rl_incorrect);
        sendButton = (Button) findViewById(R.id.send);
        ecv = (EditText) findViewById(R.id.ecv);

        byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
        photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        image.setImageBitmap(photo);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiPostRequest.PostApi2(ecv.getText().toString(),saving, user, recordID, IncorrectRecognizedPlateActivity.this,context, mLinearLayout, photoSend);
            }
        });

    }

    }
