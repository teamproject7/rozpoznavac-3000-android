package tp_android.tp_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiPostRequest {
    private static String urlApi1 =  "http://108.61.179.124:7486/spz_img/";
    private static String urlApi2 =  "http://108.61.179.124:7486/spz/";

    public static void PostApi1(final Bitmap photo, final Bitmap store_photo, final float ratio, String user, final Activity parentActivity, Context parentContext, LinearLayout mLinearLayout) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapHelper.toGrayscale(photo).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", user);
        params.put("image", encodedImage);

        final RequestQueue queue = Volley.newRequestQueue(parentActivity);
        final RequestSending rs = new RequestSending(parentContext, parentActivity, mLinearLayout);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlApi1, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        store_photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage2 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        rs.stop();
                        ResponceDecision.Responce(response.toString(), encodedImage2, ratio, parentActivity);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rs.stop();
                ResponceDecision.notResponce("problem", parentActivity);
            }
        });
        queue.add(postRequest);
        rs.stopButton(queue);
    }

    public static void PostApi2(String ecv,final Boolean saving, final String user,final long recordID, final Activity parentActivity, Context parentContext, LinearLayout mLinearLayout, final String photoSend) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", user);
        params.put("plate_string", ecv);

        final RequestQueue queue = Volley.newRequestQueue(parentActivity);
        final RequestSending rs = new RequestSending(parentContext, parentActivity, mLinearLayout);

        JsonObjectRequest postRequest= new JsonObjectRequest(Request.Method.POST, urlApi2, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        rs.stop();
                        ResponceDecision.ResponceSPZ(response.toString(),photoSend, parentActivity, recordID, saving, user);
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rs.stop();
                ResponceDecision.notResponce("problem", parentActivity);
            }
        });
        queue.add(postRequest);
        rs.stopButton(queue);
    }

}
