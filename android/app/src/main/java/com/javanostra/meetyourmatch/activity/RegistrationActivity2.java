package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.entity.NewUserDTO;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
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
        NewUserDTO truncatedUserData = new NewUserDTO(userData.getUsername(), userData.getEmail(), userData.getPassword());

        TextView textMail = findViewById(R.id.textMail);
        if (userData.getEmail() != null && !userData.getEmail().isEmpty()) {
            textMail.setText(userData.getEmail());
        }

        buttonSendCode = findViewById(R.id.buttonSendCode);
        startResendTimer();

        buttonSendCode.setOnClickListener(v -> {
            Toast.makeText(RegistrationActivity2.this, "Код отправлен заново", Toast.LENGTH_SHORT).show();
            sendUpdateCodeRequest(userData.getEmail());
            startResendTimer();
        });

        findViewById(R.id.buttonCompleteReg).setOnClickListener(v -> {
            String verificationCode = etDigit1.getText().toString() + etDigit2.getText().toString() +
                    etDigit3.getText().toString() + etDigit4.getText().toString();

            performVerify(userData.getEmail(), verificationCode, () -> {
                performLogin(userData.getUsername(), userData.getPassword(), () -> {
                    performCityUpdate(userData.getCityId());
                    Intent intent = new Intent(RegistrationActivity2.this, InterestSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            });
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

    private void performVerify(String email, String code, Runnable onComplete) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        apiService.verifyRegister(email, code).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("Verify", "Successful: " + response.body().getMessage());
                    onComplete.run();
                } else {
                    Log.d("Verify", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.d("Verify", "Network error: " + t.getMessage());
            }
        });
    }

    private void sendUpdateCodeRequest(String email) {
        RegistrationApiService apiService = RetrofitClient.getRetrofit(this).create(RegistrationApiService.class);

        apiService.updateVerificationCode(email).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateCode", "Successful: " + response.body().getMessage());
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

    private void performLogin(String username, String password, Runnable onComplete) {
        LoginApiService apiService = RetrofitClient.getRetrofit(this).create(LoginApiService.class);

        apiService.login(username, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Log.d("Login", "Successful: " + username + " " + password);
                    onComplete.run();
                } else {
                    Log.d("Login", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Login", "Network error: " + t.getMessage());
            }
        });
    }

    private void performCityUpdate(int cityID) {
        AccountApiService userApiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        userApiService.setCity((long) cityID).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("SetCity", "Successful: " + response.body().getMessage());
                } else {
                    Log.d("SetCity", "Error: " + cityID + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.d("SetCity", "Network error: " + t.getMessage());
            }
        });
    }
}