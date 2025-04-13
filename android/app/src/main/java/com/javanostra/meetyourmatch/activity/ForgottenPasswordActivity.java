package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgottenPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotten_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(view -> finish());

        inputEmail = findViewById(R.id.inputEmail);
        inputEmail.addTextChangedListener(textWatcher);

        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setEnabled(false);

        buttonContinue.setOnClickListener(view -> {
            sendResetPasswordCode(inputEmail.getText().toString());
        });
    }


    private void sendResetPasswordCode(String email) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        Call<ResponseDTO> call = apiService.sendResetPasswordCode(email);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgottenPasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgottenPasswordActivity.this, ResetCodeActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgottenPasswordActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(ForgottenPasswordActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._'+-]+@[A-Za-z0-9.-]+\\.(com|ru|org|net|edu|gov|mil|biz|info|io|pro|me|tv|us|uk|de|jp|fr|au)$";
        return email.matches(emailPattern);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (isValidEmail(inputEmail.getText().toString())) {
                buttonContinue.setEnabled(true);
            } else {
                buttonContinue.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}