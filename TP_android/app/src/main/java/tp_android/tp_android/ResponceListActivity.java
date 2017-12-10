package tp_android.tp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView;
import android.content.Intent;
import android.app.Activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ResponceListActivity extends AppCompatActivity {

    private Context mContext;
    private String response;
    private String[] responseArray;
    private String[] numberOfResponses;
    private LinearLayout mLinearLayout;
    private Button buttonSpz;
    private Button buttonPoistenie;
    private Button buttonStk;
    private Button buttonVozidlo;
    private PopupWindow mPopupWindow;
    private String imgresponse;
    private ArrayList<String> list = new ArrayList<String>();
    private ListView lv;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responce_list);
        Intent intent = getIntent();
        mContext = getApplicationContext();
        response = intent.getStringExtra("response");
        imgresponse = intent.getStringExtra("image");
        numberOfResponses = response.split("_____");
        db = new Database(this);
        db.open();


        for (int i = 0; i < numberOfResponses.length; i++) {
            String[] values = numberOfResponses[i].split("=====");
            list.add(values[2]);
            db.addRecord(new Date(), values[2], numberOfResponses[i], values[0]);
        }

        lv = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(arrayAdapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                vypis(position);
            }
        });
    }

    public void vypis(int position) {
        Intent intent = new Intent(this, ListItemActivity.class);
        intent.putExtra("response", numberOfResponses[position]);
        intent.putExtra("image", imgresponse);
        startActivity(intent);
    }
}
