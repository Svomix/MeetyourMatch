package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
    }

    public void goBack(View view) {
        finish();
    }

    public void exitAccount(View view) {
        CookieManager cookieManager = new CookieManager(this);
        cookieManager.saveCookie("");

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}