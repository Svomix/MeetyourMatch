package com.javanostra.meetyourmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.fragment.ChatFragment;
import com.javanostra.meetyourmatch.persistance.entity.Message;
import com.javanostra.meetyourmatch.persistance.entity.User;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    Context context;
    List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        User sender = ChatFragment.users.get(messages.get(position).getUser_id());
        holder.nameView.setText(sender.getUsername());
        holder.textMessageView.setText(messages.get(position).getContent());
        holder.messageTimeView.setText(messages.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView, textMessageView, messageTimeView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.Avatar);
            textMessageView = itemView.findViewById(R.id.Message);
            nameView = itemView.findViewById(R.id.Username);
            messageTimeView = itemView.findViewById(R.id.Time);
        }
    }
}
