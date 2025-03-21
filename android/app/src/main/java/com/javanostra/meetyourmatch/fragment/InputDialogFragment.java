package com.javanostra.meetyourmatch.fragment;

import static java.lang.Thread.sleep;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.AccountActivity;
import com.javanostra.meetyourmatch.activity.LoginActivity;
import com.javanostra.meetyourmatch.activity.MainScreenActivity;
import com.javanostra.meetyourmatch.activity.RegistrationActivity;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.CityApiService;
import com.javanostra.meetyourmatch.persistance.api_service.LoginApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.City;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.User;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputDialogFragment extends DialogFragment {

    private AutoCompleteTextView editText;
    private Button confirmButton, cancelButton;

    private int cityID;
    private List<City> cityList;
    private List<String> cityNamesList;

    private UserProfileDTO userTransfer;
    private String input;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00ffffff));

        View view = inflater.inflate(R.layout.dialog_input, container, false);
        editText = view.findViewById(R.id.editText);
        confirmButton = view.findViewById(R.id.confirmButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        confirmButton.setEnabled(false);

        if ("emailShow".equals(getTag())) {
            editText.setHint("Введите новую почту");
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    confirmButton.setEnabled(isValidEmail(s.toString().trim()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else if ("cityShow".equals(getTag())) {
            editText.setHint("Введите новый город");
            loadCities();

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        List<String> filteredList = new ArrayList<>();
                        for (String city : cityNamesList) {
                            if (city.toLowerCase().contains(s.toString().toLowerCase())) {
                                filteredList.add(city);
                            }
                            if (filteredList.size() >= 2) {
                                break;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                R.layout.item_dropdown, R.id.dropdownItem, filteredList);
                        editText.setAdapter(adapter);
                    }

                    confirmButton.setEnabled(isValidCity(editText.getText().toString()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            editText.setHint("Введите новое имя пользователя");
            confirmButton.setEnabled(true);
        }

        confirmButton.setOnClickListener(v -> {
            try {
                input = editText.getText().toString().trim();
                if (!input.isEmpty()) {
                    if ("emailShow".equals(getTag())) {
                        performEmailUpdate(input);
                    } else if ("cityShow".equals(getTag())) {
                        performCityUpdate(cityID);
                    } else if ("usernameShow".equals(getTag())) {
                        performUsernameUpdate(input);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        setCancelable(true);
        return view;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._'+-]+@[A-Za-z0-9.-]+\\.(com|ru|org|net|edu|gov|mil|biz|info|io|pro|me|tv|us|uk|de|jp|fr|au)$";
        return email.matches(emailPattern);
    }

    private void loadCities() {
        CityApiService apiService = RetrofitClient.getRetrofit(requireContext()).create(CityApiService.class);

        apiService.getCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList = response.body();
                    cityNamesList = new ArrayList<>();
                    for (City city : cityList) {
                        cityNamesList.add(city.getName());
                    }
                } else {
                    //Toast.makeText(RegistrationActivity.this, "Невозможно загрузить список городов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                //Toast.makeText(RegistrationActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidCity(String passedCity) {
        if (passedCity.isEmpty()) return false;
        for (int i = 0; i < cityNamesList.size(); i++) {
            if (cityNamesList.get(i).equalsIgnoreCase(passedCity)) {
                cityID = i;
                return true;
            }
        }
        return false;
    }

    private void performCityUpdate(int cityID) throws InterruptedException {
        AccountApiService userApiService = RetrofitClient.getRetrofit(requireContext()).create(AccountApiService.class);

        userApiService.setCity((long) cityID).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    ((AccountActivity) requireActivity()).updateTextView(getTag(), input);
                    Log.d("CityUpdate", "Successful: " + response.body().getMessage());
                    Toast.makeText(requireActivity().getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("CityUpdate", "Error: " + response.errorBody() + " " + response.code());
                    Toast.makeText(requireActivity().getApplicationContext(), "Такого города не существует!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.e("CityUpdate", "Network error: " + t.getMessage());
            }
        });

        sleep(300);
        dismiss();
    }

    private void performEmailUpdate(String email) throws InterruptedException {
        AccountApiService userApiService = RetrofitClient.getRetrofit(requireContext()).create(AccountApiService.class);

        userApiService.setEmail(email).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    ((AccountActivity) requireActivity()).updateTextView(getTag(), input);
                    Log.d("EmailUpdate", "Successful: " + response.body().getMessage());
                    Toast.makeText(requireActivity().getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("EmailUpdate", "Error: " + response.errorBody() + " " + response.code());
                    Toast.makeText(requireActivity().getApplicationContext(), "Почта " + email + " уже занята!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.e("EmailUpdate", "Network error: " + t.getMessage());
            }
        });

        sleep(300);
        dismiss();
    }

    private void performUsernameUpdate(String username) throws InterruptedException {
        AccountApiService userApiService = RetrofitClient.getRetrofit(requireContext()).create(AccountApiService.class);

        userApiService.setUsername(username).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    ((AccountActivity) requireActivity()).updateTextView(getTag(), input);
                    Log.d("UsernameUpdate", "Successful: " + response.body().getMessage());
                    Toast.makeText(requireActivity().getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("UsernameUpdate", "Error: " + response.errorBody() + " " + response.code());
                    Toast.makeText(requireActivity().getApplicationContext(), "Имя " + username + " уже занято!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.e("UsernameUpdate", "Network error: " + t.getMessage());
            }
        });

        sleep(300);
        dismiss();
    }
}