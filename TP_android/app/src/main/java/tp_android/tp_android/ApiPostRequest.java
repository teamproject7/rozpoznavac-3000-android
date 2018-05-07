package tp_android.tp_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public abstract class ApiPostRequest {

   // private static final String urlApi1 =  "http://108.61.179.124:7486/spz_img/";
    //private static final String urlApi2 =  "http://108.61.179.124:7486/spz/";
    private static final String urlApi1 =  "http://108.61.179.124:7486/api/v1.0/spz_img/";
    private static final String urlApi2 =  "http://108.61.179.124:7486/api/v1.0/spz/";
    private static final String MY_PREFS_NAME = "Setting";

    public static void PostApi1(final Bitmap photo, final Bitmap store_photo, final float ratio, String user, final Activity parentActivity, final Context parentContext, CoordinatorLayout mLinearLayout) {
        if (isNetworkAvailable(parentActivity)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapHelper.toGrayscale(photo).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            final Map<String, String> params = new HashMap<String, String>();
            params.put("name", user);
            params.put("image", encodedImage);

            final RequestQueue queue = Volley.newRequestQueue(parentActivity);
            final RequestSending rs = new RequestSending(parentContext, parentActivity, mLinearLayout);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlApi1, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            store_photo.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                            byte[] imageBytes2 = baos2.toByteArray();
                            String encodedImage2 = Base64.encodeToString(imageBytes2, Base64.DEFAULT);
                            rs.stop();
                            sendingEventRequesting(parentActivity);
                            ResponceDecision.Responce(response.toString(), encodedImage2, ratio, parentActivity);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    rs.stop();
                    sendingEventRequesting(parentActivity);
                    ResponceDecision.notResponce("Chyba na strane servera", parentActivity);
                }
            });
            queue.add(postRequest);
            rs.stopButton(queue,parentActivity,parentContext);
        }
        else {
            sendingEventRequesting(parentActivity);
            ResponceDecision.notResponce("Žiadne internetové pripojenie", parentActivity);
        }
    }

    public static void PostApi2(String ecv, final Boolean saving, final String user, final long recordID, final Activity parentActivity, final Context parentContext, CoordinatorLayout mLinearLayout, final String photoSend) {
        if (isNetworkAvailable(parentActivity)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", user);
            params.put("plate_string", ecv);

            final RequestQueue queue = Volley.newRequestQueue(parentActivity);
            final RequestSending rs = new RequestSending(parentContext, parentActivity, mLinearLayout);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlApi2, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            rs.stop();
                            sendingEventRequesting(parentActivity);
                            ResponceDecision.ResponceSPZ(response.toString(), photoSend, parentActivity, recordID, saving, user);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    rs.stop();
                    sendingEventRequesting(parentActivity);
                    ResponceDecision.notResponce("Chyba na strane servera", parentActivity);
                }

            });
            queue.add(postRequest);
            rs.stopButton(queue,parentActivity,parentContext);
        }
        else {
            sendingEventRequesting(parentActivity);
            ResponceDecision.notResponce("Žiadne internetové pripojenie", parentActivity);
        }
    }

    private static void sendingEventRequesting(Activity parentActivity) {
        SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("posielanie",false);
        editor.apply();
    }

    private static boolean isNetworkAvailable(Activity parentActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
