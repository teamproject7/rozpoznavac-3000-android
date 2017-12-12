package tp_android.tp_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class HistoryRecordsActivity extends ListActivity {

    private Database db;
    public static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_list);
        db = new Database(this);
        db.open();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user = prefs.getString("user","");
        fillData();
    }


    public Date convertStrToDate(String str) throws ParseException {
        String string = str;
        DateFormat format = new SimpleDateFormat("HH:mm d.M.yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Bratislava"));
        Date date = format.parse(string);
        return date;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ListItemActivity.class);
        Cursor c = db.fetchRecord(id);
        String json = "";
        if (c.moveToFirst())
            json = c.getString(c.getColumnIndex("json_response"));

        c.close();
        i.putExtra("response",json);
        finish();
        startActivity(i);
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list

        //Cursor notesCursor = db.fetchAllRecordsForUser(user);    nejde
        Cursor notesCursor = db.fetchAllRecords();

        startManagingCursor(notesCursor);

        String[] from = new String[]{db.KEY_ID, db.KEY_ECV, db.KEY_TIME, db.KEY_USER};
        int[] to = new int[]{R.id.ecv, R.id.time, R.id.user};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.record_row, notesCursor, from, to, 0);
        setListAdapter(notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}


