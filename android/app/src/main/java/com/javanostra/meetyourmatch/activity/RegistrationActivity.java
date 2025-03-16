package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.CityApiService;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.entity.City;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRegistrationData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteCity;
    private EditText inputUsername, inputEmail, inputPassword, inputPasswordConfirm;
    private Button buttonContinue;
    private CheckBox termsCheckBox;

    private int cityID;

    private List<City> cityList = new ArrayList<>();
    private List<String> cityNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUsername = findViewById(R.id.inputUserNameReg);
        inputEmail = findViewById(R.id.inputEmailReg);
        inputPassword = findViewById(R.id.inputPasswordReg);
        inputPasswordConfirm = findViewById(R.id.inputPasswordRegApprove);
        termsCheckBox = findViewById(R.id.checkBoxIL);
        buttonContinue = findViewById(R.id.buttonContinue);

        autoCompleteCity = findViewById(R.id.inputCityAutoComplete);

        loadCities();

        autoCompleteCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    List<String> filteredList = new ArrayList<>();
                    for (String city : cityNamesList) {
                        if (city.contains(s.toString())) {
                            filteredList.add(city);
                        }
                        if (filteredList.size() >= 5) {
                            break;
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrationActivity.this,
                            R.layout.item_dropdown, R.id.dropdownItem, filteredList);
                    autoCompleteCity.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        TextView termsTextView = findViewById(R.id.textView3);


        SpannableString spannableString = new SpannableString(getResources().getString(R.string.policy));

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/terms"));
                startActivity(browserIntent);
            }
        };

        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy"));
                startActivity(browserIntent);
            }
        };

        spannableString.setSpan(termsClickableSpan, 24, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyClickableSpan, 48, 69, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsTextView.setText(spannableString);
        termsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        termsTextView.setHighlightColor(ContextCompat.getColor(this, android.R.color.transparent));

        ImageButton buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(view -> finish());

        buttonContinue.setEnabled(false);

        inputUsername.addTextChangedListener(textWatcher);
        inputEmail.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
        inputPasswordConfirm.addTextChangedListener(textWatcher);
        autoCompleteCity.addTextChangedListener(textWatcher);
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkFields());

        buttonContinue.setOnClickListener(view -> {
            UserRegistrationData userData = new UserRegistrationData(
                    inputUsername.getText().toString(),
                    inputEmail.getText().toString(),
                    inputPassword.getText().toString(),
                    cityID
            );

            performRegister(userData);
        });
    }

    private void performRegister(UserRegistrationData userData) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        Call<ResponseDTO> call = apiService.register(userData.getUsername(), userData.getPassword(), userData.getEmail());

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Пользователь зарегистрирован", Toast.LENGTH_SHORT).show();
                    performLogin(userData);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка регистрации: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети REG: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogin(UserRegistrationData userData) {
        LoginApiService apiService = RetrofitClient.getRetrofit(this).create(LoginApiService.class);

        Call<ResponseDTO> call = apiService.login(userData.getUsername(), userData.getPassword());
        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Intent intent = new Intent(RegistrationActivity.this, RegistrationActivity2.class);
                    intent.putExtra("user_registration_data", userData);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка входа: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети LOG: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performCityUpdate(UserRegistrationData userData) {
        AccountApiService userApiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        Call<String> call = userApiService.setCity((long) userData.getCityId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "response.body()", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка обновления: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети CIT: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadCities() {
        CityApiService apiService = RetrofitClient.getRetrofit(this).create(CityApiService.class);

        apiService.getCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(@NonNull Call<List<City>> call, @NonNull Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList = response.body();
                    for (City city : cityList) {
                        cityNamesList.add(city.getName());
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Невозможно загрузить список городов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<City>> call, @NonNull Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFields() {
        boolean isFormValid = !inputUsername.getText().toString().isEmpty()
                && !inputEmail.getText().toString().isEmpty()
                && !inputPassword.getText().toString().isEmpty()
                && !inputPasswordConfirm.getText().toString().isEmpty()
                && !autoCompleteCity.getText().toString().isEmpty()
                && termsCheckBox.isChecked()
                && (inputPasswordConfirm.getText().toString().equals(inputPassword.getText().toString()))
                && cityCheck(autoCompleteCity.getText().toString());

        buttonContinue.setEnabled(isFormValid);
    }

    private boolean cityCheck(String passedCity) {
        for (int i = 0; i < cityNamesList.size(); i++) {
            if (cityNamesList.get(i).equals(passedCity)) {
                cityID = i;
                return true;
            }
        }
        return false;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            checkFields();
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}