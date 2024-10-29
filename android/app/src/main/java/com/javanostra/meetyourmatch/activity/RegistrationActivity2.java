package com.javanostra.meetyourmatch;

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

        buttonSendCode = findViewById(R.id.buttonSendCode);
        startResendTimer();

        buttonSendCode.setOnClickListener(v -> {
            Toast.makeText(RegistrationActivity2.this, "Код отправлен заново", Toast.LENGTH_SHORT).show();
            startResendTimer();
        });

        findViewById(R.id.buttonCompleteReg).setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity2.this, InterestSelectionActivity.class);
            startActivity(intent);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit3.requestFocus();
                } else if (s.length() == 0) {
                    etDigit1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    etDigit4.requestFocus();
                } else if (s.length() == 0) {
                    etDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    etDigit3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAllDigitsEntered();
        }

        @Override
        public void afterTextChanged(Editable s) {}
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
}