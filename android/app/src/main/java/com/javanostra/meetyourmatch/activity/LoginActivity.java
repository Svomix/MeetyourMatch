package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUserName, inputPassword;
    private Button loginButton;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInputFields();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUserName = findViewById(R.id.inputUserName);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonToRegistration);
        Button loginButtonVK = findViewById(R.id.buttonLoginVK);

        loginButton.setEnabled(false);

        inputUserName.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(view -> {
            String username = inputUserName.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            performLogin(username, password);
        });

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        loginButtonVK.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateInputFields() {
        String username = inputUserName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());
    }

    private void performLogin(String username, String password) {
        LoginApiService apiService = RetrofitClient.getRetrofit(this).create(LoginApiService.class);

        Call<ResponseDTO> call = apiService.login(username, password);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    Toast.makeText(LoginActivity.this, R.string.successfulLogin, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(LoginActivity.this, R.string.invalidLogin, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {

                Toast.makeText(LoginActivity.this, R.string.connectionError + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}