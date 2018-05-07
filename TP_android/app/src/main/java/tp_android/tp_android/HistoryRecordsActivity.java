package tp_android.tp_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.support.design.widget.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

public class HistoryRecordsActivity extends ListActivity {

    private FloatingActionButton fb;
    private Database db;
    private static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private String user;
    private Boolean help;
    private Boolean saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_list);
        db = new Database(this);
        db.open();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user = prefs.getString("user","");
        help = prefs.getBoolean("help",true);
        saving = prefs.getBoolean("saving", true);
        fb = findViewById(R.id.info);
        if(help){
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogHelp();
                }
            });
        }
        else {
            fb.hide();
        }
        fillData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = new Database(this);
        db.open();
        fillData();
    }

    public String convertStrToDate(String str)  {
        String string = str;
        DateFormat format = new SimpleDateFormat("HH:mm d.M.yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Bratislava"));
        Date date = new Date();
        try {
            date = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.toString();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, ListItemActivity.class);
        Cursor c = db.fetchRecord(id);
        String json = "";
        String ecv_image = "";

        if (c.moveToFirst()) {
            json = c.getString(c.getColumnIndex("json_response"));
            ecv_image = c.getString(c.getColumnIndex("ecv_image"));
        }

        intent.putExtra("response",json);
        intent.putExtra("photoSend", ecv_image);
        if(saving){
            intent.putExtra("recordID", id);
        }
        c.close();
        startActivity(intent);
    }

    private void fillData() {
        Cursor notesCursor = db.fetchAllRecordsForUser(user);
        if (notesCursor.getCount()>0){
            startManagingCursor(notesCursor);
            String[] from = new String[]{ db.KEY_ECV,db.KEY_TIME};
            int[] to = new int[]{R.id.ecv, R.id.time};

            SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.activity_record_list_row, notesCursor, from, to, 0);
            setListAdapter(notes);
            ListView lv = getListView();
            lv.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View v ,int position, long id) {
                    confirmDialog(id);
                    return true;
                }
            });
        }
        else{
            this.finish();
            Toast.makeText(getApplicationContext(), "Žiadny záznam...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    private void confirmDialog(final long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("Prajete si zmazať vybraný záznam ?");
            builder.setCancelable(true);
            builder.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Záznam zmazaný...", Toast.LENGTH_SHORT).show();
                        db.deleteRecord(id);
                        fillData();
                }
            });
            builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }

    private void alertDialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Pre otvorenie záznamu kliknite a pre vymazanie podržte položku");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

}


