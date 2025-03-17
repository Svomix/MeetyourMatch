package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        TextView name = findViewById(R.id.username);
        name.setText(getIntent().getExtras().get("username").toString());

        TextView town = findViewById(R.id.hometown);
        town.setText(getIntent().getExtras().get("city").toString());

        TextView email = findViewById(R.id.email);
        email.setText(getIntent().getExtras().get("email").toString());
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

    public void changeInterests(View view) {
        Intent intent = new Intent(this, InterestSelectionActivity.class);
        intent.putExtra("previousActivity", "Account");
        startActivity(intent);
    }
}