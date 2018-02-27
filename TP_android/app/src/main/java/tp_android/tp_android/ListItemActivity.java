package tp_android.tp_android;

import android.graphics.Matrix;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.graphics.Color;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ListItemActivity extends AppCompatActivity {

    private Context mContext;
    private String response;
    private LinearLayout mLinearLayout;
    private Button buttonSpz;
    private Button buttonPoistenie;
    private Button buttonStk;
    private Button buttonVozidlo;
    private Button buttonZleRozpoznanie;
    private PopupWindow mPopupWindow;
    private ImageView image;
    private Bitmap photo;
    private JSONObject jsonResponce;
    private String photoSend;

    private String spz;
    private Boolean stk;
    private Boolean poistenie;
    private String znacka;
    private String model;
    private String rocnik;
    private Long recordID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        Intent intent = getIntent();
        mContext = getApplicationContext();
        response = intent.getStringExtra("response");
        photoSend = (intent.getStringExtra("photoSend"));
        recordID = (intent.getLongExtra("recordID",-1));

        byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
        photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            spz = (jsonResponce.getString("plate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            stk = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("checks").getJSONObject("stk").getBoolean("valid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            poistenie = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("insurance").getBoolean("valid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            znacka = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("brand"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            model = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("model"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            rocnik = (((JSONObject) jsonResponce.getJSONObject("vehicle")).getJSONObject("parameters").getString("year"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mLinearLayout = (LinearLayout) findViewById(R.id.rl);
        buttonSpz = (Button) findViewById(R.id.buttonSpz);
        buttonPoistenie = (Button) findViewById(R.id.buttonPoistenie);
        buttonStk = (Button) findViewById(R.id.buttonStk);
        buttonVozidlo = (Button) findViewById(R.id.buttonVozidlo);
        buttonZleRozpoznanie = (Button) findViewById(R.id.buttonIncorectRecognized);
        image = (ImageView) findViewById(R.id.imageView);


        image.setImageBitmap(photo);
        buttonSpz.setText(spz);
        buttonSpz.setBackgroundColor(Color.GREEN);
        if (stk.equals("true"))
        {
            buttonPoistenie.setBackgroundColor(Color.GREEN);
            buttonPoistenie.setText("Zaplatené");
        } else {
            buttonPoistenie.setBackgroundColor(Color.RED);
            buttonPoistenie.setText("Nezaplatené");
        }

        if (poistenie.equals("true"))
        {
            buttonStk.setBackgroundColor(Color.GREEN);
            buttonStk.setText("Platná");
        } else

        {
            buttonStk.setBackgroundColor(Color.RED);
            buttonStk.setText("Neplatná");
        }
        buttonVozidlo.setText("Info");

        buttonVozidlo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.custom_layout, null);

                mPopupWindow = new PopupWindow(
                        customView,
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                );
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }
                Button buttonZnacka = (Button) customView.findViewById(R.id.buttonZnacka);
                Button buttonModel = (Button) customView.findViewById(R.id.buttonModel);
                Button buttonRocnik = (Button) customView.findViewById(R.id.buttonRocnik);
                buttonZnacka.setText(znacka);
                buttonModel.setText(model);
                buttonRocnik.setText(rocnik);
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
            }
        });


        buttonZleRozpoznanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListItemActivity.this, IncorrectRecognizedPlateActivity.class);
                intent.putExtra("photoSend", photoSend);
                intent.putExtra("recordID", recordID);
                startActivity(intent);
                ListItemActivity.this.finish();
            }
        });
    }
}


