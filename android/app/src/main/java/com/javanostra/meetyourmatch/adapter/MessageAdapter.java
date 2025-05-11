package com.javanostra.meetyourmatch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;

import java.util.Objects;

public class MessageAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE_HEADER = 3;

    private final String currentUserId;

    public MessageAdapter(@NonNull String currentUserId) { 
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof ChatMessage) {
            ChatMessage message = (ChatMessage) item;
            
            if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        } else if (item instanceof DateHeaderItem) {
            return VIEW_TYPE_DATE_HEADER;
        }
        
        return super.getItemViewType(position); 
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SENT:
                View sentView = inflater.inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(sentView);
            case VIEW_TYPE_RECEIVED:
                View receivedView = inflater.inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(receivedView);
            case VIEW_TYPE_DATE_HEADER:
                View headerView = inflater.inflate(R.layout.item_date_header, parent, false);
                return new DateHeaderViewHolder(headerView);
            default:
                View defaultView = inflater.inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(defaultView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = getItem(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENT:
                ((SentMessageViewHolder) holder).bind((ChatMessage) item);
                break;
            case VIEW_TYPE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind((ChatMessage) item);
                break;
            case VIEW_TYPE_DATE_HEADER:
                ((DateHeaderViewHolder) holder).bind((DateHeaderItem) item);
                break;
        }
    }

    

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            
            timeText.setText(DateUtils.formatTime(itemView.getContext(), message.getTimestamp().getTime()));
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            
            timeText.setText(DateUtils.formatTime(itemView.getContext(), message.getTimestamp().getTime()));
        }
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeaderText;

        DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeaderText = itemView.findViewById(R.id.dateHeaderText);
        }

        void bind(DateHeaderItem header) {
            dateHeaderText.setText(DateUtils.formatDateForHeader(itemView.getContext(), header.getTimestamp()));
        }
    }

    private static final DiffUtil.ItemCallback<Object> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Object>() {
                @Override
                public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof ChatMessage && newItem instanceof ChatMessage) {
                        ChatMessage oldMsg = (ChatMessage) oldItem;
                        ChatMessage newMsg = (ChatMessage) newItem;
                        return Objects.equals(oldMsg.getTimestamp(), newMsg.getTimestamp()) &&
                                Objects.equals(oldMsg.getSenderId(), newMsg.getSenderId()) &&
                                Objects.equals(oldMsg.getRecipientId(), newMsg.getRecipientId());
                    } else if (oldItem instanceof DateHeaderItem && newItem instanceof DateHeaderItem) {
                        return Objects.equals(((DateHeaderItem) oldItem).getTimestamp(), ((DateHeaderItem) newItem).getTimestamp());
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof ChatMessage && newItem instanceof ChatMessage) {
                        return Objects.equals(((ChatMessage) oldItem).getContent(), ((ChatMessage) newItem).getContent());
                    } else if (oldItem instanceof DateHeaderItem && newItem instanceof DateHeaderItem) {
                        return Objects.equals(oldItem, newItem);
                    }
                    return false;
                }
            };
}