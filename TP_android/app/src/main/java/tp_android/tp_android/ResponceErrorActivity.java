package tp_android.tp_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResponceErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responce_error);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        TextView textView = (TextView) findViewById(R.id.message);
        textView.setText(message);
    }
}
