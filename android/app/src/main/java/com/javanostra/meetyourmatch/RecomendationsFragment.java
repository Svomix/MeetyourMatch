package com.javanostra.meetyourmatch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class RecomendationsFragment extends Fragment implements GestureDetector.OnGestureListener {
    private List<Event> events;
    private int currentEventIndex = 0;

    private TextView event_title;
    private TextView event_description;
    private Button event_full_description;
    private ImageButton like_button;
    private ImageButton dislike_button;
    private GestureDetector gestureDetector;

    private ImageView action_image_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recomendations, container, false);

        event_title = view.findViewById(R.id.event_title);
        event_description = view.findViewById(R.id.event_description);
        event_full_description = view.findViewById(R.id.event_full_description);
        like_button = view.findViewById(R.id.like_button);
        dislike_button = view.findViewById(R.id.dislike_button);
        action_image_view = view.findViewById(R.id.action_image_view);

        events = new ArrayList<>();
        events.add(new Event("Name-2", "Desc", 8, 7, 2024));
        events.add(new Event("Name-1", "Desc", 12, 8, 2024));
        events.add(new Event("Name0", "DescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDesc", 22, 9, 2024));
        events.add(new Event("Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1", "Desc", 25, 10, 2024));
        events.add(new Event("Name2", "Desc", 25, 10, 2024));
        events.add(new Event("Name3", "Desc", 12, 7, 2024));
        events.add(new Event("Name4", "Desc", 20, 8, 2024));
        events.add(new Event("Name5", "Desc", 5, 11, 2024));
        events.add(new Event("Name6", "Desc", 9, 12, 2024));

        displayCurrentEvent();

        event_full_description.setOnClickListener(v -> handleDescription(events.get(currentEventIndex)));
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
        if (currentEventIndex < events.size()) {
            Event currentEvent = events.get(currentEventIndex);
            event_title.setText(currentEvent.getTitle());
            event_description.setText(currentEvent.getDescription());
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

    private void handleLike() {
        currentEventIndex++;
        showActionImage(R.drawable.ic_baseline_thumb_up_24);

        displayCurrentEvent();
    }

    private void handleDislike() {
        currentEventIndex++;
        showActionImage(R.drawable.ic_baseline_thumb_down_24);

        displayCurrentEvent();
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
                    animateSwipeToLeft(getView().findViewById(R.id.root_layout), this::handleLike);
                    Toast.makeText(getContext(), "animateSwipeToLeft", Toast.LENGTH_LONG).show();
                } else {
                    animateSwipeToRight(getView().findViewById(R.id.root_layout), this::handleDislike);
                    Toast.makeText(getContext(), "animateSwipeToRight", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        }
        return false;
    }

    private void showActionImage(int imageResId) {
        action_image_view.setImageResource(imageResId);
        action_image_view.setAlpha(0.7f);
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
}