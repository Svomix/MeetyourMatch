package com.javanostra.meetyourmatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.ChatMessagesActivity;
import com.javanostra.meetyourmatch.adapter.ChatRecyclerViewInterface;
import com.javanostra.meetyourmatch.adapter.ChatAdapter;
import com.javanostra.meetyourmatch.persistance.entity.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements ChatRecyclerViewInterface {

    private static final String TAG = "ChatFragment";

    // TODO
    public static final List<User> users = new ArrayList<>();
    static {
        users.add(new User("user1", "test@example.org", "Password"));
        users.add(new User("user2", "admin@example.org", "Password"));
        //users.add(new User("user3", "fish@lang.com", "Password"));
        //users.add(new User("user4", "fish@lang.com", "Password"));
    }

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (getContext() == null) {
            Log.e(TAG, "Context is null during setupRecyclerView");
            return;
        }
        chatAdapter = new ChatAdapter(getContext(), users, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void onItemClick(int position) {
        if (position >= 0 && position < users.size()) {
            User selectedUser = users.get(position);
            Log.d(TAG, "Chat item clicked: " + selectedUser.getUsername());

            Intent intent = new Intent(getActivity(), ChatMessagesActivity.class);
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_ID, selectedUser.getEmail());
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_USERNAME, selectedUser.getUsername());
            //intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_IMAGE_URL, selectedUser.getImageUrl());
            startActivity(intent);
        } else {
            Log.e(TAG, "Invalid position clicked: " + position);
            Toast.makeText(getContext(), "Error selecting chat", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: update users
}