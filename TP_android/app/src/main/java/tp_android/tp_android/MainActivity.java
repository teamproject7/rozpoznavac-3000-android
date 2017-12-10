package tp_android.tp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "Setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("saving", true);
        editor.putBoolean("colored", true);
        editor.putInt("comprimation", 75);
        editor.apply();
    }

    public void CaptureImage(View view) {
            Intent intent = new Intent(this, CapturephotoActivity.class);
            startActivity(intent);
    }

    public void Gallery2Image(View view) {
        Intent intent = new Intent(this, Gallery2Activity.class);
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
