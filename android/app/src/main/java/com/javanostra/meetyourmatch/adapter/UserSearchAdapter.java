package com.javanostra.meetyourmatch.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {

    private List<UserProfileDTO> userListFull; 
    private List<UserProfileDTO> userListFiltered; 
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserProfileDTO user);
    }

    public UserSearchAdapter(OnUserClickListener listener) {
        this.listener = listener;
        this.userListFull = new ArrayList<>();
        this.userListFiltered = new ArrayList<>();
    }

    
    public void setUsers(List<UserProfileDTO> users) {
        this.userListFull = new ArrayList<>(users); 
        filter(""); 
    }

    
    public void filter(String query) {
        userListFiltered.clear();
        if (query == null || query.isEmpty()) {
            userListFiltered.addAll(userListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (UserProfileDTO user : userListFull) {
                if (user.getUsername().toLowerCase().contains(lowerCaseQuery) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseQuery))) {
                    userListFiltered.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<UserProfileDTO> getFilteredList() {
        return userListFiltered;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_search, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserProfileDTO user = userListFiltered.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        ImageView imageViewUserAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            imageViewUserAvatar = itemView.findViewById(R.id.chat_list_avatar);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(userListFiltered.get(position));
                }
            });
        }

        public void bind(UserProfileDTO user) {
            textViewUserName.setText(user.getUsername());
            if (user.getAvatarPath() != null && !Objects.equals(user.getAvatarPath(), "")) {
                Uri imgUri = Uri.parse(user.getAvatarPath());
                if (imgUri != null) imageViewUserAvatar.setImageURI(imgUri);
                else imageViewUserAvatar.setImageResource(R.drawable.big_avatar);
            } else {
                imageViewUserAvatar.setImageResource(R.drawable.big_avatar);
            }
        }
    }
}