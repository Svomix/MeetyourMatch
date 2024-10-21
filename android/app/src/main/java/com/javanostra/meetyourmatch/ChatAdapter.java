package com.javanostra.meetyourmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    Context context;
    List<User> users;

    public ChatAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_box, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.nameView.setText(users.get(position).getUsername());
        holder.lastMessageView.setText("Привет, это " +  users.get(position).getUsername());
        Random random = new Random();
        String hour = String.valueOf(random.nextInt(23));
        String minute = String.valueOf(random.nextInt(60));
        if (minute.length() == 1) minute = "0" + minute;
        holder.timeView.setText(hour + ":" + minute);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
