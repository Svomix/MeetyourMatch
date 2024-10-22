package com.javanostra.meetyourmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final ChatRecyclerViewInterface recyclerViewInterface;
    Context context;
    List<User> users;

    public ChatAdapter(Context context, List<User> users, ChatRecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.users = users;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_box, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.nameView.setText(users.get(position).getUsername());

        List<Message> messages = ChatMessagesActivity.historyChat.getOrDefault(position, new ArrayList<>());
        Message lastMessage = (messages.size() == 0? new Message("NULL", position, -1, "NULL") : messages.get(messages.size() - 1));
        String time = lastMessage.getDateOfSending();
        String text = lastMessage.getMessage();
        holder.lastMessageView.setText(text);
        holder.timeView.setText(time);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView, lastMessageView, timeView;

        public ChatViewHolder(@NonNull View itemView, ChatRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.Avatar);
            nameView = itemView.findViewById(R.id.Username);
            lastMessageView = itemView.findViewById(R.id.LastMessage);
            timeView = itemView.findViewById(R.id.Time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }


    }
}
