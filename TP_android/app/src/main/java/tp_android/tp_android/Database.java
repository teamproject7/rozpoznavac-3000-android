package tp_android.tp_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class Database {

    public static final String KEY_ID = "_id";
    public static final String KEY_TIME = "date_time";
    public static final String KEY_TIMEDEFAULT = "date_time_default";
    public static final String KEY_ECV = "ecv";
    public static final String KEY_ECVDEFAULT = "ecv_default";
    public static final String KEY_ECVIMAGE = "ecv_image";
    public static final String KEY_JSON = "json_response";
    public static final String KEY_USER = "user";

    private static final String TAG = "Database";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_CREATE =
            "create table ecv_records (_id integer primary key autoincrement, "
                    + "date_time text not null, date_time_default text not null, ecv text not null, ecv_default text not null, ecv_image text not null, json_response text not null, user text not null)";

    private static final String DATABASE_NAME = "tp7_ecv";
    private static final String DATABASE_TABLE = "ecv_records";
    private static final int DATABASE_VERSION = 2;

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
            db.execSQL("DROP TABLE IF EXISTS ecv_records");
            onCreate(db);
        }
    }

    public Database(Context ctx) {
        this.mCtx = ctx;
    }


    public Database open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public long addRecord(Date dateTime, String ecv, String ecv_image, String json, String user) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, dateTime.toString());
        initialValues.put(KEY_TIMEDEFAULT, dateTime.toString());
        initialValues.put(KEY_ECV, ecv);
        initialValues.put(KEY_ECVDEFAULT, ecv);
        initialValues.put(KEY_ECVIMAGE, ecv_image);
        initialValues.put(KEY_JSON, json);
        initialValues.put(KEY_USER, user);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    public void deleteRecord(long rowId) {
        db.execSQL("DELETE FROM "+DATABASE_TABLE+" WHERE "+KEY_ID+"='"+rowId+"'");
    }

    public void deletadeAllUserRecord(String user) {
        db.execSQL("DELETE FROM "+DATABASE_TABLE+" WHERE "+KEY_USER+"='"+user+"'");
    }

    private void deleteAllrecords(){
        db.delete(DATABASE_TABLE, null, null);
    }


    public Cursor fetchAllRecords() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TIME,
                KEY_ECV,KEY_ECVIMAGE, KEY_JSON, KEY_USER,KEY_TIMEDEFAULT,KEY_ECVDEFAULT}, null, null, null, null, null);
    }

    public Cursor fetchAllRecordsForUser(String user) {

        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TIME,
                KEY_ECV,KEY_ECVIMAGE, KEY_JSON, KEY_USER, KEY_TIMEDEFAULT, KEY_ECVDEFAULT}, KEY_USER + "='" + user+"'", null, null, null, KEY_TIME+" ASC",null);
    }

    public Cursor fetchRecord(long rowId) throws SQLException {
        Cursor mCursor =db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                                KEY_TIME, KEY_ECV, KEY_ECVIMAGE, KEY_JSON, KEY_USER, KEY_TIMEDEFAULT, KEY_ECVDEFAULT}, KEY_ID + "='" + rowId + "'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateRecord(long rowId, Date dateTime,String dateTime_default, String ecv, String ecv_default, String ecv_image, String json, String user) {
        ContentValues args = new ContentValues();
        args.put(KEY_TIME, dateTime.toString());
        args.put(KEY_TIMEDEFAULT, dateTime_default);
        args.put(KEY_ECV, ecv);
        args.put(KEY_ECVDEFAULT, ecv_default);
        args.put(KEY_ECVIMAGE, ecv_image);
        args.put(KEY_JSON, json);
        args.put(KEY_USER, user);

        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
    }
}

