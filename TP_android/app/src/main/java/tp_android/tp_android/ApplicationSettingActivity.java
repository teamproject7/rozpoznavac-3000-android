package tp_android.tp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Switch;
import android.widget.CompoundButton;

public class ApplicationSettingActivity extends AppCompatActivity {

    TextView tvProgressLabel;
    public static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_setting);
        Switch switchButton, switchButton2;
        // set a change listener on the SeekBar
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int comprimation = prefs.getInt("comprimation", 100);
        boolean colored = prefs.getBoolean("colored",true);
        boolean saving = prefs.getBoolean("saving", true);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        SeekBar seekBar = findViewById(R.id.comprimation);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("Set compriamtion of sending images: " + comprimation + " %");
        int progress = seekBar.getProgress();
        seekBar.setProgress(comprimation);



        switchButton = (Switch) findViewById(R.id.colored);
        switchButton.setChecked(colored);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    editor.putBoolean("colored", true);

                } else {
                    editor.putBoolean("colored", false);
                }
                editor.apply();
            }
        });

        switchButton2 = (Switch) findViewById(R.id.saving);
        switchButton2.setChecked(saving);
        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    editor.putBoolean("saving", true);

                } else {
                    editor.putBoolean("saving", false);
                }
                editor.apply();
            }
        });



    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("Set compriamtion of sending images: " + progress + " %");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            editor.putInt("comprimation", seekBar.getProgress());
            editor.apply();
            // called after the user finishes moving the SeekBar
        }
    };


}
