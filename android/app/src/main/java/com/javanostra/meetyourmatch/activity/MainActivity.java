package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieManager cookieManager = new CookieManager(this);
        String token = cookieManager.getCookie();

        Intent intent;
        if (token != null && !token.isEmpty()) {
            intent = new Intent(MainActivity.this, MainScreenActivity.class);
        } else {
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(intent);

        finish();
    }
}