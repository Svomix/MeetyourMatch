package com.javanostra.meetyourmatch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessage;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class GroupMessageAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE_HEADER = 3;

    private static final long HIDE_AVATAR_NAME_THRESHOLD_MILLIS = 5 * 60 * 1000; // 5min

    private final Long currentUserId;
    private final SimpleDateFormat timeFormatter;

    public GroupMessageAdapter(Long currentUserId) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        this.timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof GroupChatMessage) {
            GroupChatMessage message = (GroupChatMessage) item;
            if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
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
            case VIEW_TYPE_MESSAGE_SENT:
                View sentView = inflater.inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(sentView);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                View receivedView = inflater.inflate(R.layout.item_group_message_received, parent, false);
                return new ReceivedMessageViewHolder(receivedView);
            case VIEW_TYPE_DATE_HEADER:
                View dateView = inflater.inflate(R.layout.item_date_header, parent, false);
                return new DateHeaderViewHolder(dateView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object currentItem = getItem(position);
        Object previousItem = (position > 0) ? getItem(position - 1) : null;

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder).bind((GroupChatMessage) currentItem);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind((GroupChatMessage) currentItem, previousItem);
                break;
            case VIEW_TYPE_DATE_HEADER:
                ((DateHeaderViewHolder) holder).bind((DateHeaderItem) currentItem);
                break;
        }
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(GroupChatMessage message) {
            messageText.setText(message.getContent());
            if (message.getTimestamp() != null) {
                timeText.setText(timeFormatter.format(message.getTimestamp()));
            } else {
                timeText.setText("");
            }
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, senderNameText;
        ImageView senderAvatarImage;
        View senderInfoLayout;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_group_message_body_received);
            timeText = itemView.findViewById(R.id.text_group_message_time_received);
            senderNameText = itemView.findViewById(R.id.text_group_sender_name);
            senderAvatarImage = itemView.findViewById(R.id.image_group_sender_avatar);
            senderInfoLayout = itemView.findViewById(R.id.layout_sender_info_container);
        }

        void bind(GroupChatMessage currentMessage, Object previousItemObject) {
            messageText.setText(currentMessage.getContent());

            if (currentMessage.getTimestamp() != null) {
                timeText.setText(timeFormatter.format(currentMessage.getTimestamp()));
            } else {
                timeText.setText("");
            }

            boolean showSenderInfo = true;

            if (previousItemObject instanceof GroupChatMessage) {
                GroupChatMessage previousMessage = (GroupChatMessage) previousItemObject;

                boolean previousWasSentByCurrentUser = previousMessage.getSenderId() != null && previousMessage.getSenderId().equals(currentUserId);

                if (!previousWasSentByCurrentUser &&
                        previousMessage.getSenderId() != null &&
                        previousMessage.getSenderId().equals(currentMessage.getSenderId())) {

                    if (currentMessage.getTimestamp() != null && previousMessage.getTimestamp() != null) {
                        long timeDiff = currentMessage.getTimestamp().getTime() - previousMessage.getTimestamp().getTime();
                        if (timeDiff < HIDE_AVATAR_NAME_THRESHOLD_MILLIS) {
                            showSenderInfo = false;
                        }
                    } else {
                        showSenderInfo = true;
                    }
                }
            }

            else if (previousItemObject instanceof DateHeaderItem) {
                showSenderInfo = true;
            }


            if (showSenderInfo) {
                if (senderInfoLayout != null) senderInfoLayout.setVisibility(View.VISIBLE);
                senderNameText.setText(currentMessage.getSenderUsername());
                Glide.with(itemView.getContext())
                        .load(currentMessage.getSenderAvatarPath())
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .transform(new CircleCrop())
                        .into(senderAvatarImage);
            } else {
                if (senderInfoLayout != null) senderInfoLayout.setVisibility(View.GONE);
                // ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageText.getLayoutParams();
                // params.leftMargin = desired_left_margin_when_avatar_hidden;
                // messageText.setLayoutParams(params);
            }
        }
    }

    class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

        DateHeaderViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateHeaderText);
        }

        void bind(DateHeaderItem dateHeaderItem) {
            dateText.setText(DateUtils.formatDateForHeader(itemView.getContext(), dateHeaderItem.getTimestamp()));
        }
    }


    private static final DiffUtil.ItemCallback<Object> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Object>() {
                @Override
                public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof GroupChatMessage && newItem instanceof GroupChatMessage) {
                        return Objects.equals(((GroupChatMessage) oldItem).getId(), ((GroupChatMessage) newItem).getId());
                    } else if (oldItem instanceof DateHeaderItem && newItem instanceof DateHeaderItem) {
                        return DateUtils.isSameDay(((DateHeaderItem) oldItem).getTimestamp(), ((DateHeaderItem) newItem).getTimestamp());
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof GroupChatMessage && newItem instanceof GroupChatMessage) {
                        return oldItem.equals(newItem);
                    } else if (oldItem instanceof DateHeaderItem && newItem instanceof DateHeaderItem) {
                        return oldItem.equals(newItem);
                    }
                    return false;
                }
            };
}