package com.javanostra.meetyourmatch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameView, lastMessageView, timeView;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.Avatar);
        nameView = itemView.findViewById(R.id.Username);
        lastMessageView = itemView.findViewById(R.id.LastMessage);
        timeView = itemView.findViewById(R.id.Time);
    }


}
