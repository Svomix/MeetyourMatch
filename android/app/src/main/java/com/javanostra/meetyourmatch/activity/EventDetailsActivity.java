package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.Tag;
import com.javanostra.meetyourmatch.persistance.entity.User;
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
    TextView eventTitle, eventDescription, eventDate, eventTags, likeCount;
    ImageView coverImage;
    ImageButton likeButton, calendarButton;

    private boolean isLiked;
    private boolean isAddedToCalendar;

    private List<Tag> eventTagsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        currentEvent = (Event) intent.getSerializableExtra("event");

        likeButton = findViewById(R.id.likeButton);
        likeButton.setColorFilter(Color.parseColor("#FCAD2F"));
        calendarButton = findViewById(R.id.calendarButton);

        fetchAccountInfo();

        eventTags = findViewById(R.id.event_tags);
        fetchEventTags(currentEvent.getId());

        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_description);
        eventDate = findViewById(R.id.event_date);

        likeCount = findViewById(R.id.likeCount);
        fetchEventLikes(currentEvent.getId());

        coverImage = findViewById(R.id.eventLogo);
        Glide.with(this)
                .load(currentEvent.getCoverImgUrl())
                .placeholder(R.drawable.ic_mym_128)
                .error(R.drawable.ic_mym_128)
                .into(coverImage);

        eventTitle.setText(currentEvent.getTitle());
        eventDescription.setText(currentEvent.getDescription());
        eventDate.setText(dateCorrectImplementation(currentEvent));

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
            fetchSwitchLike(currentUser.getId(), currentEvent.getId());
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
            fetchSwitchCalendar(currentUser.getId(), currentEvent.getId());
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        super.onBackPressed();
    }

    private String dateCorrectImplementation(Event event) {
        StringBuilder builder = new StringBuilder("Дата: ");

        int buffer = event.getDate().getDate();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        buffer = event.getDate().getMonth();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        builder.append(event.getDate().getYear()+1900);

        return builder.toString();
    }

    private void fetchAccountInfo() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(this).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.message());
                    currentUser = response.body();

                    fetchUserEvent(currentUser.getId(), currentEvent.getId());
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

    public void fetchUserEvent(Long userId, Long eventId) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiService.getUserEventById(userId, eventId).enqueue(new retrofit2.Callback<UserEventDTO>() {
            @Override
            public void onResponse(Call<UserEventDTO> call, retrofit2.Response<UserEventDTO> response) {
                if (response.isSuccessful()) {
                    UserEventDTO dto = response.body();
                    currentUserEvent = new UserEvent(dto.getUser(), dto.getEvent(), dto.getLiked(), dto.getDisliked(), dto.getInCalendar());

                    isLiked = currentUserEvent.getLiked();
                    isAddedToCalendar = currentUserEvent.getInCalendar();

                    if (isLiked) likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    else likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);

                    if (isAddedToCalendar) calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                    else calendarButton.setImageResource(R.drawable.ic_baseline_bookmark_add_24);
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserEventDTO> call, Throwable t) {
            }
        });

    }

    public void fetchEventLikes(Long eventId) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiService.getLikes(eventId).enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    likeCount.setText(response.body().toString());
                } else {
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
            }
        });

    }

    public void fetchEventTags(Long eventId) {
        EventApiService apiService = RetrofitClient.getRetrofit(this).create(EventApiService.class);

        apiService.findEventTags(eventId).enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    eventTagsList = response.body();

                    StringBuilder builder = new StringBuilder();
                    for (Tag tag : eventTagsList) {
                        builder.append("#").append(tag.getName()).append(" ");
                    }
                    eventTags.setText(builder.toString());
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
            }
        });
    }

    public void fetchSwitchLike(Long userId, Long eventId) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiService.setLiked(userId, eventId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchEventLikes(eventId);
                    //System.out.println("Event liked successfully.");
                } else {
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void fetchSwitchCalendar(Long userId, Long eventId) {
        UserApiService apiService = RetrofitClient.getRetrofit(this).create(UserApiService.class);

        apiService.setCalendar(userId, eventId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    //System.out.println("Event added to calendar successfully.");
                } else {
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}