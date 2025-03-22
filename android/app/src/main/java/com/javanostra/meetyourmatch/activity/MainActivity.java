package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieManager cookieManager = new CookieManager(this);
        String token = cookieManager.getCookie();

        if (token != null && !token.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }
}