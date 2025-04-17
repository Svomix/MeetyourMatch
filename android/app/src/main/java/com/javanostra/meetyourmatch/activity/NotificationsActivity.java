package com.javanostra.meetyourmatch.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.NotificationAdapter;
import com.javanostra.meetyourmatch.persistance.entity.Notification;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    List<Notification> notifications = List.of(
      new Notification(),
      new Notification(),
      new Notification(),
      new Notification(),
      new Notification()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.bars));
        setContentView(R.layout.activity_notifications);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.bars));

        RecyclerView recyclerView = findViewById(R.id.NotificationRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NotificationAdapter(this, notifications));
    }
}