package com.service.himalaya.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.service.himalaya.R;
import com.service.himalaya.Utils.Common;
import com.service.himalaya.Utils.PreferenceHelper;
import com.service.himalaya.enamclass.Roll_id_ENUM;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (PreferenceHelper.getString(Common.USER_ID, "").equals("")) {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                } else if (PreferenceHelper.getInt(Common.ROLE_ID, 0) > 2) {
                    Intent i = new Intent(SplashActivity.this, WorkerActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, UserListActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
