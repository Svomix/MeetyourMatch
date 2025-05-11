package com.javanostra.meetyourmatch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;
import java.util.ArrayList;
import java.util.List;

public class SelectedParticipantsAdapter extends RecyclerView.Adapter<SelectedParticipantsAdapter.ParticipantViewHolder> {

    private List<UserProfileDTO> selectedUsers = new ArrayList<>();
    private final OnParticipantRemoveListener removeListener;

    public interface OnParticipantRemoveListener {
        void onParticipantRemoved(UserProfileDTO user);
    }

    public SelectedParticipantsAdapter(OnParticipantRemoveListener listener) {
        this.removeListener = listener;
    }

    public void setParticipants(List<UserProfileDTO> users) {
        this.selectedUsers = new ArrayList<>(users);
        notifyDataSetChanged();
    }

    public List<UserProfileDTO> getParticipants() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        UserProfileDTO user = selectedUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    class ParticipantViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        ImageButton removeButton;

        ParticipantViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.participant_avatar);
            nameTextView = itemView.findViewById(R.id.participant_name);
            removeButton = itemView.findViewById(R.id.button_remove_participant);

            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && removeListener != null) {
                    removeListener.onParticipantRemoved(selectedUsers.get(position));
                }
            });
        }

        void bind(UserProfileDTO user) {
            nameTextView.setText(user.getUsername());
            Glide.with(itemView.getContext())
                    .load(user.getAvatarPath())
                    .placeholder(R.drawable.avatar) // Общая заглушка
                    .error(R.drawable.avatar)
                    .transform(new CircleCrop())
                    .into(avatarImageView);
        }
    }
}