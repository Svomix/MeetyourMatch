package com.javanostra.meetyourmatch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    private boolean isLiked;
    private boolean isAddedToCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra("event");

        TextView eventTitle = findViewById(R.id.event_title);
        TextView eventDescription = findViewById(R.id.event_description);
        TextView eventDate = findViewById(R.id.event_date);

        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());
        eventDate.setText(dateCorrectImplementation(event));

        ImageButton likeButton = findViewById(R.id.likeButton);
        likeButton.setColorFilter(Color.parseColor("#FCAD2F"));
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
        });

        ImageButton calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> {
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
        });
    }

    private String dateCorrectImplementation(Event event) {
        StringBuilder builder = new StringBuilder("Дата: ");

        int buffer = event.getDate();
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        buffer = event.getMonthAsIndex()+1;
        if (buffer < 10) builder.append("0");
        builder.append(buffer).append('.');

        builder.append(event.getYear());

        return builder.toString();
    }
}