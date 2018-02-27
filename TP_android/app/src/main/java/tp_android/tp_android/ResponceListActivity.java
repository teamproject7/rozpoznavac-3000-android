package tp_android.tp_android;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.util.Log;

public class ResponceListActivity extends AppCompatActivity {

    private String response;
    private ArrayList<String> list = new ArrayList<String>();
    private ListView lv;
    private JSONObject jsonResponce;
    private JSONArray dataArray;
    private String photoSend;
    private Float ratio;
    private Database db;
    private ArrayList<Long> recordID = new ArrayList<Long>();
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;
    private Bitmap photo;
    private ArrayList<String> spzList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
        db.open();
        setContentView(R.layout.activity_responce_list);
        Intent intent = getIntent();
        response = intent.getStringExtra("response");
        photoSend = intent.getStringExtra("photoSend");
        ratio = intent.getFloatExtra("ratio", 1);

        byte[] decodedString = Base64.decode(photoSend, Base64.DEFAULT);
        photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user = prefs.getString("user","");
        saving = prefs.getBoolean("saving", true);

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dataArray = jsonResponce.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0; i < dataArray.length(); i++){
            Integer x1;
            Integer x2;
            Integer y1;
            Integer y2;
            ArrayList<Integer> listx = new ArrayList<>();
            ArrayList<Integer> listy = new ArrayList<>();
            JSONArray coordinates = new JSONArray();
            try {
                JSONObject item = (JSONObject)dataArray.get(i);
                list.add(item.getString("plate"));
                try {
                    coordinates = (((JSONArray) item.getJSONArray("coordinates")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < coordinates.length(); j++) {
                    try {
                        listx.add(((JSONObject) coordinates.getJSONObject(j)).getInt("x"));
                        listy.add(((JSONObject) coordinates.getJSONObject(j)).getInt("y"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                x1 = Math.round(Collections.min(listx)*ratio);
                x2 = Math.round(Collections.max(listx)*ratio);
                y1 = Math.round(Collections.min(listy)*ratio);
                y2 = Math.round(Collections.max(listy)*ratio);

                Bitmap bmp = Bitmap.createBitmap(photo, x1, y1, x2 - x1, y2 - y1);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                spzList.add(encodedImage);

                if(saving==true){
                    Long id = db.addRecord(Calendar.getInstance().getTime(),item.getString("plate"), encodedImage, item.toString() , user);
                    recordID.add(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        lv = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list );
        lv.setAdapter(arrayAdapter);
        lv.setClickable(true);
        if(dataArray.length() > 1){
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    vypis(position);
                }
            });
        }
        else{
            vypis(0);
        }
    }

    public void vypis(int position) {
        Intent intent = new Intent(this, ListItemActivity.class);
        try {
            intent.putExtra("response",((JSONObject)dataArray.get(position)).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("photoSend", spzList.get(position));
        if(saving==true){
            intent.putExtra("recordID", recordID.get(position));
        }
        startActivity(intent);
        if(dataArray.length() == 1){
            this.finish();
        }
    }
}