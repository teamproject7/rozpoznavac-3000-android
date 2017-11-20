package tp_android.tp_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import android.util.Base64;

import java.util.HashMap;
import java.util.Map;

public class CapturephotoActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Bitmap photo;
    private String encodedImage;
    private Button photoButton;
    private Button postImageButton;
    private String url = "http://10.0.2.2:8080/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturephoto);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        photoButton = (Button) this.findViewById(R.id.button1);
        postImageButton = (Button) this.findViewById(R.id.button2);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            photoButton.setText("Nova fotka");
            postImageButton.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(photo);
        }
    }

    public void PostImage(View view) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                /*new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", response);
                    }
                },*/
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

                        vypis(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("name", "Majo");
                params.put("name", "Majo");
                Log.d("name", encodedImage);
                params.put("image", encodedImage);
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void vypis(String response){
        String[] splitresponse = response.split("=====");
        Log.d("pocet", Integer.toString(splitresponse.length));
        Log.d("respon", response);
        Log.d("Response", splitresponse[0] + splitresponse[1]);
        Log.d("Znacka", splitresponse[2]);
        Log.d("poistenie", splitresponse[3]);
        Log.d("STK, EK", splitresponse[4]);
        Log.d("Znacka", splitresponse[5]);
        Log.d("Model", splitresponse[6]);
        Log.d("Rok vyroby", splitresponse[7]);

        Intent intent = new Intent(this, ListItemActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("image", encodedImage);
        startActivity(intent);
    }
}