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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.api_service.RegistrationApiService;
import com.javanostra.meetyourmatch.persistance.entity.UserRegistrationData;

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

        findViewById(R.id.buttonCompleteReg).setOnClickListener(v -> {
            performRegister(userData);
            performLogin(userData.getUsername(), userData.getPassword());
            performCityUpdate(userData.getCityId());

            //Intent intent = new Intent(RegistrationActivity2.this, InterestSelectionActivity.class);
            //startActivity(intent);
        });

        findViewById(R.id.buttonClose2).setOnClickListener(v -> {
            finish();
        });

        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        finishRegistration = findViewById(R.id.buttonCompleteReg);
        setupOtpInputs();

        etDigit1.addTextChangedListener(inputWatcher);
        etDigit2.addTextChangedListener(inputWatcher);
        etDigit3.addTextChangedListener(inputWatcher);
        etDigit4.addTextChangedListener(inputWatcher);
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
            finishRegistration.setEnabled(true);
        } else {
            finishRegistration.setEnabled(false);
        }
    }

    private void performRegister(UserRegistrationData userData) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        Call<String> call = apiService.register(userData.getUsername(), userData.getPassword(), userData.getEmail());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity2.this, "Пользователь зарегистрирован", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity2.this, "Ошибка регистрации: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegistrationActivity2.this, "Ошибка сети REG: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogin(String username, String password) {
        LoginApiService apiService = RetrofitClient.getRetrofit(this).create(LoginApiService.class);

        Call<ResponseBody> call = apiService.login(username, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                } else {
                    Toast.makeText(RegistrationActivity2.this, "Ошибка входа: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistrationActivity2.this, "Ошибка сети LOG: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performCityUpdate(int cityID) {
        AccountApiService userApiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        Call<String> call = userApiService.setCity((long) cityID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity2.this, "response.body()", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity2.this, "Ошибка обновления: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegistrationActivity2.this, "Ошибка сети CIT: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}