package com.service.himalaya.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.service.himalaya.Application.Himalaya_applicaation;
import com.service.himalaya.R;
import com.service.himalaya.Services.MyBackgroundService;
import com.service.himalaya.Utils.Common;
import com.service.himalaya.Utils.LocationTrack;
import com.service.himalaya.Utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.service.himalaya.Utils.Common.Logout;

public class WorkerActivity extends AppCompatActivity {

    Button btn_work, btn_end_work;
    LocationTrack locationTrack;
    private ProgressDialog pDialog;
    Geocoder geocoder;
    List<Address> addresses;

    double longitude;
    double latitude;

    String address = "";
    String locality = "";
    String city = "";
    String str_lattitude = "";
    String str_longitude = "";
    private Boolean mRequestingLocationUpdates;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    Timer timer = new Timer();
    ;
    TimerTask hourlyTask;
    Boolean isprocessing = false;
    private JobScheduler jobScheduler;
    public LocationManager manager;
    ImageView logout_ic;
    String android_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        try {
            android_id = Settings.Secure.getString(WorkerActivity.this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            Log.d("android_id", android_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        http://www.androidhive.info/2015/02/android-location-api-using-google-play-services/
        init();
        requestPermission();
        findViews();


        if (PreferenceHelper.getString(Common.start_work, "").equals("start")) {
            btn_end_work.setVisibility(View.VISIBLE);
            btn_work.setVisibility(View.GONE);
        } else {
            btn_end_work.setVisibility(View.GONE);
            btn_work.setVisibility(View.VISIBLE);
        }
        Listners();


    }


    public void GetLocationFirstTime() {


    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            init();
                            startLocationUpdates();
//                            updateLocationUI();

//                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkerActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void startworktimer() {
        PreferenceHelper.putBoolean(Common.istimer_start, true);
        timer = new Timer();
        hourlyTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (isprocessing == false) {
//                            SaveLocation();
                            init();

                            startLocationUpdates();
                            try {
                                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                                if (PreferenceHelper.getString(Common.Start_Date, "").equals(date)) {
                                    SaveLocation(false, false);

                                } else {
                                    SaveLocationEndWork(false, true);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    }
                });

            }
        };
        timer.schedule(hourlyTask, 0l, 10000);

    }

    private void Listners() {
        btn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GetLocationFirstTime();
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                PreferenceHelper.putString(Common.Start_Date, date);
//                Toast.makeText(WorkerActivity.this, date, Toast.LENGTH_SHORT).show();

                if (mCurrentLocation != null) {


                    latitude = mCurrentLocation.getLatitude();
                    longitude = mCurrentLocation.getLongitude();
                    long time = mCurrentLocation.getTime();

                    Log.d("check_lat", String.valueOf(latitude));
                    Log.d("check_long", String.valueOf(longitude));
                    Log.d("time", String.valueOf(time));
                    geocoder = new Geocoder(WorkerActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    address = addresses.get(0).getAddressLine(0);
                    locality = addresses.get(0).getSubLocality();
                    city = addresses.get(0).getLocality();

                    str_lattitude = String.valueOf(latitude);
                    str_longitude = String.valueOf(longitude);

                    Log.d("str_lattitude", str_lattitude);
                    Log.d("str_longitude", str_longitude);

                }
                SaveLocation(true, false);

                startService(new Intent(WorkerActivity.this, MyBackgroundService.class));

//                MyBackgroundService.startForeground();
                PreferenceHelper.putString(Common.start_work, "start");
                btn_work.setVisibility(View.GONE);
                btn_end_work.setVisibility(View.VISIBLE);
                startworktimer();

            }
        });

        logout_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(WorkerActivity.this);
            }
        });


        btn_end_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBackgroundService myBackgroundService = new MyBackgroundService();
                stopService(new Intent(WorkerActivity.this, MyBackgroundService.class));
                SaveLocationEndWork(false, true);

                PreferenceHelper.putString(Common.start_work, "end");
                btn_work.setVisibility(View.VISIBLE);
                btn_end_work.setVisibility(View.GONE);
                PreferenceHelper.putBoolean(Common.istimer_start, false);
                isprocessing = true;

                try {

                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mFusedLocationClient
                        .removeLocationUpdates(mLocationCallback)
                        .addOnCompleteListener(WorkerActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();


                            }
                        });
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
//                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

