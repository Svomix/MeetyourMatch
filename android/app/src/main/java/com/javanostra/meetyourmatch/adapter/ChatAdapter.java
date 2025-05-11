package com.javanostra.meetyourmatch.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.ChatInfoDTO;
import com.javanostra.meetyourmatch.persistance.entity.ChatUserDTO;
import com.javanostra.meetyourmatch.persistance.entity.Relation; 
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class ChatAdapter extends ListAdapter<ChatUserDTO, ChatAdapter.ChatViewHolder> {

    private final OnChatItemClickListener clickListener;

    public interface OnChatItemClickListener {
        void onItemClick(ChatUserDTO chatUser);
        
    }

    public ChatAdapter(@NonNull OnChatItemClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatUserDTO currentChatUser = getItem(position);
        holder.bind(currentChatUser);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatarImageView;
        private final FrameLayout iconContainer;
        private final ImageView iconFriend;
        private final ImageView iconBlocked;
        private final ImageView iconGroup;
        private final TextView nameView;
        private final TextView lastMessageView;
        private final TextView timeView;

        private SimpleDateFormat timeFormatter;
        private ChatUserDTO currentChatUser;

        public ChatViewHolder(@NonNull View itemView, @NonNull final OnChatItemClickListener listener) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.chat_list_avatar);
            iconContainer = itemView.findViewById(R.id.frameLayout);
            iconFriend = itemView.findViewById(R.id.chat_list_icon_friend);
            iconBlocked = itemView.findViewById(R.id.chat_list_icon_blocked);
            iconGroup = itemView.findViewById(R.id.chat_list_icon_group);
            nameView = itemView.findViewById(R.id.chat_list_username);
            lastMessageView = itemView.findViewById(R.id.chat_list_last_message);
            timeView = itemView.findViewById(R.id.chat_list_time);
            timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

            itemView.setOnClickListener(v -> {
                if (currentChatUser != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(currentChatUser);
                }
            });
        }

        public void bind(@NonNull ChatUserDTO chatUser) {
            this.currentChatUser = chatUser;
            ChatInfoDTO chatInfo = chatUser.getUserProfile();

            if (chatInfo == null) {
                Log.e("ChatViewHolder", "UserProfileDTO is null inside ChatUserDTO at position " + getAdapterPosition());
                nameView.setText(R.string.error_unknown_user);
                lastMessageView.setText("");
                timeView.setText("");
                avatarImageView.setImageResource(R.drawable.avatar);
                iconContainer.setVisibility(View.GONE);
                iconFriend.setVisibility(View.GONE);
                iconBlocked.setVisibility(View.GONE);
                if (iconGroup != null) iconGroup.setVisibility(View.GONE);
                itemView.setAlpha(1.0f); 
                return;
            }

            nameView.setText(chatInfo.getUsername());
            lastMessageView.setText(chatInfo.getLastMessage());
            if (chatInfo.getLastMessageTime() != null) {
                timeView.setText(timeFormatter.format(chatInfo.getLastMessageTime()));
            } else {
                timeView.setText("");
            }

            itemView.setAlpha(1.0f);
            nameView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.textColorPrimaryModern));
            lastMessageView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.textColorSecondaryModern));
            iconContainer.setVisibility(View.GONE);
            iconFriend.setVisibility(View.GONE);
            iconBlocked.setVisibility(View.GONE);
            if (iconGroup != null) iconGroup.setVisibility(View.GONE);

            if (chatInfo.getIsGroup()) {
                if (iconGroup != null) {
                    iconContainer.setVisibility(View.VISIBLE);
                    iconGroup.setVisibility(View.VISIBLE);
                }
                Glide.with(itemView.getContext())
                        .load(chatInfo.getAvatarPath())
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .transform(new CircleCrop())
                        .into(avatarImageView);
            } else {
                switch (chatUser.getRelationStatus()) {
                    case FRIEND:
                        iconContainer.setVisibility(View.VISIBLE);
                        iconFriend.setVisibility(View.VISIBLE);
                        break;
                    case BLOCKED:
                        iconContainer.setVisibility(View.VISIBLE);
                        iconBlocked.setVisibility(View.VISIBLE);
                        nameView.setTextColor(Color.parseColor("#FF8D19"));
                        lastMessageView.setTextColor(Color.parseColor("#FF8D19"));
                        itemView.setAlpha(0.6f);
                        break;
                    case NONE:
                    default:
                        break;
                }
                Glide.with(itemView.getContext())
                        .load(chatInfo.getAvatarPath())
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .transform(new CircleCrop())
                        .into(avatarImageView);
            }
        }
    }

    private static final DiffUtil.ItemCallback<ChatUserDTO> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatUserDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatUserDTO oldItem, @NonNull ChatUserDTO newItem) {
                    ChatInfoDTO oldProfile = oldItem.getUserProfile();
                    ChatInfoDTO newProfile = newItem.getUserProfile();

                    if (oldProfile == null || newProfile == null) {
                        return oldProfile == newProfile;
                    }
                    return Objects.equals(oldProfile.getId(), newProfile.getId()) &&
                            oldProfile.getIsGroup() == newProfile.getIsGroup();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChatUserDTO oldItem, @NonNull ChatUserDTO newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };
}