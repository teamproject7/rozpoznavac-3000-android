package tp_android.tp_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SettingDatabase {

    public static final String KEY_ID = "_id";
    public static final String KEY_COLORED = "colored";
    public static final String KEY_SAVING = "saving";
    public static final String KEY_USER = "user";
    public static final String KEY_HELP = "help";
    public static final String KEY_COMPRIMATION = "comprimation";


    private static final String TAG = "SettingDatabase";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static final String DATABASE_CREATE =
            "create table setting_records (_id integer primary key autoincrement, "
                    + "colored boolean not null, saving boolean not null, help boolean not null, user text not null, comprimation float not null)";

    private static final String DATABASE_NAME = "tp7_ecv_setting";
    private static final String DATABASE_TABLE = "setting_records";
    private static final int DATABASE_VERSION = 12;

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
            db.execSQL("DROP TABLE IF EXISTS setting_records");
            onCreate(db);
        }
    }

    public SettingDatabase(Context ctx) {
        this.mCtx = ctx;
    }


    public SettingDatabase open() throws SQLException {
        dbHelper = new DatabaseHelper(mCtx);
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public long addRecord(String user, Boolean colored, Boolean saving, Boolean help, Float comprimation) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USER, user);
        initialValues.put(KEY_COLORED, colored);
        initialValues.put(KEY_SAVING, saving);
        initialValues.put(KEY_HELP, help);
        initialValues.put(KEY_COMPRIMATION, comprimation);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    public Cursor fetchRecord(long rowId) throws SQLException {

        Cursor mCursor =db.query(true, DATABASE_TABLE, new String[]{KEY_ID,KEY_USER, KEY_COLORED, KEY_SAVING,KEY_HELP, KEY_COMPRIMATION}, KEY_ID + "='" + rowId+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchAllRecords() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID,KEY_USER, KEY_COLORED, KEY_SAVING, KEY_HELP, KEY_COMPRIMATION}, null, null, null, null, null);
    }

    public boolean updateRecord(long rowId,String user, Boolean colored, Boolean saving, Boolean help, Float comprimation) {
        ContentValues args = new ContentValues();
        args.put(KEY_USER, user);
        args.put(KEY_COLORED, colored);
        args.put(KEY_HELP, help);
        args.put(KEY_SAVING, saving);
        args.put(KEY_COMPRIMATION, comprimation);
        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
    }
}

