package tp_android.tp_android;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public abstract class ResponceDecision {


    public static void Responce (String response, String encodedImage, float ratio, Activity parentActivity) {

        JSONObject jsonResponce = new JSONObject();
        String statusCode = "";

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            statusCode = jsonResponce.getString("status_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch(statusCode) {
            case "NO_LICENCE_PLATE_FOUND":
                noLicencePlateFound(jsonResponce, parentActivity);
                break;

            case "SUCCESS":
                licencePlateFound(jsonResponce, parentActivity, response, encodedImage, ratio);
                break;

            case "UNEXPECTED_ERROR":
                unexpectedError(jsonResponce,parentActivity);
                break;

            case "FILE_NOT_ALLOWED":
                fileNotAllowed(jsonResponce, parentActivity);
                break;

            default:
                break;
        }

    }

    public static void ResponceSPZ (String response, String photoSend, Activity parentActivity, Long recordID, Boolean saving, String user) {

        JSONObject jsonResponce = new JSONObject();
        String statusCode = "";

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            statusCode = jsonResponce.getString("status_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch(statusCode) {
            case "SUCCESS":
                newEcv(jsonResponce, parentActivity, response, photoSend, recordID, saving, user);
                break;

            default:
                break;
        }

    }

    private static void newEcv (JSONObject jsonResponce, Activity parentActivity, String response, String photoSend, Long recordID, Boolean saving, String user) {
        Log.d("responce", response);
        Database db = new Database(parentActivity);
        db.open();
        JSONObject dataArray = null;

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dataArray = jsonResponce.getJSONObject("data");
            if (saving==true){
                Boolean id = db.updateRecord(recordID, Calendar.getInstance().getTime(),dataArray.getString("plate"), photoSend, dataArray.toString(), user);
                Log.d("update?",id.toString() );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(parentActivity, ListItemActivity.class);
        intent.putExtra("response",dataArray.toString());
        //intent.putExtra("coord", coord);
        intent.putExtra("photoSend", photoSend);
        intent.putExtra("recordID", recordID);
        parentActivity.startActivity(intent);
        parentActivity.finish();
    }


    private static void noLicencePlateFound (JSONObject jsonResponce, Activity parentActivity) {
        String message = "";

        try {
            message = jsonResponce.getString("messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(parentActivity, ResponceErrorActivity.class);
        intent.putExtra("message", message);
        parentActivity.startActivity(intent);
    }

    private static void licencePlateFound (JSONObject jsonResponce, Activity parentActivity, String response, String encodedImage, float ratio) {
        Intent intent = new Intent(parentActivity, ResponceListActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("photoSend", encodedImage);
        intent.putExtra( "ratio", ratio);
        parentActivity.startActivity(intent);
    }

    private static void unexpectedError (JSONObject jsonResponce, Activity parentActivity) {
        String message = "";

        try {
            message = jsonResponce.getString("messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(parentActivity, ResponceErrorActivity.class);
        intent.putExtra("message", message);
        parentActivity.startActivity(intent);
    }

    private static void fileNotAllowed (JSONObject jsonResponce, Activity parentActivity) {
        String message = "";

        try {
            message = jsonResponce.getString("messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(parentActivity, ResponceErrorActivity.class);
        intent.putExtra("message", message);
        parentActivity.startActivity(intent);
    }

    //nejde pripojenie, nejde poslatrequest, dlho caka ....

    public static void notResponce (String message, Activity parentActivity) {

        Intent intent = new Intent(parentActivity, ResponceErrorActivity.class);
        intent.putExtra("message", message);
        parentActivity.startActivity(intent);
    }


}
