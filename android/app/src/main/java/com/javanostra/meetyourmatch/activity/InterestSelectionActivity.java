package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.ElementAdapter;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.TagApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.Element;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementAdapter adapter;
    private List<Element> elementList;
    private EditText searchEditText;

    private Button buttonContinue;

    private List<Tag> tags;
    private Long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_selec);

        tags = new ArrayList<>();
        elementList = new ArrayList<>();
        loadTags();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.buttonClose3).setOnClickListener(v -> {
            Intent intent = new Intent(InterestSelectionActivity.this, MainScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        buttonContinue = findViewById(R.id.buttonCompleteReg2);
        buttonContinue.setOnClickListener(v -> {
            performGetAccountInfo(this::performSaveUserTags);
        });
    }

    private void loadTags() {
        TagApiService apiService = RetrofitClient.getRetrofit(this).create(TagApiService.class);

        apiService.getAllTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TagsLoader", "Success: " + response.body());
                    tags.addAll(response.body());
                    elementList.clear();
                    for (int i = 0; i < tags.size(); i++) {
                        elementList.add(new Element(i + 1, "#" + tags.get(i).getName(), false));
                    }
                    adapter = new ElementAdapter(elementList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d("TagsLoader", "Load error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Log.d("TagsLoader", "Network error: " + t.getMessage());
            }
        });
    }

    private void performGetAccountInfo(Runnable onComplete) {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.body().getUsername());
                    currentUserId = response.body().getId();
                    onComplete.run();
                } else {
                    Log.d("GetAccountInfo", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                Log.d("GetAccountInfo", "Network error: " + t.getMessage());
            }
        });
    }

    private void performSaveUserTags() {
        UserApiService apiServiceUser = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        for (Element element : elementList) {
            if (element.isSelected()) {
                apiServiceUser.createUserAttribute(currentUserId, 1L, element.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("SaveUserTags", "Successful: " + currentUserId + " " + 1 + " " + element.getId());
                        } else {
                            Log.d("SaveUserTags", "Error: " + response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("SaveUserTags", "Network error: " + t.getMessage());
                    }
                });
            }
        }

        Intent intent = new Intent(InterestSelectionActivity.this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}