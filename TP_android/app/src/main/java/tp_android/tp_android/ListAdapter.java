package tp_android.tp_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.time.LocalDateTime;

public class ListAdapter {

    public static final String KEY_ID = "id";
    public static final String KEY_TIME = "date_time";
    public static final String KEY_ECV = "ecv";
    public static final String KEY_JSON = "json_response";

    private static final String TAG = "ListAdapter";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_CREATE =
            "create table ecv_records (id integer primary key autoincrement, "
                    + "date_time text not null, ecv text not null, json_response text not null)";

    private static final String DATABASE_NAME = "tp7";
    private static final String DATABASE_TABLE = "ecv_records";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public ListAdapter(Context ctx) {
        this.mCtx = ctx;
    }


    public ListAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public long addRecord(LocalDateTime dateTime, String ecv, String json) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, dateTime.toString());
        initialValues.put(KEY_ECV, ecv);
        initialValues.put(KEY_JSON, json);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteNote(long rowId) {

        return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }


    public Cursor fetchAllRecords() {

        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TIME,
                KEY_ECV, KEY_JSON}, null, null, null, null, null);
    }

    public Cursor fetchRecord(long rowId) throws SQLException {

        Cursor mCursor =

                db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                                KEY_TIME, KEY_ECV, KEY_JSON}, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public boolean updateRecord(long rowId, LocalDateTime dateTime, String ecv, String json) {
        ContentValues args = new ContentValues();
        args.put(KEY_TIME, dateTime.toString());
        args.put(KEY_ECV, ecv);
        args.put(KEY_JSON, json);

        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
    }
}

