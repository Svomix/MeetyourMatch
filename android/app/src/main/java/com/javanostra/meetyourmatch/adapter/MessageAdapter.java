package com.javanostra.meetyourmatch.adapter;

import android.content.Context;
import android.text.format.DateFormat; // For formatting timestamp
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;

import java.util.List;
import java.util.Date; // Import Date

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<ChatMessage> messages;
    private String currentUserId;

    public MessageAdapter(Context context, List<ChatMessage> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_received, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageView, messageTimeView;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            textMessageView = itemView.findViewById(R.id.text_message_body);
            messageTimeView = itemView.findViewById(R.id.text_message_time);
            // if (viewType == VIEW_TYPE_RECEIVED) {
            //     senderNameView = itemView.findViewById(R.id.text_sender_name);
            // }
        }

        void bind(ChatMessage message) {
            textMessageView.setText(message.getContent());
            if (message.getTimestamp() != null) {
                messageTimeView.setText(DateFormat.format("HH:mm", message.getTimestamp()));
            } else {
                messageTimeView.setText("");
            }

            // if (senderNameView != null) {
            //     senderNameView.setText(message.getSenderId()); // Or fetch username if needed
            // }
        }
    }
}