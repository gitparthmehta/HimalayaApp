package com.service.himalaya.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.service.himalaya.Application.Himalaya_applicaation;
import com.service.himalaya.R;
import com.service.himalaya.Utils.Common;
import com.service.himalaya.Utils.PreferenceHelper;
import com.service.himalaya.enamclass.Roll_id_ENUM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class UserLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ProgressDialog pDialog;
    TextView username_txt;
    String user_name = "";
    String user_id = "";
    private GoogleMap map;
    SupportMapFragment mapFragment;
    private String dealLat, dealLang, address;
    ImageView back;
    Boolean isprocessing = false;
    Timer timer;
    TimerTask hourlyTask;
    String android_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        findViews();


        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_name = intent.getStringExtra("user_name");
        username_txt.setText(user_name);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (isprocessing == false) {
            GetLocation();

        }
        timer = new Timer();
        hourlyTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (isprocessing == false) {
                            GetLocation();

                        }

                    }
                });

            }
        };
        timer.schedule(hourlyTask, 0l, 10000);


        Listners();

    }

    private void Listners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void findViews() {
        pDialog = new ProgressDialog(UserLocationActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        username_txt = findViewById(R.id.username_txt);
        back = findViewById(R.id.back);


    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void GetLocation() {
        isprocessing = true;
        String myServer = "";


        myServer = Common.BASE_URL + Common.USERCURRENT_LOCATION_API + "UserID=" + user_id;

        Log.e("location_url :", myServer);
        showpDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                myServer,
                null,
                new Response.Listener<JSONObject>() {
                    private TextView text1;

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response_location", String.valueOf(response));
                        try {
                            String apiStatus = response.getString("apiStatus");
                            String apiMessage = response.getString("apiMessage");


//                            Toast.makeText(UserLocationActivity.this, apiMessage, Toast.LENGTH_SHORT).show();


                            if (response.getJSONObject("data") != null) {
                                JSONObject obj = response.getJSONObject("data");
                                if (obj.length() > 0) {
                                    String locationID = obj.getString("locationID");
                                    dealLat = obj.getString("latitude");
                                    dealLang = obj.getString("longitude");
                                    address = obj.getString("address");
                                    String city = obj.getString("city");
                                    String locality = obj.getString("locality");
                                    String currentDateTime = obj.getString("currentDateTime");
                                    String spentTime = obj.getString("spentTime");

                                } else {

                                }

                                mapFragment.getMapAsync(UserLocationActivity.this);

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            isprocessing = false;

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (dealLat.equals("null")) {
                dealLat = "0";
            }
        } catch (NullPointerException e) {
            dealLat = "0";
        }
        try {
            if (dealLang.equals("null")) {
                dealLang = "0";
            }
        } catch (NullPointerException e) {
            dealLang = "0";
        }
        LatLng sydney = new LatLng(Double.parseDouble(dealLat), Double.parseDouble(dealLang));

        Log.i("latelong: ", dealLat.toString() + "long " + dealLang.toString());
        map = googleMap;
        //map.setMapType(M);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Toast.makeText(UserLocationActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        }

        //map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 100));

        Marker marker = map.addMarker(new MarkerOptions()
                .title(address)
                .snippet(address)
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));


        MapInfoWindowAdapter markerWindowView = new MapInfoWindowAdapter(UserLocationActivity.this, "");
        googleMap.setInfoWindowAdapter(markerWindowView);
        marker.showInfoWindow();


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(UserLocationActivity.this, MapsActivity.class);
//                intent.putExtra("address", dealaddress);
//                intent.putExtra("title", restaurant.getText().toString());
//                intent.putExtra("phone", phone_text);
//                intent.putExtra("dealLat", dealLat);
//                intent.putExtra("dealLang", dealLang);
//                intent.putExtra("mapimage", mapimage);
//                startActivity(intent);
            }
        });

        GoogleMap.OnMarkerClickListener onMarkerClickedListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                return true;
            }
        };


    }

    class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private LayoutInflater inflater;
        private Context context;
        String image_str;
        View v;

        public MapInfoWindowAdapter(UserLocationActivity context, String mapimage) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            this.image_str = mapimage;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            try {
                v = inflater.inflate(R.layout.custom_info_contents, null);
            } catch (Exception e) {
                e.printStackTrace();
                // map is already there
            }

            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(address);

            ImageView iv = (ImageView) v.findViewById(R.id.markerImage);
//            Uri uri = Uri.parse(mapimage);
//            iv.setImageURI(uri);

//            Glide.with(getApplicationContext()).load(Uri.parse(image_str)).into(iv);
            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {


            return v;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hourlyTask.cancel();
    }
}
