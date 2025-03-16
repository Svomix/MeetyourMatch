package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

    private List<Tag> tags = new ArrayList<>();
    private Long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_selec);

        //performGetAll(); retrofit
        getFakeTags();

        elementList = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            elementList.add(new Element(i+1, tags.get(i).getName(), false));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ElementAdapter(elementList);
        recyclerView.setAdapter(adapter);

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
            finish();
        });

        buttonContinue = findViewById(R.id.buttonCompleteReg2);
        buttonContinue.setOnClickListener(v -> {
            //performSaveUserTags(); retrofit

            Intent intent = new Intent(InterestSelectionActivity.this, MainScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void performGetAll() {
        TagApiService tagApiService = RetrofitClient.getRetrofit(this).create(TagApiService.class);

        Call<List<Tag>> call = tagApiService.getAllTags();

        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    tags = response.body();
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
            }
        });
    }

    private void performSaveUserTags() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        Call<UserProfileDTO> firstCall = apiServiceAcc.getAccountInfo();
        firstCall.enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    currentUserId = response.body().getId();
                } else {
                    throw new RuntimeException();
                }
            }
            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                throw new RuntimeException();
            }
        });

        UserApiService apiServiceUser = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        for (Element element : elementList) {
            if (element.isSelected()) {
                Call<Void> secondCall = apiServiceUser.createUserAttribute(currentUserId, 1L, element.getName());

                secondCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {

                        } else {
                            throw new RuntimeException();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        throw new RuntimeException();
                    }
                });
            }
        }
    }

    private void getFakeTags() {
        tags.add(new Tag(0L, "Test1"));
        tags.add(new Tag(0L, "Test2"));
        tags.add(new Tag(0L, "Test3"));
        tags.add(new Tag(0L, "Test4"));
        tags.add(new Tag(0L, "Test5"));
        tags.add(new Tag(0L, "Test6"));
    }
}