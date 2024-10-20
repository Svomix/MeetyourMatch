package com.javanostra.meetyourmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

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