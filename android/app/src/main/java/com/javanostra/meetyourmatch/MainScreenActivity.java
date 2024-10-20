package com.javanostra.meetyourmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainScreenActivity extends AppCompatActivity {

    ChatFragment chatFragment;
    CalendarFragment calendarFragment;
    MapFragment mapFragment;
    RecomendationsFragment recomendationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        chatFragment = new ChatFragment();
        calendarFragment = new CalendarFragment();
        mapFragment = new MapFragment();
        recomendationsFragment = new RecomendationsFragment();
        loadFragment(chatFragment);
    }

    public void chooseFragment(View view) {
        ImageButton imageButton = (ImageButton) view;
        if (view.getId() == R.id.chatButton) {
            loadFragment(chatFragment);
            imageButton.setImageResource(R.drawable.chosen_chat);
        }
        else if (view.getId() == R.id.rangeButton) {
            loadFragment(calendarFragment);
            imageButton.setImageResource(R.drawable.chosen_range);
        }
        else if (view.getId() == R.id.searchButton) {
            loadFragment(recomendationsFragment);
            imageButton.setImageResource(R.drawable.chosen_search);
        }
        else {
            loadFragment(mapFragment);
            imageButton.setImageResource(R.drawable.chosen_location_pin);
        }

        if (view.getId() != R.id.chatButton)
            ((ImageButton) findViewById(R.id.chatButton)).setImageResource(R.drawable.chat);
        if (view.getId() != R.id.rangeButton)
            ((ImageButton) findViewById(R.id.rangeButton)).setImageResource(R.drawable.range);
        if (view.getId() != R.id.searchButton)
            ((ImageButton) findViewById(R.id.searchButton)).setImageResource(R.drawable.search);
        if (view.getId() != R.id.locationPinButton)
            ((ImageButton) findViewById(R.id.locationPinButton)).setImageResource(R.drawable.location_pin);
    }

    public void openAccount(View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() ==1) {
            finish();
        }else{
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}