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
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.CityApiService;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.entity.City;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRegistrationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteCity;
    private EditText inputUsername, inputEmail, inputPassword, inputPasswordConfirm;
    private Button buttonContinue;
    private CheckBox termsCheckBox;

    private int cityID;

    private List<City> cityList;
    private List<String> cityNamesList;

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
                        if (city.toLowerCase().contains(s.toString().toLowerCase())) {
                            filteredList.add(city);
                        }
                        if (filteredList.size() >= 2) {
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

        String text = "Я прочитал и согласен с Условиями пользования и Политикой приватности";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/terms"));
                startActivity(browserIntent);
            }
        };

        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
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

            checkUserExists(userData, () -> {
                Intent intent = new Intent(RegistrationActivity.this, RegistrationActivity2.class);
                intent.putExtra("user_registration_data", userData);
                startActivity(intent);
            });
        });
    }

    private void checkUserExists(UserRegistrationData userData, Runnable onComplete) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        apiService.checkUserExists(userData.getUsername(), userData.getEmail()).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    int statusCode = response.body().getCode();

                    if (statusCode == HttpsURLConnection.HTTP_ACCEPTED) {
                        sendUpdateCodeRequest(userData.getEmail(), onComplete);
                    } else if (statusCode == HttpsURLConnection.HTTP_CONFLICT) {
                        Toast.makeText(RegistrationActivity.this, "Пользователь уже подтвержден", Toast.LENGTH_SHORT).show();
                    } else if (statusCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                        performRegister(userData, onComplete);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Неизвестный ответ от сервера", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Ошибка проверки пользователя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performRegister(UserRegistrationData userData, Runnable onComplete) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        apiService.register(userData.getUsername(), userData.getPassword(), userData.getEmail()).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("Registration", "Successful: " + response.body().getMessage());
                    onComplete.run();
                } else {
                    Log.d("Registration", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.d("Registration", "Network error: " + t.getMessage());
            }
        });
    }

    private void sendUpdateCodeRequest(String email, Runnable onComplete) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        apiService.updateVerificationCode(email).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateCode", "Successful: " + response.body().getMessage());
                    onComplete.run();
                } else {
                    Log.d("UpdateCode", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.d("UpdateCode", "Network error: " + t.getMessage());
            }
        });
    }

    private void loadCities() {
        CityApiService apiService = RetrofitClient.getRetrofit(this).create(CityApiService.class);

        apiService.getCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList = response.body();
                    cityNamesList = new ArrayList<>();
                    for (City city : cityList) {
                        cityNamesList.add(city.getName());
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Невозможно загрузить список городов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
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
                && cityCheck(autoCompleteCity.getText().toString())
                && isValidEmail(inputEmail.getText().toString());

        buttonContinue.setEnabled(isFormValid);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._'+-]+@[A-Za-z0-9.-]+\\.(com|ru|org|net|edu|gov|mil|biz|info|io|pro|me|tv|us|uk|de|jp|fr|au)$";
        return email.matches(emailPattern);
    }

    private boolean cityCheck(String passedCity) {
        if (passedCity.isEmpty()) return false;
        for (int i = 0; i < cityNamesList.size(); i++) {
            if (cityNamesList.get(i).equalsIgnoreCase(passedCity)) {
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