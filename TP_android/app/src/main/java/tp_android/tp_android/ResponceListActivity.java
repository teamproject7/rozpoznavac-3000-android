package tp_android.tp_android;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import android.util.Log;

public class ResponceListActivity extends AppCompatActivity {

    private String response;
    private ArrayList<String> list = new ArrayList<String>();
    private ListView lv;
    private JSONObject jsonResponce;
    private JSONArray dataArray;
    private String photoSend;
    private Database db;
    private ArrayList<Long> recordID = new ArrayList<Long>();
    private SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Setting";
    private String user;
    private Boolean saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
        db.open();
        setContentView(R.layout.activity_responce_list);
        Intent intent = getIntent();

        response = intent.getStringExtra("response");
        photoSend = intent.getStringExtra("photoSend");

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
            try {
                JSONObject item = (JSONObject)dataArray.get(i);
                list.add(item.getString("plate"));
                if(saving==true){
                    Long id = db.addRecord(Calendar.getInstance().getTime(),item.getString("plate"),item.toString(), user);
                    Log.d("id",id.toString() );
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
        intent.putExtra("photoSend", photoSend);
        if(saving==true){
            intent.putExtra("recordID", recordID.get(position));
        }
        startActivity(intent);
        if(dataArray.length() == 1){
            this.finish();
        }
    }
}