package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRegistrationData;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity2 extends AppCompatActivity {

    EditText etDigit1, etDigit2, etDigit3, etDigit4;
    private Button buttonSendCode;
    private Button finishRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserRegistrationData userData = (UserRegistrationData) getIntent().getSerializableExtra("user_registration_data");

        buttonSendCode = findViewById(R.id.buttonSendCode);
        startResendTimer();

        buttonSendCode.setOnClickListener(v -> {
            Toast.makeText(RegistrationActivity2.this, "Код отправлен заново", Toast.LENGTH_SHORT).show();
            startResendTimer();
        });

        findViewById(R.id.buttonClose2).setOnClickListener(v -> {
            finish();
        });

        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        setupOtpInputs();

        etDigit1.addTextChangedListener(inputWatcher);
        etDigit2.addTextChangedListener(inputWatcher);
        etDigit3.addTextChangedListener(inputWatcher);
        etDigit4.addTextChangedListener(inputWatcher);

        finishRegistration = findViewById(R.id.buttonCompleteReg);
        finishRegistration.setEnabled(false);
        finishRegistration.setOnClickListener(v -> {
            testCode(getCode(), userData.getEmail());
        });
    }

    private String getCode() {
        String code = "";
        code += etDigit1.getText();
        code += etDigit2.getText();
        code += etDigit3.getText();
        code += etDigit4.getText();
        return code;
    }

    private void testCode(String code, String email) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);
        Call<ResponseDTO> call = apiService.verifyRegister(code, email);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Toast.makeText(RegistrationActivity2.this, "Код подтвержден", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity2.this, InterestSelectionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegistrationActivity2.this, "Неправильный код: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(RegistrationActivity2.this, "Ошибка сети COD: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        etDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit3.requestFocus();
                } else if (s.length() == 0) {
                    etDigit1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit4.requestFocus();
                } else if (s.length() == 0) {
                    etDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    etDigit3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void startResendTimer() {
        buttonSendCode.setEnabled(false);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSendCode.setText("Отправить повторно через " + millisUntilFinished / 1000 + " сек.");
            }

            public void onFinish() {
                buttonSendCode.setText("Отправить повторно");
                buttonSendCode.setEnabled(true);
            }
        }.start();
    }

    private final TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAllDigitsEntered();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void checkAllDigitsEntered() {
        String digit1 = etDigit1.getText().toString().trim();
        String digit2 = etDigit2.getText().toString().trim();
        String digit3 = etDigit3.getText().toString().trim();
        String digit4 = etDigit4.getText().toString().trim();

        finishRegistration.setEnabled(!digit1.isEmpty() && !digit2.isEmpty() && !digit3.isEmpty() && !digit4.isEmpty());
    }
}