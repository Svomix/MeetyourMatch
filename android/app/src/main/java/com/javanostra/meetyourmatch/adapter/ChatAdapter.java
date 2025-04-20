package com.javanostra.meetyourmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.ChatRecyclerViewInterface;
import com.javanostra.meetyourmatch.persistance.entity.User;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final ChatRecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<User> users;

    public ChatAdapter(Context context, List<User> users, ChatRecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.users = users;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameView.setText(user.getUsername());

        holder.lastMessageView.setText("Tap to chat...");
        holder.timeView.setText(""); // Placeholder

        Glide.with(context)
                .load(user.getAvatarPath())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .circleCrop()
                .into(holder.avatarImageView);

    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void updateUserList(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImageView;
        TextView nameView, lastMessageView, timeView;

        public ChatViewHolder(@NonNull View itemView, ChatRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.chat_list_avatar);
            nameView = itemView.findViewById(R.id.chat_list_username);
            lastMessageView = itemView.findViewById(R.id.chat_list_last_message);
            timeView = itemView.findViewById(R.id.chat_list_time);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}