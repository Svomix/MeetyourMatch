package com.javanostra.meetyourmatch;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatMessagesActivity extends AppCompatActivity {

    public static HashMap<Integer, List<Message>> historyChat = new HashMap<>();

    static {
        List<Message> messages = List.of(
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "12:30"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "12:32"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "12:36"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "13:00"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "13:01"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 0, -1, "13:30"),
                new Message("Last message of 0.", 0, -1, "14:30")
        );

        historyChat.put(0, messages);

        messages = List.of(
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "12:30"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "12:32"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "12:36"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "13:00"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "13:01"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 1, -1, "13:30"),
                new Message("Last message of 1", 1, -1, "14:30")
        );

        historyChat.put(1, messages);

        messages = List.of(
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "12:30"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "12:32"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "12:36"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "13:00"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "13:01"),
                new Message("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.", 2, -1, "13:30"),
                new Message("Last message of 2", 2, -1, "14:30")
        );

        historyChat.put(2, messages);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_messages);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.bars));
        String username = getIntent().getStringExtra("Username");
        int IdSender = getIntent().getIntExtra("IDSender", -1);


        ((TextView) findViewById(R.id.Username)).setText(username);
        RecyclerView recyclerView = findViewById(R.id.MessageContainer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MessageAdapter(getApplicationContext(), historyChat.getOrDefault(IdSender, new ArrayList<>())));
    }
}