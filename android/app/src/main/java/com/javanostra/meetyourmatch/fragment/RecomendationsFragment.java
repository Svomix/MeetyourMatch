package com.javanostra.meetyourmatch.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.EventDetailsActivity;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.Location;
import com.javanostra.meetyourmatch.persistance.entity.UserActionDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecomendationsFragment extends Fragment implements GestureDetector.OnGestureListener {
    private Event event;
    private int currentEventIndex = 0;
    private UserProfileDTO currentUser;

    private TextView event_title, event_description;
    private ImageView event_full_description, event_refresh, eventImage, action_image_view;
    private ImageButton like_button, dislike_button;
    private GestureDetector gestureDetector;

    HashMap<String, String> headers = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomendations, container, false);
        currentEventIndex = 0;

        event_title = view.findViewById(R.id.event_title);
        event_description = view.findViewById(R.id.event_description);
        event_full_description = view.findViewById(R.id.event_full_description_button);
        event_refresh = view.findViewById(R.id.event_refresh_button);
        like_button = view.findViewById(R.id.like_button);
        dislike_button = view.findViewById(R.id.dislike_button);
        action_image_view = view.findViewById(R.id.action_image_view);
        eventImage = view.findViewById(R.id.eventImage);

        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Mobile Safari/537.36");

        fetchOneEvent();
        fetchAccountInfo();

        event_full_description.setOnClickListener(v -> handleDescription(event));
        event_refresh.setOnClickListener(v -> handleRefresh());
        like_button.setOnClickListener(v -> handleLike());
        dislike_button.setOnClickListener(v -> handleDislike());

        gestureDetector = new GestureDetector(getContext(), this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        return view;
    }

    private void displayCurrentEvent() {
        if (event != null) {
            Event currentEvent = event;
            event_title.setText(currentEvent.getTitle());
            event_description.setText(currentEvent.getDescription());
            GlideUrl glideUrl = new GlideUrl(
                    currentEvent.getCoverImgUrl(),
                    new LazyHeaders.Builder().addHeader("User-Agent", Objects.requireNonNull(headers.get("User-Agent"))).build()
            );
            Log.d("GlideLoad", "Loading image from: " + currentEvent.getCoverImgUrl());
            Glide.with(this)
                    .load(glideUrl)
                    .timeout(30000)
                    .placeholder(R.drawable.ic_mym_128)
                    .error(R.drawable.ic_mym_128)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Load failed", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.d("GlideSuccess", "Image loaded successfully");
                            return false;
                        }
                    })
                    .into(eventImage);
        } else {
            event_title.setText("Нет больше мероприятий");
            event_description.setText("");
        }
    }

    private void handleDescription(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void handleRefresh() {
        currentEventIndex++;
        showActionImage(R.drawable.ic_baseline_refresh_24);

        fetchOneEvent();
    }

    public interface OnRecommendationsInteractionListener {
        void onSwitchToSearch();
    }

    private OnRecommendationsInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRecommendationsInteractionListener) {
            listener = (OnRecommendationsInteractionListener) context;
        }
    }

    private void handleLike() {
        fetchSwitchLike(event.getId(), true);
        fetchSwitchDislike(event.getId(), false);
        currentEventIndex++;
        showActionImage(R.drawable.ic_baseline_thumb_up_24);

        fetchOneEvent();
    }

    private void handleDislike() {
        fetchSwitchDislike(event.getId(), true);
        fetchSwitchLike(event.getId(), false);
        currentEventIndex++;
        showActionImage(R.drawable.ic_baseline_thumb_down_24);

        fetchOneEvent();
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        float SWIPE_THRESHOLD = 100;
        float SWIPE_VELOCITY_THRESHOLD = 100;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    animateSwipeToLeft(getView().findViewById(R.id.content_container), this::handleLike);
                } else {
                    animateSwipeToRight(getView().findViewById(R.id.content_container), this::handleDislike);
                }
                return true;
            }
        } else {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    animateSwipeToDown(getView().findViewById(R.id.content_container));
                } else {
                    animateSwipeToUp(getView().findViewById(R.id.content_container), this::handleRefresh);
                }
                return true;
            }
        }
        return false;
    }

    private void showActionImage(int imageResId) {
        action_image_view.setImageResource(imageResId);
        action_image_view.setAlpha(0.7f);
        action_image_view.setColorFilter(Color.parseColor("#FBB63F"));
        action_image_view.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        action_image_view.setAlpha(0f);
                    }
                })
                .start();
    }

    private void animateSwipeToUp(View view, Runnable onSwipeComplete) {
        view.animate()
                .translationY(-view.getHeight() / 3)
                .setDuration(400)
                .withEndAction(() -> {
                    view.setTranslationY(0);
                    view.setAlpha(0f);
                    onSwipeComplete.run();
                    view.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void animateSwipeToDown(View view) {
        handleDescription(event);
        view.animate()
                .translationY(view.getHeight() / 3)
                .setDuration(400)
                .withEndAction(() -> {
                    view.setTranslationY(0);
                    view.setAlpha(0f);

                    view.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void animateSwipeToLeft(View view, Runnable onSwipeComplete) {
        view.animate()
                .translationX(view.getWidth())
                .translationY(view.getHeight() / 5)
                .rotation(35)
                .setDuration(400)
                .withEndAction(() -> {
                    view.setTranslationX(0);
                    view.setTranslationY(0);
                    view.setRotation(0);
                    onSwipeComplete.run();
                    view.setAlpha(0f);
                    view.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void animateSwipeToRight(View view, Runnable onSwipeComplete) {
        view.animate()
                .translationX(-view.getWidth())
                .translationY(view.getHeight() / 5)
                .rotation(-35)
                .setDuration(400)
                .withEndAction(() -> {
                    view.setTranslationX(0);
                    view.setTranslationY(0);
                    view.setRotation(0);
                    onSwipeComplete.run();
                    view.setAlpha(0f);
                    view.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    public void fetchOneEvent() {
        EventApiService apiService = RetrofitClient.getRetrofit(getContext()).create(EventApiService.class);

        apiService.findRec().enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    event = response.body();
                    displayCurrentEvent();
                } else {
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
            }
        });
    }

    private void fetchAccountInfo() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(getActivity().getApplicationContext()).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.message());
                    currentUser = response.body();
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

    public void fetchSwitchLike(Long eventId, Boolean isLiked) {
        AccountApiService apiService = RetrofitClient.getRetrofit(getActivity().getApplicationContext()).create(AccountApiService.class);

        apiService.setLiked(eventId, isLiked).enqueue(new retrofit2.Callback<UserActionDTO>() {
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

    public void fetchSwitchDislike(Long eventId, Boolean isDisliked) {
        AccountApiService apiService = RetrofitClient.getRetrofit(getActivity().getApplicationContext()).create(AccountApiService.class);

        apiService.setDisliked(eventId, isDisliked).enqueue(new retrofit2.Callback<UserActionDTO>() {
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

}