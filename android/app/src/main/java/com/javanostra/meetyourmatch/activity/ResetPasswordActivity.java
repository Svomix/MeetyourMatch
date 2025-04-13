package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
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
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputPassword, inputPasswordConfirm;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String email = getIntent().getExtras().get("email").toString();

        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirm = findViewById(R.id.inputPasswordApprove);
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setEnabled(false);

        inputPassword.addTextChangedListener(textWatcher);
        inputPasswordConfirm.addTextChangedListener(textWatcher);


        buttonContinue.setOnClickListener(view -> {
                updatePassword(inputPassword.getText().toString(), email);
        });
    }

    private void updatePassword(String password, String email) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);
        Call<ResponseDTO> call = apiService.updatePassword(password, email);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            buttonContinue.setEnabled(!inputPassword.getText().toString().isEmpty()
                    && !inputPasswordConfirm.getText().toString().isEmpty()
                    && inputPasswordConfirm.getText().toString().equals(inputPassword.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}