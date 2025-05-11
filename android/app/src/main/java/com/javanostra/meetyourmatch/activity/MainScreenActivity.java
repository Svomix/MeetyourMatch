package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.fragment.CalendarFragment;
import com.javanostra.meetyourmatch.fragment.ChatFragment;
import com.javanostra.meetyourmatch.fragment.EventSearchFragment;
import com.javanostra.meetyourmatch.fragment.MapFragment;
import com.javanostra.meetyourmatch.fragment.RecomendationsFragment;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreenActivity extends AppCompatActivity implements RecomendationsFragment.OnRecommendationsInteractionListener, FragmentManager.OnBackStackChangedListener {

    private static final int[] BUTTON_IDS = {
            R.id.chatButton,
            R.id.rangeButton,
            R.id.recsButton,
            R.id.searchButton,
            R.id.locationPinButton
    };

    private static final int[] BUTTON_IMAGES = {
            R.drawable.chat,
            R.drawable.range,
            R.drawable.recs,
            R.drawable.search,
            R.drawable.location_pin
    };

    private static final int[] BUTTON_IMAGES_CHOSEN = {
            R.drawable.chosen_chat,
            R.drawable.chosen_range,
            R.drawable.chosen_recs,
            R.drawable.chosen_search,
            R.drawable.chosen_location_pin
    };

    private ChatFragment chatFragment;
    private CalendarFragment calendarFragment;
    private MapFragment mapFragment;
    private RecomendationsFragment recomendationsFragment;
    private EventSearchFragment searchFragment;
    private UserProfileDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        recomendationsFragment = new RecomendationsFragment();
        chatFragment = new ChatFragment();
        calendarFragment = new CalendarFragment();
        mapFragment = new MapFragment();
        searchFragment = new EventSearchFragment();

        loadFragment(recomendationsFragment);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private void getCurrentUser() {
        AccountApiService userApiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);
        Call<UserProfileDTO> call = userApiService.getAccountInfo();
        call.enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDTO> call, @NonNull Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainScreenActivity.this, "Удачно взят юзер", Toast.LENGTH_SHORT).show();
                    currentUser = response.body();
                } else {
                    Toast.makeText(MainScreenActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDTO> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(MainScreenActivity.this, "Ошибка сети GET: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chooseFragment(View view) {
        int selectedButtonId = view.getId();
        resetButtonImages();

        for (int i = 0; i < BUTTON_IDS.length; i++) {
            if (selectedButtonId == BUTTON_IDS[i]) {
                ((ImageButton) view).setImageResource(BUTTON_IMAGES_CHOSEN[i]);
                loadFragment(getFragmentById(selectedButtonId));
                break;
            }
        }
    }

    private Fragment getFragmentById(int buttonId) {
        if (buttonId == R.id.rangeButton) {
            return calendarFragment;
        } else if (buttonId == R.id.recsButton) {
            return recomendationsFragment;
        } else if (buttonId == R.id.searchButton) {
            return searchFragment;
        } else if (buttonId == R.id.locationPinButton) {
            return mapFragment;
        } else {
            return chatFragment;
        }
    }

    private void resetButtonImages() {
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            ((ImageButton) findViewById(BUTTON_IDS[i])).setImageResource(BUTTON_IMAGES[i]);
        }
    }

    public void openAccount(View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }

    public void openNotifications(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    @Override
    public void onSwitchToSearch() {
        loadFragment(searchFragment);
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount <= 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment == null) {
            finish();
            return;
        }

        resetButtonImages();

        if (currentFragment instanceof ChatFragment) {
            ((ImageButton) findViewById(R.id.chatButton)).setImageResource(R.drawable.chosen_chat);
        } else if (currentFragment instanceof CalendarFragment) {
            ((ImageButton) findViewById(R.id.rangeButton)).setImageResource(R.drawable.chosen_range);
        } else if (currentFragment instanceof RecomendationsFragment) {
            ((ImageButton) findViewById(R.id.recsButton)).setImageResource(R.drawable.chosen_recs);
        } else if (currentFragment instanceof EventSearchFragment) {
            ((ImageButton) findViewById(R.id.searchButton)).setImageResource(R.drawable.chosen_search);
        } else if (currentFragment instanceof MapFragment) {
            ((ImageButton) findViewById(R.id.locationPinButton)).setImageResource(R.drawable.chosen_location_pin);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}