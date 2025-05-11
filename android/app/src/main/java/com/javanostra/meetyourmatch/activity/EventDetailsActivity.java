package com.javanostra.meetyourmatch.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.fragment.CommentsBottomSheetFragment;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.FullEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.User;
import com.javanostra.meetyourmatch.persistance.entity.UserActionDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserEvent;
import com.javanostra.meetyourmatch.persistance.entity.UserEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    UserProfileDTO currentUser;
    Event currentEvent;
    UserEvent currentUserEvent;
    TextView eventTitle, eventDescription, eventDate, eventPlace, eventTags, likeCount;
    ImageView coverImage;
    ImageButton likeButton, calendarButton, commentsButton;
    FrameLayout shareButton;

    private boolean isLiked;
    private boolean isAddedToCalendar;
    private boolean calendarStatusChanged = false;

    private List<Tag> eventTagsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("event")) {
            currentEvent = intent.getParcelableExtra("event");
        } else {
            Log.e("EventDetailsActivity", "No 'event' extra found in Intent!");
            Toast.makeText(this, "Ошибка: Не удалось загрузить данные события.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (currentEvent == null) {
            Log.e("EventDetailsActivity", "Ошибка: Объект Event получен как null после getParcelableExtra.");
            Toast.makeText(this, "Ошибка: Не удалось получить данные события.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.d("EventDetailsActivity", "Successfully loaded event: " + currentEvent.getTitle());

        likeButton = findViewById(R.id.likeButton);
        likeButton.setColorFilter(Color.parseColor("#FCAD2F"));
        calendarButton = findViewById(R.id.calendarButton);
        commentsButton = findViewById(R.id.commentsButton);
        shareButton = findViewById(R.id.shareButtonContainer);

        fetchAccountInfo();

        eventTags = findViewById(R.id.event_tags);
        fetchEventTags(currentEvent.getId());

        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_description);
        eventDate = findViewById(R.id.event_date);
        eventPlace = findViewById(R.id.event_place);

        likeCount = findViewById(R.id.likeCount);
        fetchEventLikes(currentEvent.getId());

        coverImage = findViewById(R.id.eventImage);
        Glide.with(this)
                .load(currentEvent.getCoverImgUrl())
                .placeholder(R.drawable.ic_mym_128)
                .error(R.drawable.ic_mym_128)
                .into(coverImage);

        eventTitle.setText(currentEvent.getTitle());
        eventDescription.setText(currentEvent.getDescription());
        if (currentEvent.getDate() != null) eventDate.setText(dateCorrectImplementation(currentEvent));
        //eventPlace.setText(currentEvent.getLocation().getAddress() + ", " +
        //        currentEvent.getLocation().getTitle());
        if (currentEvent.getLocation() != null) eventPlace.setText(currentEvent.getLocation().getAddress());

        likeButton.setOnClickListener(v -> {
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                isLiked = false;
            } else {
                likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                isLiked = true;

                v.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .start();
                        }).start();
            }
            fetchSwitchLike(currentEvent.getId(), isLiked);
        });

        calendarButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            if (isAddedToCalendar) {
                calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_add_24);
                isAddedToCalendar = false;
            } else {
                calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                isAddedToCalendar = true;

                v.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .start();
                        }).start();
            }
            calendarStatusChanged = true;
            fetchSwitchCalendar(currentEvent.getId(), isAddedToCalendar);
        });

        commentsButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            showCommentsBottomSheet();
        });

        shareButton.setOnClickListener(v -> {
            String url = currentEvent.getSourceUrl();
            openWebPage(url);
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        super.onBackPressed();
    }

    private String dateCorrectImplementation(Event event) {
        StringBuilder builder = new StringBuilder();

        int buffer = event.getDate().getDate();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        buffer = event.getDate().getMonth();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        builder.append(event.getDate().getYear()+1900);
        builder.append(" - ");

        buffer = event.getDate().getHours();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append(':');

        buffer = event.getDate().getMinutes();
        if (buffer < 10) builder.append("0");
        builder.append(buffer);

        return builder.toString();
    }

    private void showCommentsBottomSheet() {
        if (currentEvent.getId() != null && currentEvent.getId() != -1) {
            CommentsBottomSheetFragment commentsSheet = CommentsBottomSheetFragment.newInstance(currentEvent.getId());
            commentsSheet.show(getSupportFragmentManager(), commentsSheet.getTag());
        } else {
            Toast.makeText(this, "Cannot load comments: Event ID missing.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAccountInfo() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.message());
                    currentUser = response.body();

                    fetchEventAction(currentEvent.getId());
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

    public void fetchEventAction(Long eventId) {
        AccountApiService apiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiService.getEventAction(eventId).enqueue(new retrofit2.Callback<UserActionDTO>() {
            @Override
            public void onResponse(Call<UserActionDTO> call, retrofit2.Response<UserActionDTO> response) {
                if (response.isSuccessful()) {
                    UserActionDTO dto = response.body();

                    assert dto != null;
                    isLiked = dto.getLiked();
                    isAddedToCalendar = dto.getInCalendar();

                    if (isLiked) likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    else likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);

                    if (isAddedToCalendar) calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                    else calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_add_24);
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserActionDTO> call, Throwable t) {
            }
        });

    }

    public void fetchEventLikes(Long eventId) {
        EventApiService apiService = RetrofitClient.getRetrofit(this).create(EventApiService.class);

        apiService.findEventById(eventId).enqueue(new retrofit2.Callback<FullEventDTO>() {
            @Override
            public void onResponse(Call<FullEventDTO> call, Response<FullEventDTO> response) {
                if (response.isSuccessful()) {
                    likeCount.setText(response.body().getUserActionCounters().getLikedCounter().toString());
                } else {
                }
            }

            @Override
            public void onFailure(Call<FullEventDTO> call, Throwable t) {
            }
        });

    }

    public void fetchEventTags(Long eventId) {
        EventApiService apiService = RetrofitClient.getRetrofit(this).create(EventApiService.class);

        apiService.findEventById(eventId).enqueue(new Callback<FullEventDTO>() {
            @Override
            public void onResponse(Call<FullEventDTO> call, Response<FullEventDTO> response) {
                if (response.isSuccessful()) {
                    eventTagsList = response.body().getTags();

                    StringBuilder builder = new StringBuilder();
                    for (Tag tag : eventTagsList) {
                        builder.append("#").append(tag.getName()).append(" ");
                    }
                    eventTags.setText(builder.toString());
                } else {
                }
            }

            @Override
            public void onFailure(Call<FullEventDTO> call, Throwable t) {
            }
        });
    }

    public void fetchSwitchLike(Long eventId, Boolean isLiked) {
        AccountApiService apiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiService.setLiked(eventId, isLiked).enqueue(new retrofit2.Callback<UserActionDTO>() {
            @Override
            public void onResponse(Call<UserActionDTO> call, retrofit2.Response<UserActionDTO> response) {
                if (response.isSuccessful()) {
                    fetchEventLikes(eventId);
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserActionDTO> call, Throwable t) {
            }
        });
    }

    public void fetchSwitchCalendar(Long eventId, Boolean isAdded) {
        AccountApiService apiService = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiService.setCalendar(eventId, isAdded).enqueue(new retrofit2.Callback<UserActionDTO>() {
            @Override
            public void onResponse(Call<UserActionDTO> call, retrofit2.Response<UserActionDTO> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserActionDTO> call, Throwable t) {
            }
        });
    }

    private void openWebPage(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        try {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Не найдено приложение для открытия ссылки", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        if (calendarStatusChanged) {
            Log.d("EventDetails", "Finishing with RESULT_OK because status changed.");
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            Log.d("EventDetails", "Finishing with RESULT_CANCELED.");
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        super.finish();
    }

    private void finishWithError(String message) {
        Log.e("EventDetails", "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}