package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRegistrationData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetCodeActivity extends AppCompatActivity {

    EditText etDigit1, etDigit2, etDigit3, etDigit4;
    private Button buttonSendCode;
    private Button confirmCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_code);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String email = getIntent().getExtras().get("email").toString();
        String newPassword = getIntent().getExtras().get("newPassword").toString();

        TextView textMail = findViewById(R.id.textMail);
        textMail.setText(email);

        buttonSendCode = findViewById(R.id.buttonSendCode);
        startResendTimer();

        buttonSendCode.setOnClickListener(v -> {
            Toast.makeText(ResetCodeActivity.this, "Код отправлен заново", Toast.LENGTH_SHORT).show();
            updateResetCode(email);
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

        confirmCodeButton = findViewById(R.id.buttonComplete);
        confirmCodeButton.setEnabled(false);
        confirmCodeButton.setOnClickListener(v -> {
            testConfirmCode(getCode(), email, newPassword);
        });
    }

    private void testConfirmCode(String code, String email, String newPassword) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);
        Call<ResponseDTO> call = apiService.checkResetPasswordCode(newPassword, code, email);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetCodeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetCodeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetCodeActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(ResetCodeActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateResetCode(String email) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);
        Call<ResponseDTO> call = apiService.updateResetCode(email);

        call.enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDTO> call, @NonNull Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetCodeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetCodeActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(ResetCodeActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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

        if (!digit1.isEmpty() && !digit2.isEmpty() && !digit3.isEmpty() && !digit4.isEmpty()) {
            confirmCodeButton.setEnabled(true);
        } else {
            confirmCodeButton.setEnabled(false);
        }
    }
}