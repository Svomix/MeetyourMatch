package com.javanostra.meetyourmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteCity;
    private List<String> cityList;

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

        CheckBox termsCheckBox = findViewById(R.id.checkBoxIL);
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
        buttonClose.setOnClickListener(view -> {
            finish();
        });

        Button buttonContinue = findViewById(R.id.buttonContinue);
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonContinue.setEnabled(isChecked);
        });

        buttonContinue.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationActivity.this, RegistrationActivity2.class);
            startActivity(intent);
        });

        autoCompleteCity = findViewById(R.id.inputCityAutoComplete);
        cityList = Arrays.asList("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
                "Казань", "Нижний Новгород", "Челябинск", "Самара",
                "Омск", "Ростов-на-Дону", "Уфа", "Красноярск",
                "Пермь", "Воронеж", "Волгоград");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_dropdown,
                R.id.dropdownItem,
                cityList);
        autoCompleteCity.setAdapter(adapter);
    }
}