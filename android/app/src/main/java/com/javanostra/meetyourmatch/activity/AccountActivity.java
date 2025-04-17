package com.javanostra.meetyourmatch.activity;

import static androidx.fragment.app.DialogFragment.STYLE_NO_FRAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.fragment.InputDialogFragment;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.ImageApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.entity.ResponseDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.yalantis.ucrop.UCrop;

public class  AccountActivity extends AppCompatActivity {

    private UserProfileDTO currentUser;
    private List<Tag> currentUserTags;

    ImageButton buttonClose;
    TextView usernameShow, cityShow, emailShow, tagsShow, genderShow, ageShow;
    ShapeableImageView avatar;
    LinearLayout city, email, tag;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int UCROP_REQUEST_CODE = 2;

    private ActivityResultLauncher<Intent> tagSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.bars));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameShow = findViewById(R.id.usernameShow);
        cityShow = findViewById(R.id.cityShow);
        emailShow = findViewById(R.id.emailShow);
        tagsShow = findViewById(R.id.tagsShow);
        genderShow = findViewById(R.id.genderShow);
        ageShow = findViewById(R.id.ageShow);

        performGetAccountInfo(() ->
                performGetUserTags(currentUser.getId())
        );

        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> openGallery());

        city = findViewById(R.id.City);
        email = findViewById(R.id.Email);
        tag = findViewById(R.id.Tags);
        tagSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        performGetUserTags(currentUser.getId());
                    }
                }
        );

        city.setOnClickListener(v -> showInputDialog("cityShow"));
        email.setOnClickListener(v -> showInputDialog("emailShow"));
        //usernameShow.setOnClickListener(v -> showInputDialog("usernameShow"));
        tag.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, InterestSelectionActivity.class);
            intent.putExtra("previousActivity", "Account");
            tagSelectionLauncher.launch(intent);
        });

        buttonClose = findViewById(R.id.buttonClose4);
        buttonClose.setOnClickListener(v -> {
            finish();
        });
    }

    private void showInputDialog(String tag) {
        InputDialogFragment dialog = new InputDialogFragment();
        dialog.show(getSupportFragmentManager(), tag);
    }

    public void updateTextView(String tag, String newValue) {
        switch (tag) {
            case "cityShow":
                cityShow.setText(newValue);
                break;
            case "emailShow":
                emailShow.setText(newValue);
                break;
            case "usernameShow":
                usernameShow.setText(newValue);
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri sourceUri = data.getData();
                if (sourceUri != null) {
                    String fileName = "cropped_avatar_" + System.currentTimeMillis() + ".jpg";
                    File file = new File(getCacheDir(), fileName);
                    Uri destinationUri = Uri.fromFile(file);
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(300, 300)
                            .start(this, UCROP_REQUEST_CODE);
                }
            } else if (requestCode == UCROP_REQUEST_CODE) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    avatar.setImageURI(null);
                    avatar.setImageURI(resultUri);
                    avatar.invalidate();
                    performUploadImage(resultUri);
                } else {
                    Log.e("UCrop", "Crop error:" + UCrop.getError(data));
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("UCrop", "Crop error: " + (cropError != null ? cropError.getMessage() : ""));
        }
    }

    public void exitAccount(View view) {
        CookieManager cookieManager = new CookieManager(this);
        cookieManager.saveCookie("");

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void performGetAccountInfo(Runnable onComplete) {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.message());
                    currentUser = response.body();

                    usernameShow.setText(currentUser.getUsername());
                    if (currentUser.getCity() != null) cityShow.setText(currentUser.getCity().getName());
                    emailShow.setText(currentUser.getEmail());
                    if (currentUser.getGender() != null) genderShow.setText(currentUser.getGender());
                    if (currentUser.getGender() != null) ageShow.setText(currentUser.getGender());

                    if (currentUser.getAvatarPath() != null) performDownloadImage(currentUser.getAvatarPath());

                    onComplete.run();
                } else {
                    Log.e("GetAccountInfo", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                Log.e("GetAccountInfo", "Network error: " + t.getMessage());
            }
        });
    }

    private void performGetUserTags(Long userId) {
        UserApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiServiceAcc.getUserTags(userId).enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    Log.d("GetUserTags", "Successful: " + response.message());
                    currentUserTags = response.body();

                    StringBuilder builder = new StringBuilder();
                    int size = Math.min(5, currentUserTags.size());
                    for (int i = 0; i < size; i++) {
                        builder.append("#").append(currentUserTags.get(i).getName());
                        if (i != size-1) builder.append(", ");
                    }
                    tagsShow.setText(builder.toString());
                } else {
                    Log.e("GetUserTags", "Error: " + response.message() + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Log.e("GetUserTags", "Network error: " + t.getMessage());
            }
        });
    }

    public void performUploadImage(Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        ImageApiService apiService = RetrofitClient.getRetrofit(this).create(ImageApiService.class);
        apiService.uploadImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        performSetImage(responseBody);
                        Log.d("ImageAPI","Upload success: " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ImageAPI","Upload error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ImageAPI", "Upload Network error: " + t.getMessage());
            }
        });
    }

    public void performDownloadImage(String filename) {
        ImageApiService apiService = RetrofitClient.getRetrofit(this).create(ImageApiService.class);
        apiService.downloadImage(currentUser.getAvatarPath()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("ImageAPI", "Download success: " + response.message());
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avatar.setImageBitmap(bitmap);
                            avatar.invalidate();
                        }
                    });
                } else {
                    Log.e("ImageAPI", "Download Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ImageAPI", "Download Error: " + t.getMessage());
            }
        });
    }

    private void performSetImage(String imageUrl) {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiServiceAcc.setImage(imageUrl).enqueue(new Callback<ResponseDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("SetAvatarImage", "Success: " + response.body().getCode());
                } else {
                    Log.e("SetAvatarImage", "Error: " + response.body().getCode() + " " + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseDTO> call, Throwable t) {
                Log.e("SetAvatarImage","Network error: " + t.getMessage());
            }
        });
    }
}