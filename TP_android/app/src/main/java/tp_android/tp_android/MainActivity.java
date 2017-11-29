package tp_android.tp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
    }

    public void CaptureImage(View view) {
            Intent intent = new Intent(this, CapturephotoActivity.class);
            startActivity(intent);
    }

    public void GalleryImage(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);

    }
}
