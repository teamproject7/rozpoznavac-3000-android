package tp_android.tp_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.sip.SipSession;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;


public abstract class ApiPostRequest {

    private static String urlApi1 =  "http://108.61.179.124:7486/spz_img/";
    private static String urlApi2 =  "http://108.61.179.124:7486/spz/";

    public static final String MY_PREFS_NAME = "Setting";

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
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            store_photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            String encodedImage2 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            rs.stop();
                            SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                            editor.putBoolean("posielanie",false);
                            editor.apply();
                            ResponceDecision.Responce(response.toString(), encodedImage2, ratio, parentActivity);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    rs.stop();
                    SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putBoolean("posielanie",false);
                    editor.apply();
                    ResponceDecision.notResponce("Server problem", parentActivity);
                }
            });
            queue.add(postRequest);
            rs.stopButton(queue,parentActivity,parentContext);
        }
        else {
            SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean("posielanie",false);
            editor.apply();
            ResponceDecision.notResponce("No internet connection", parentActivity);
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
                            SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                            editor.putBoolean("posielanie",false);
                            editor.apply();
                            ResponceDecision.ResponceSPZ(response.toString(), photoSend, parentActivity, recordID, saving, user);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    rs.stop();
                    SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putBoolean("posielanie",false);
                    editor.apply();
                    ResponceDecision.notResponce("Server problem", parentActivity);
                }

            });
            queue.add(postRequest);
            rs.stopButton(queue,parentActivity,parentContext);
        }
        else {
            SharedPreferences.Editor editor = parentActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean("posielanie",false);
            editor.apply();
            ResponceDecision.notResponce("No internet connection", parentActivity);
        }

    }

    private static boolean isNetworkAvailable(Activity parentActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
