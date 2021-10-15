package com.service.himalaya.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.widget.Button;

import com.service.himalaya.Activities.LoginActivity;
import com.service.himalaya.Activities.UserListActivity;
import com.service.himalaya.R;

public class Common {


    public static final String start_work = "start_work";
    public static final String USER_ID = "USER_ID";
    public static final String ROLE_ID = "ROLE_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String BASE_URL = "http://182.54.150.35:100/Himalaya/";
//    public static final String BASE_URL = "http:192.168.0.7:100/Himalaya/";

    public static final String LOGIN_URL = "Login?";
    public static final String USERLIST_API = "GetUserList?";
    public static final String USERCURRENT_LOCATION_API = "GetUserCurrentLocation?";
    public static final String SAVE_LOCATION_API = "SaveLocationData";
    public static final String PARENT_ID = "PARENT_ID?";
    public static final String Start_Date = "Start_Date";
    public static String  istimer_start ;


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    public static void Logout(final Activity activity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity, R.style.MyDialogTheme);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        PreferenceHelper.putString(Common.USER_ID, "");
                        PreferenceHelper.putString(Common.USER_NAME, "");
                        PreferenceHelper.putInt(Common.ROLE_ID, 0);
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLACK);
    }
}
