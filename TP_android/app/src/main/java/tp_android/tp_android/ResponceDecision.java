package tp_android.tp_android;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class ResponceDecision {


    public static void Responce (String response, String encodedImage, Activity parentActivity) {

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

            case "LICENCE_PLATE_FOUND":
                licencePlateFound(jsonResponce, parentActivity, response, encodedImage);
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

    private static void licencePlateFound (JSONObject jsonResponce, Activity parentActivity, String response, String encodedImage) {
        Intent intent = new Intent(parentActivity, ResponceListActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("photoSend", encodedImage);
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
