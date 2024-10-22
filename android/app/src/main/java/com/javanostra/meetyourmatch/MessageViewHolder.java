package com.javanostra.meetyourmatch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

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