//                updateLocationUI();
                updateLocationUI();
            }
        };


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(WorkerActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";

                                Toast.makeText(WorkerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    private void findViews() {
        btn_work = findViewById(R.id.btn_work);
        btn_end_work = findViewById(R.id.btn_end_work);
        pDialog = new ProgressDialog(WorkerActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        logout_ic = findViewById(R.id.logout_ic);
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {


            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            long time = mCurrentLocation.getTime();

            Log.d("check_lat", String.valueOf(latitude));
            Log.d("check_long", String.valueOf(longitude));
            Log.d("time", String.valueOf(time));
            geocoder = new Geocoder(WorkerActivity.this, Locale.getDefault());

            try {

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses != null && addresses.size() > 0) {

                    address = addresses.get(0).getAddressLine(0);
                    locality = addresses.get(0).getSubLocality();
                    city = addresses.get(0).getLocality();
                }
                str_lattitude = String.valueOf(latitude);
                str_longitude = String.valueOf(longitude);

                Log.d("str_lattitude", str_lattitude);
                Log.d("str_longitude", str_longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void SaveLocation(final Boolean Isstarted, final Boolean Isstopped) {
        isprocessing = true;


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserID", PreferenceHelper.getString(Common.USER_ID, ""));
            jsonObject.put("Latitude", str_lattitude);
            jsonObject.put("Longitude", str_longitude);
            jsonObject.put("Address", address);
            jsonObject.put("Locality", locality);
            jsonObject.put("City", city);
            jsonObject.put("IsStarted", Isstarted);
            jsonObject.put("IsStopped", Isstopped);
            jsonObject.put("DeviceID", android_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Common.isNetworkAvailable(this)) {

//            showpDialog();
            String url = Common.BASE_URL + Common.SAVE_LOCATION_API;

//            showpDialog();
            JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reader) {
//                            hidepDialog();
                            try {
                                Log.d("response_worker", String.valueOf(reader));
                                Toast.makeText(WorkerActivity.this, "Done", Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
//                            hidepDialog();
                        }
                    });

            Himalaya_applicaation.getInstance().addToRequestQueue(jobReq);

        } else {
            Toast.makeText(WorkerActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();
        }
        isprocessing = false;
    }

    public void SaveLocationEndWork(final Boolean Isstarted, final Boolean Isstopped) {
        isprocessing = true;


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserID", PreferenceHelper.getString(Common.USER_ID, ""));
            jsonObject.put("Latitude", str_lattitude);
            jsonObject.put("Longitude", str_longitude);
            jsonObject.put("Address", address);
            jsonObject.put("Locality", locality);
            jsonObject.put("City", city);
            jsonObject.put("IsStarted", Isstarted);
            jsonObject.put("IsStopped", Isstopped);
            jsonObject.put("DeviceID", android_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Common.isNetworkAvailable(this)) {

//            showpDialog();
            String url = Common.BASE_URL + Common.SAVE_LOCATION_API;

//            showpDialog();
            JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reader) {
//                            hidepDialog();
                            try {
                                Log.d("response_worker", String.valueOf(reader));
                                Toast.makeText(WorkerActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                android.os.Process.killProcess(android.os.Process.myPid());


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
//                            hidepDialog();
                        }
                    });

            Himalaya_applicaation.getInstance().addToRequestQueue(jobReq);

        } else {
            Toast.makeText(WorkerActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();
        }
        isprocessing = false;
    }

//    private void showpDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }

//    private void hidepDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }


}
