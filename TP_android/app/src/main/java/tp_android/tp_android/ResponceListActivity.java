package tp_android.tp_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
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
    private ArrayList<String> list2 = new ArrayList<String>();
    private ListView lv;
    private JSONObject jsonResponce;
    private JSONArray dataArray;
    private String photoSend;
    private Float ratio;
    private Database db;
    private ArrayList<Long> recordID = new ArrayList<Long>();
    private ArrayList<String> recordJSON = new ArrayList<String>();
    private SharedPreferences prefs;
    private static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;
    private Bitmap photo;
    private ArrayList<String> spzList = new ArrayList<String>();
    static final int LIST_RELOAD = 1;
    private boolean preslo= false;

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
        user = prefs.getString("user", "");
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


        for (int i = 0; i < dataArray.length(); i++) {
                Integer x1;
                Integer x2;
                Integer y1;
                Integer y2;
                ArrayList<Integer> listx = new ArrayList<>();
                ArrayList<Integer> listy = new ArrayList<>();
                JSONArray coordinates = new JSONArray();
                try {
                    JSONObject item = (JSONObject) dataArray.get(i);
                    //list.add(item.getString("plate"));
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

                    x1 = Math.round(Collections.min(listx) * ratio);
                    x2 = Math.round(Collections.max(listx) * ratio);
                    y1 = Math.round(Collections.min(listy) * ratio);
                    y2 = Math.round(Collections.max(listy) * ratio);

                    try {
                        Bitmap bmp = Bitmap.createBitmap(photo, x1, y1, x2 - x1, y2 - y1);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        spzList.add(encodedImage);

                        if (saving == true) {
                            Long id = db.addRecord(Calendar.getInstance().getTime(), item.getString("plate"), encodedImage, item.toString(), user);
                            recordID.add(id);
                            recordJSON.add(((JSONObject)dataArray.get(i)).toString());
                            Cursor c = db.fetchRecord(id);
                            if (c.moveToFirst()) {
                                list.add(c.getString(c.getColumnIndex("ecv")));
                            }
                        }
                        preslo=true;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
        }
        lv = (ListView) findViewById(R.id.list_view);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_responce_list_row, list);
        lv.setAdapter(arrayAdapter);
        lv.setClickable(true);
        if (dataArray.length() >=2  && preslo) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    vypis(position);
                }
            });
        } else if (dataArray.length() ==1  && preslo) {
            vypis(0);
        }
        else {
            alertError(this,"Nepodarilo sa spracovať prijaté údaje zo serveru");
        }

    }

    public void vypis(int position) {
        Intent intent = new Intent(this, ListItemActivity.class);
        intent.putExtra("response",recordJSON.get(position));
        intent.putExtra("photoSend", spzList.get(position));
        if(saving){
            intent.putExtra("recordID", recordID.get(position));
        }

        startActivityForResult(intent,LIST_RELOAD);
        if(dataArray.length() == 1){
            this.finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                list = new  ArrayList<String>();
                recordJSON.clear();
                recordJSON = new  ArrayList<String>();
                Cursor c;
                for(long rID : recordID){
                    c = db.fetchRecord(rID);
                    if (c.moveToFirst()) {
                        list.add(c.getString(c.getColumnIndex("ecv")));
                        recordJSON.add(c.getString(c.getColumnIndex("json_response")));
                    }
                }
                lv = (ListView) findViewById(R.id.list_view);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.activity_responce_list_row, list );
                lv.setAdapter(arrayAdapter);
                lv.setClickable(true);
                if(dataArray.length() >= 1 && preslo){
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
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }
    public  void alertError(Activity parentActivity, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle("Chyba");
        builder.setMessage(error);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResponceListActivity.this.finish();
            }
        });
        builder.show();
    }

}