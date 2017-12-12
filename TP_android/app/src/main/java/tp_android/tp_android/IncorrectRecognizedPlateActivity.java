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

    private EditText ecv;
    private String coord;
    private Long recordID;

    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;


    private Bitmap photo;
    private String photoSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incorrect_recognized_plate);
        Intent intent = getIntent();
        context = getApplicationContext();
        photoSend = intent.getStringExtra("photoSend");
        coord = intent.getStringExtra("coord");
        recordID = intent.getLongExtra("recordID", -1);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user = prefs.getString("user","");
        saving = prefs.getBoolean("saving", true);
        ArrayList<Integer> docasne  = intent.getIntegerArrayListExtra("docasne");



        byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
        photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Matrix matrix = new Matrix();
        ImageView image = (ImageView) findViewById(R.id.image1);
        image.setImageBitmap(Bitmap.createBitmap(photo, docasne.get(0), docasne.get(1), docasne.get(2) - docasne.get(0), docasne.get(3) - docasne.get(1)));

        mLinearLayout = (LinearLayout) findViewById(R.id.rl_incorrect);

        sendButton = (Button) findViewById(R.id.send);

        ecv = (EditText) findViewById(R.id.ecv);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiPostRequest.PostApi2(ecv.getText().toString(),saving, user, recordID, IncorrectRecognizedPlateActivity.this,context, mLinearLayout, photoSend, coord );
            }
        });

    }

    }
