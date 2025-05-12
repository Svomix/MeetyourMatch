package com.javanostra.meetyourmatch.activity;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.kotlin.VKAuth;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.vk.id.onetap.xml.OneTap;

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

        VKAuth vkAuth = VKAuth.Companion.getInstance();
        vkAuth.vkInit(this);
        vkAuth.vkAuth(this, findViewById(R.id.buttonLoginVK));

        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Main", "Request permission");
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 101);
        }


        inputUserName = findViewById(R.id.inputUserName);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.buttonLogin);
        Button forgotPasswordButton = findViewById(R.id.buttonForgotPass);
        Button registerButton = findViewById(R.id.buttonToRegistration);

        loginButton.setEnabled(false);

        inputUserName.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);

        forgotPasswordButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgottenPasswordActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String username = inputUserName.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            performLogin(username, password);
        });

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.putExtra("auth", "commonAuth");
            startActivity(intent);
        });
    }

    private void validateInputFields() {
        String username = inputUserName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());
    }

    public void vkAuth(String username, String email, String accessToken) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        Call<Boolean> call = apiService.checkUserIfExistsByEmail(email);

        call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                        if (response.isSuccessful()) {

                            if (Boolean.TRUE.equals(response.body())) {
                                performVkLogin(accessToken, email);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                                intent.putExtra("auth", "vkAuth");
                                intent.putExtra("email", email);
                                intent.putExtra("name", username);
                                intent.putExtra("token", accessToken);
                                startActivity(intent);
                            }


                        } else {

                            Toast.makeText(LoginActivity.this, R.string.invalidLogin, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {

                        Toast.makeText(LoginActivity.this, R.string.connectionError + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void performVkLogin(String token, String email) {
        LoginApiService apiService = RetrofitClient.getRetrofit(this).create(LoginApiService.class);

        Call<ResponseDTO> call = apiService.vkLogin(token, email);

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