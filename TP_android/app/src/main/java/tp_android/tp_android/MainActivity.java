package tp_android.tp_android;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSetting();
    }

    private void loadSetting() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        SettingDatabase db_setting;
        db_setting = new SettingDatabase(this);
        db_setting.open();
        Cursor c = db_setting.fetchAllRecords();
        Boolean db_saving;
        Boolean db_colored;
        Boolean db_help;
        Float db_comrimation;
        String db_user;

        if (c.getCount() < 1){
            db_setting.addRecord("Admin", Boolean.TRUE, Boolean.TRUE,Boolean.TRUE, 1.5f);
            loadSetting();
        }
        else if (c.moveToFirst()) {
            db_colored = c.getInt(c.getColumnIndex("colored"))!= 0;
            //db_saving = c.getInt(c.getColumnIndex("saving"))!= 0;
            db_saving = true;
            db_help = c.getInt(c.getColumnIndex("help"))!= 0;
            db_user = c.getString(c.getColumnIndex("user"));
            db_comrimation = c.getFloat(c.getColumnIndex("comprimation"));

            editor.putBoolean("saving", db_saving);
            editor.putBoolean("help", db_help);
            editor.putBoolean("colored", db_colored);
            editor.putFloat("comprimation", db_comrimation);
            editor.putString("user", db_user);
            editor.apply();
        }
    }

    public void CaptureImage(View view) {
            Intent intent = new Intent(this, CapturePhotoActivity.class);
            startActivity(intent);
    }

    public void Gallery2Image(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);

    }

    public void Setting(View view) {
        Intent intent = new Intent(this, ApplicationSettingActivity.class);
        startActivity(intent);

    }

    public void HistoricRecords(View view) {
        Intent intent = new Intent(this, HistoryRecordsActivity.class);
        startActivity(intent);

    }


}
