package tp_android.tp_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

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
            case "UNEXPECTED_ERROR":
                unexpectedError(jsonResponce,parentActivity);
                break;

            case "FILE_NOT_ALLOWED":
                fileNotAllowed(jsonResponce, parentActivity);
                break;
            case "NO_EGV_INFO_FOUND":
                noEgvFound(jsonResponce, parentActivity);
                break;

            default:
                break;
        }
    }

    private static void noEgvFound(JSONObject jsonResponce, Activity parentActivity) {
        alertError(parentActivity,"Žiadne e-gov data...");
    }

    private static void newEcv (JSONObject jsonResponce, Activity parentActivity, String response, String photoSend, Long recordID, Boolean saving, String user) {
        Database db = new Database(parentActivity);
        db.open();
        JSONObject dataArray = null;
        String db_ecv_default = "";
        String db_user = "";
        String db_time_default = "";

        Cursor c = db.fetchRecord(recordID);
        if (c.moveToFirst()) {
            db_ecv_default = c.getString(c.getColumnIndex("ecv_default"));
            db_user = c.getString(c.getColumnIndex("user"));
            db_time_default = c.getString(c.getColumnIndex("date_time_default"));
        }

        try {
            jsonResponce = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            dataArray = jsonResponce.getJSONObject("data");
            if (saving==true){
                Boolean id = db.updateRecord(recordID, Calendar.getInstance().getTime(),db_time_default, dataArray.getString("plate"),db_ecv_default, photoSend, dataArray.toString(), db_user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent returnIntent = new Intent();
        parentActivity.setResult(Activity.RESULT_OK,returnIntent);

        Intent intent = new Intent(parentActivity, ListItemActivity.class);
        intent.putExtra("response",dataArray.toString());
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
        alertError(parentActivity,"Žiadna ŠPZ ...");
        //alertError(parentActivity,message);
    }

    private static void licencePlateFound (JSONObject jsonResponce, Activity parentActivity, String response, String encodedImage, float ratio) {
        Intent intent = new Intent(parentActivity, ResponceListActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("photoSend", encodedImage);
        intent.putExtra( "ratio", ratio);
        parentActivity.startActivity(intent);
        parentActivity.finish();
    }

    private static void unexpectedError (JSONObject jsonResponce, Activity parentActivity) {
        String message = "";

        try {
            message = jsonResponce.getString("messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alertError(parentActivity,message);
    }

    private static void fileNotAllowed (JSONObject jsonResponce, Activity parentActivity) {
        String message = "";

        try {
            message = jsonResponce.getString("messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        alertError(parentActivity,message);
    }

    public static void notResponce (String message, Activity parentActivity) {
        alertError(parentActivity,message);
    }

    public static void alertError(Activity parentActivity, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle("Chyba");
        builder.setMessage(error);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


}
