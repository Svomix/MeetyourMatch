package com.javanostra.meetyourmatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.ElementAdapter;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.TagApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.entity.Element;
import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.UserInterest;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementAdapter adapter;
    private List<Element> elementList;
    private EditText searchEditText;
    private Button buttonContinue;

    private List<Interest> interests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_selec);

        String previousActivity = getIntent().getExtras().get("previousActivity").toString();
        performGetAllInterests();

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
            performSaveUserTags();
            if (previousActivity.equals("Account")) {
                finish();
            } else if (previousActivity.equals("Register")) {
                Intent intent = new Intent(InterestSelectionActivity.this, MainScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performGetAllInterests() {
        UserApiService userApiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        Call<List<Interest>> call = userApiService.getAllInterests();
        Context context = this;
        call.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if (response.isSuccessful()) {
                    interests = response.body();
                    Toast.makeText(InterestSelectionActivity.this, R.string.successfulInterest, Toast.LENGTH_SHORT).show();

                    elementList = new ArrayList<>();
                    for (int i = 0; i < interests.size(); i++) {
                        elementList.add(new Element(i+1, interests.get(i).getName(), false));
                    }

                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    adapter = new ElementAdapter(elementList);
                    recyclerView.setAdapter(adapter);

                    performGetUserInterests();
                } else {
                    Toast.makeText(InterestSelectionActivity.this, R.string.invalidInterest, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                Toast.makeText(InterestSelectionActivity.this, R.string.connectionError + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performGetUserInterests() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);
        Call<Set<UserInterest>> firstCall = apiServiceAcc.getMyInterests();
        firstCall.enqueue(new Callback<Set<UserInterest>>() {
            @Override
            public void onResponse(Call<Set<UserInterest>> call, Response<Set<UserInterest>> response) {
                if (response.isSuccessful()) {
                    response.body().forEach(System.out::println);
                    Toast.makeText(InterestSelectionActivity.this, R.string.successfulInterest, Toast.LENGTH_SHORT).show();
                    setAllUserInterests(response.body());

                } else {
                    Toast.makeText(InterestSelectionActivity.this, R.string.invalidInterest, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Set<UserInterest>> call, Throwable t) {
                Toast.makeText(InterestSelectionActivity.this, R.string.connectionError + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllUserInterests(Set<UserInterest> userInterests) {
        int size = adapter.getItemCount();
        for (int i = 0; i < size; ++i) {
            boolean hasInterest = false;
            for (UserInterest interest : userInterests) {
                if (interest.getName().equals(adapter.getElement(i).getName())) {
                    hasInterest = true;
                    break;
                }
            }
            if (hasInterest) adapter.setElementSelected(i);
        }
        adapter.sortElementsForDisplay();
        adapter.notifyDataSetChanged();
    }

    private void performSaveUserTags() {
        //TODO: change to more effective method
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);
        int size = adapter.getItemCount();
        Call<String> firstCall;
        for (int i = 0; i < size; ++i) {
            Element element = adapter.getElement(i);
            if (element.isSelected()) {
                firstCall = apiServiceAcc.addInterest((int)element.getId());
            } else {
                firstCall = apiServiceAcc.deleteInterest((int)element.getId());
            }

            firstCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        }
    }
}