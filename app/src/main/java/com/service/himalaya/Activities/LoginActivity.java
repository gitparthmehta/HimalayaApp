package com.service.himalaya.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.service.himalaya.Application.Himalaya_applicaation;
import com.service.himalaya.R;
import com.service.himalaya.Utils.Common;
import com.service.himalaya.Utils.PreferenceHelper;
import com.service.himalaya.enamclass.Roll_id_ENUM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    Geocoder geocoder;
    List<Address> addresses;
    Button btn_signin;
    EditText et_email, et_password;
    private ProgressDialog pDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);


        findViews();
        Listners();
        GetLocation();
    }

    private void GetLocation() {
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(21.194032, 72.805983, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String sub_loaclity = addresses.get(0).getSubLocality();
            Log.d("sub_locality", sub_loaclity);
            Log.d("mAdminArea", addresses.get(0).getAdminArea());
            Log.d("mSubAdminArea", addresses.get(0).getSubAdminArea());
            Log.d("mPostalCode", addresses.get(0).getPostalCode());
            Log.d("mCountryCode", addresses.get(0).getCountryCode());
            Log.d("mCountryName", addresses.get(0).getCountryName());
            Log.d("getFeatureName", addresses.get(0).getFeatureName());
            Log.d("getAdminArea", addresses.get(0).getAdminArea());
            Log.d("getSubAdminArea", addresses.get(0).getSubAdminArea());

            Log.d("address", address);
            Log.d("city", city);
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        System.exit(0);

    }

    private void findViews() {

        try {
            String android_id = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            Log.d("android_id",android_id);

        }catch (Exception e){
            e.printStackTrace();
        }
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btn_signin = findViewById(R.id.btn_signin);
        et_password = findViewById(R.id.et_password);
        et_email = findViewById(R.id.et_email);
    }

    private void Listners() {
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (et_email.getText().toString().equalsIgnoreCase("Worker")) {
//                    Intent intent = new Intent(LoginActivity.this, WorkerActivity.class);
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
//                    startActivity(intent);
//                }


                if (et_email.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter  Username", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter  Password", Toast.LENGTH_SHORT).show();

                } else {
                    SubmitLoginData(et_email.getText().toString(), et_password.getText().toString());
                }
            }
        });
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void SubmitLoginData(String email_id, String password) {

        String myServer = "";


        myServer = Common.BASE_URL + Common.LOGIN_URL + "Username=" + email_id + "&Password=" + password;

        Log.e("Login_url :", myServer);
        showpDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                myServer,
                null,
                new Response.Listener<JSONObject>() {
                    private TextView text1;

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response_login", String.valueOf(response));
                        try {
                            String apiStatus = response.getString("apiStatus");
                            String apiMessage = response.getString("apiMessage");


                            Toast.makeText(LoginActivity.this, apiMessage, Toast.LENGTH_SHORT).show();
                            JSONObject obj = response.getJSONObject("data");


                            if (obj.length() > 0) {
                                String userName = obj.getString("displayName");
                                int roleID = obj.getInt("roleID");
                                String userID = obj.getString("userID");
                                PreferenceHelper.putString(Common.USER_ID, userID);
                                PreferenceHelper.putString(Common.USER_NAME, userName);
                                PreferenceHelper.putInt(Common.ROLE_ID, roleID);


                                if (roleID > 2) {

                                    Intent intent = new Intent(LoginActivity.this, WorkerActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
                                    startActivity(intent);
                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("error", String.valueOf(error));
                        // Do something when error occurred
                        hidepDialog();

                    }
                }

        );
        Himalaya_applicaation.getInstance().addToRequestQueue(jsonObjReq);

    }

}
