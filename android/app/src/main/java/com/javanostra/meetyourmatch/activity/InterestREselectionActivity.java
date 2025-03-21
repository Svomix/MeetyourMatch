package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestREselectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementAdapter adapter;
    private List<Element> elementList;
    private EditText searchEditText;

    private Button buttonContinue;

    private List<Tag> tags;
    private Long currentUserId;
    private List<Tag> currentUserTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_reselec);

        tags = new ArrayList<>();
        elementList = new ArrayList<>();

        if(currentUserTags != null) currentUserTags.clear();
        loadTags(() ->
                performGetAccountInfo(() ->
                        performGetUserTags(currentUserId)
                )
        );

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.buttonClose3).setOnClickListener(v -> {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        });

        buttonContinue = findViewById(R.id.buttonCompleteReg2);
        buttonContinue.setOnClickListener(v -> performSaveUserTags());
    }

    private void loadTags(Runnable onComplete) {
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

                    onComplete.run();
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

    private void performGetUserTags(Long userId) {
        UserApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiServiceAcc.getUserTags(userId).enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    Log.d("GetUserTags", "Successful: " + response.message());
                    currentUserTags = response.body();

                    if (!currentUserTags.isEmpty()) {
                        for (Element element : elementList) {
                            for (Tag tag : currentUserTags) {
                                if (element.getName().substring(1).equals(tag.getName()))
                                    element.setSelected(true);
                            }
                        }
                    }
                    adapter = new ElementAdapter(elementList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("GetUserTags", "Error: " + response.message() + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Log.e("GetUserTags", "Network error: " + t.getMessage());
            }
        });
    }

    private void performSaveUserTags() {
        UserApiService apiServiceUser = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        for (Element element : elementList) {
            if (element.isSelected() && !isContaining(element)) {
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
            } else if (!element.isSelected() && isContaining(element)) {
                apiServiceUser.deleteUserAttribute(currentUserId, 1L, Long.toString(element.getId())).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("DeleteUserTags", "Successful: " + currentUserId + " " + 1 + " " + element.getId());
                        } else {
                            Log.d("DeleteUserTags", "Error: " + response.errorBody().toString() + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("DeleteUserTags", "Network error: " + t.getMessage());
                    }
                });
            }
        }

        setResult(RESULT_OK, new Intent());
        finish();
        //performGetUserTags(currentUserId);
    }

    private boolean isContaining(Element element) {
        if (currentUserTags == null) return false;
        for (Tag tag : currentUserTags) {
            if (tag.getName().equals(element.getName().substring(1))) return true;
        }
        return false;
    }
}