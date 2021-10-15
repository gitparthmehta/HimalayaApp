package com.service.himalaya.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.service.himalaya.Adapters.SubUserAdapter;
import com.service.himalaya.Adapters.UserListAdapter;
import com.service.himalaya.Application.Himalaya_applicaation;
import com.service.himalaya.ItemClickListner.SubUserClickListner;
import com.service.himalaya.Models.Sub_UserListModel;
import com.service.himalaya.Models.UserListModel;
import com.service.himalaya.R;
import com.service.himalaya.Utils.Common;
import com.service.himalaya.Utils.PreferenceHelper;
import com.service.himalaya.enamclass.Roll_id_ENUM;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static com.service.himalaya.Utils.Common.Logout;


public class UserListActivity extends AppCompatActivity implements SubUserClickListner {

    RecyclerView user_list;
    ArrayList<UserListModel> userListModels = new ArrayList<>();
    ArrayList<Sub_UserListModel> subUserListModels = new ArrayList<>();
    private ProgressDialog pDialog;
    UserListAdapter adapter;
    TextView sub_user_name;
    LinearLayout name_layout;
    ImageView back;
    String parent_id = "";
    String parent_name = "";
    boolean doubleBackToExitPressedOnce = false;
    ImageView logout_ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        findviews();
        GetUserList();
        Listners();
    }

    private void Listners() {


        logout_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(UserListActivity.this);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_layout.setVisibility(VISIBLE);
//                GetUserList_click(id, name);
                Log.d("parent_name", parent_name);
                Log.d("p_id", parent_id);
//                sub_user_name.setText(name);

                if (PreferenceHelper.getString(Common.USER_ID, "").equals(parent_id)) {
                    back.setVisibility(View.GONE);
                    GetUserList_click(parent_id, parent_name);
                    sub_user_name.setText(parent_name);

                } else {
                    back.setVisibility(VISIBLE);
                    GetUserList_click(parent_id, parent_name);
                    sub_user_name.setText(parent_name);

                }
            }
        });
    }

    private void findviews() {

        pDialog = new ProgressDialog(UserListActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        user_list = findViewById(R.id.user_list);
        logout_ic = findViewById(R.id.logout_ic);
        user_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new UserListAdapter(userListModels, UserListActivity.this, UserListActivity.this);
        sub_user_name = findViewById(R.id.sub_user_name);
        name_layout = findViewById(R.id.name_layout);
        back = findViewById(R.id.back);
        sub_user_name.setText(PreferenceHelper.getString(Common.USER_NAME, ""));

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void GetUserList() {
        back.setVisibility(View.GONE);
        String myServer = "";

        if (userListModels.size() > 0) {
            userListModels.clear();
        }
        myServer = Common.BASE_URL + Common.USERLIST_API + "UserID=" + PreferenceHelper.getString(Common.USER_ID, "") + "&LoginUserID=" + PreferenceHelper.getString(Common.USER_ID, "");

        Log.e("Login_url :", myServer);
        showpDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(UserListActivity.this);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                myServer,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray apppage_obj = null;

                        Log.d("response_login", String.valueOf(response));
                        try {
                            String apiStatus = response.getString("apiStatus");
                            String apiMessage = response.getString("apiMessage");


//                            Toast.makeText(UserListActivity.this, apiMessage, Toast.LENGTH_SHORT).show();

                            JSONObject data_obj = response.getJSONObject("data");
                            apppage_obj = data_obj.getJSONArray("users");

                            JSONObject parentUserdetail = null;

                            if (!data_obj.isNull("parentUserdetail")) {
                                parentUserdetail = data_obj.getJSONObject("parentUserdetail");

                                if (parentUserdetail.length() > 0) {
                                    parent_id = parentUserdetail.getString("parentUserID");
                                    parent_name = parentUserdetail.getString("parentUserName");
                                }
                            } else {
                                parent_id = "";
                                parent_name = "";
                            }


                            if (apppage_obj.length() > 0) {


                                for (int i = 0; i <= apppage_obj.length(); i++) {
                                    JSONObject obj = apppage_obj.getJSONObject(i);

                                    UserListModel userListModel = new UserListModel();
                                    userListModel.setUserName(obj.getString("displayName"));
                                    userListModel.setUserId(obj.getString("userId"));
                                    userListModel.setIsParent(obj.getString("isParent"));
                                    userListModel.setRoleId(obj.getString("roleId"));
                                    userListModels.add(userListModel);

                                }
                            }


                            Log.d("scholllist_arr", String.valueOf(userListModels.size()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        user_list.setAdapter(adapter);

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

    public void GetUserList_click(String user_id_click, String sub_username) {

        String myServer = "";

        if (userListModels.size() > 0) {
            userListModels.clear();
        }
        myServer = Common.BASE_URL + Common.USERLIST_API + "UserID=" + user_id_click + "&LoginUserID=" + PreferenceHelper.getString(Common.USER_ID, "");

        Log.e("Login_url :", myServer);
        showpDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(UserListActivity.this);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                myServer,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray apppage_obj = null;

                        Log.d("response_login", String.valueOf(response));
                        try {
                            String apiStatus = response.getString("apiStatus");
                            String apiMessage = response.getString("apiMessage");


//                            Toast.makeText(UserListActivity.this, apiMessage, Toast.LENGTH_SHORT).show();

                            JSONObject data_obj = response.getJSONObject("data");
                            apppage_obj = data_obj.getJSONArray("users");


                            JSONObject parentUserdetail = null;

                            if (!data_obj.isNull("parentUserdetail")) {
                                parentUserdetail = data_obj.getJSONObject("parentUserdetail");

                                if (parentUserdetail.length() > 0) {
                                    parent_id = parentUserdetail.getString("parentUserID");
                                    parent_name = parentUserdetail.getString("parentUserName");
                                }
                            } else {
                                parent_id = "";
                                parent_name = "";
                            }


                            if (apppage_obj.length() > 0) {
                                for (int i = 0; i < apppage_obj.length(); i++) {
                                    JSONObject obj = apppage_obj.getJSONObject(i);

                                    UserListModel userListModel = new UserListModel();
                                    userListModel.setUserName(obj.getString("displayName"));
                                    userListModel.setUserId(obj.getString("userId"));
                                    userListModel.setIsParent(obj.getString("isParent"));
                                    userListModel.setRoleId(obj.getString("roleId"));
                                    userListModels.add(userListModel);

                                }
                            }
                            Log.d("scholllist_arr", String.valueOf(userListModels.size()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        user_list.setAdapter(adapter);


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
    public void SubUserClickListner(String id, String name) {
        name_layout.setVisibility(VISIBLE);
        GetUserList_click(id, name);
        back.setVisibility(VISIBLE);

        sub_user_name.setText(name);
    }

    @Override
    public void onBackPressed() {
        name_layout.setVisibility(VISIBLE);
//                GetUserList_click(id, name);
        Log.d("parent_name", parent_name);
        Log.d("p_id", parent_id);
//                sub_user_name.setText(name);

        if (back.getVisibility() == View.GONE) {

            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);


        } else {
            if (PreferenceHelper.getString(Common.USER_ID, "").equals(parent_id)) {
                back.setVisibility(View.GONE);
                GetUserList_click(parent_id, parent_name);
                sub_user_name.setText(parent_name);

            } else {
                back.setVisibility(VISIBLE);
                GetUserList_click(parent_id, parent_name);
                sub_user_name.setText(parent_name);

            }
        }
    }

}
