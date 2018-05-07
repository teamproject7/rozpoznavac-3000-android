package tp_android.tp_android;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class ApplicationSettingActivity extends AppCompatActivity {
    private Switch switchButton, switchButton2, switchButton3;
    private SeekBar seekBar;
    private Database db;
    private TextView tvProgressLabel;
    private TextView userLabel;
    private Button deleteUserData;
    private static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences.Editor editor;
    private boolean colored;
    private boolean saving;
    private boolean help;
    private String user;
    private Float comprimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_setting);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        comprimation = prefs.getFloat("comprimation", 2);
        colored = prefs.getBoolean("colored",true);
        saving = prefs.getBoolean("saving", true);
        help = prefs.getBoolean("help", true);
        user = prefs.getString("user", "");

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        userLabel = findViewById(R.id.user);
        userLabel.setText(user);

        seekBar = findViewById(R.id.comprimation);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("Maximálna veľkosť posielaného obrázku (1-5MB):  " + Math.round((double)(comprimation*100.0f))/100.0f + " MB");
        seekBar.setProgress(Math.round((int)((comprimation-1)/0.25)));
        deleteUserData = (Button) findViewById(R.id.buttonDelete);

        switchButton = (Switch) findViewById(R.id.colored);
        switchButton.setChecked(colored);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    colored=true;

                } else {
                    colored=false;
                }
                editor.putBoolean("colored", colored);
                editor.apply();
                setDB();
            }
        });

        switchButton2 = (Switch) findViewById(R.id.saving);
        switchButton2.setChecked(saving);
        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    saving=true;

                } else {
                    saving=false;
                }
                editor.putBoolean("saving",saving);
                editor.apply();
                setDB();
            }
        });

        switchButton3 = (Switch) findViewById(R.id.help);
        switchButton3.setChecked(help);
        switchButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    help=true;

                } else {
                    help=false;
                }
                editor.putBoolean("help", help);
                editor.apply();
                setDB();
            }
        });

        deleteUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            editor.putFloat("comprimation", ((float) (seekBar.getProgress()*0.25f)+1.0f));
            comprimation=( (float)(seekBar.getProgress()*0.25f)+1.0f);
            editor.apply();
            setDB();
            tvProgressLabel.setText("Maximálna veľkosť posielaného obrázku (1-5MB):  " + Math.round((double)(comprimation*100.0f))/100.0f + " MB");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            editor.putFloat("comprimation", ((float) (seekBar.getProgress()*0.25f)+1.0f));
            comprimation=( (float)(seekBar.getProgress()*0.25f)+1.0f);
            editor.apply();
            setDB();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            editor.putFloat("comprimation", ((float) (seekBar.getProgress()*0.25f)+1.0f));
            comprimation=( (float)(seekBar.getProgress()*0.25f)+1.0f);
            editor.apply();
            setDB();
        }
    };

    private void confirmDialog() {
        db = new Database(ApplicationSettingActivity.this);
        db.open();
        int pocet = 0;
        Cursor c = db.fetchAllRecordsForUser(user);
        pocet = c.getCount();
        final int finalPocet = pocet;

        if (finalPocet > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("Prajete si zmazať všetky záznamy užívateľa '" + user + "' ?");
            builder.setCancelable(true);
            builder.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deletadeAllUserRecord(user);
                    if (finalPocet == 1) {
                        Toast.makeText(getApplicationContext(), "Záznam zmazaný...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Všetky záznamy zmazané...", Toast.LENGTH_SHORT).show();
                }
                }
            });

            builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), ("Ždiadne záznamy..."), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDB(){
        SettingDatabase db_setting;
        db_setting = new SettingDatabase(this);
        db_setting.open();
        Cursor c = db_setting.fetchAllRecords();
        int db_id;

        if (c.moveToFirst()) {
            db_id = c.getInt(c.getColumnIndex("_id"));
            db_setting.updateRecord(db_id,user,colored,saving,help, comprimation);
        }
    }


}
