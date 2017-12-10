package tp_android.tp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.graphics.Color;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class ListItemActivity extends AppCompatActivity {

    private Context mContext;
    private String response;
    private String[] responseArray;
    private LinearLayout mLinearLayout;
    private Button buttonSpz;
    private Button buttonPoistenie;
    private Button buttonStk;
    private Button buttonVozidlo;
    private PopupWindow mPopupWindow;
    private String imgresponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        Intent intent = getIntent();
        mContext = getApplicationContext();
        response = intent.getStringExtra("response");
        imgresponse = intent.getStringExtra("image");
        responseArray = response.split("=====");


        mLinearLayout = (LinearLayout) findViewById(R.id.rl);
        buttonSpz = (Button) findViewById(R.id.buttonSpz);
        buttonPoistenie = (Button) findViewById(R.id.buttonPoistenie);
        buttonStk = (Button) findViewById(R.id.buttonStk);
        buttonVozidlo = (Button) findViewById(R.id.buttonVozidlo);
        if (imgresponse != null) {
            byte[] decodedString = Base64.decode(imgresponse, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() - 30, bmp.getHeight() - 30);

            ImageView image = (ImageView) findViewById(R.id.imageView);

            image.setImageBitmap(bmp2);
        }
        buttonSpz.setText(responseArray[2]);
        buttonSpz.setBackgroundColor(Color.GREEN);
        if (responseArray[3].equals("1")) {
            buttonPoistenie.setBackgroundColor(Color.GREEN);
            buttonPoistenie.setText("Zaplatené");
        } else {
            buttonPoistenie.setBackgroundColor(Color.RED);
            buttonPoistenie.setText("Nezaplatené");
        }
        if (responseArray[4].equals("1")) {
            buttonStk.setBackgroundColor(Color.GREEN);
            buttonStk.setText("Platná");
        } else {
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
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }
                Button buttonZnacka = (Button) customView.findViewById(R.id.buttonZnacka);
                Button buttonModel = (Button) customView.findViewById(R.id.buttonModel);
                Button buttonRocnik = (Button) customView.findViewById(R.id.buttonRocnik);
                buttonZnacka.setText(responseArray[5]);
                buttonModel.setText(responseArray[6]);
                buttonRocnik.setText(responseArray[7]);
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
    }

}
