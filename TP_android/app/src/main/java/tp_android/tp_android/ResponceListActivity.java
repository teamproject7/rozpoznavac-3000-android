package tp_android.tp_android;

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

public class ResponceListActivity extends AppCompatActivity {

    private String response;
    private ArrayList<String> list = new ArrayList<String>();
    private ListView lv;
    private JSONObject jsonResponce;
    private JSONArray dataArray;
    private String photoSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responce_list);
        Intent intent = getIntent();

        response = intent.getStringExtra("response");
        photoSend = intent.getStringExtra("photoSend");

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


        if(dataArray.length() != 1){
            vypis(0);
        }
        else {
            for(int i=0; i < dataArray.length(); i++){
                try {
                    JSONObject item = (JSONObject)dataArray.get(i);
                    list.add(item.getString("plate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            lv = (ListView) findViewById(R.id.list_view);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list );
            lv.setAdapter(arrayAdapter);
            lv.setClickable(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    vypis(position);
                }
            });

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
        startActivity(intent);
        if(position != 0){
            this.finish();
        }


    }
}