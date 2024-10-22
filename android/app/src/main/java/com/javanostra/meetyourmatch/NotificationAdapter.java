package com.javanostra.meetyourmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    Context context;
    List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.notificationText.setText("This is a notificationText #" + position);
        holder.notificationHeader.setText("This is a notificationHeader #" + position);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }



    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView notificationHeader, notificationText, notificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationHeader = itemView.findViewById(R.id.NotificationHeader);
            notificationText = itemView.findViewById(R.id.NotificationText);
            notificationTime = itemView.findViewById(R.id.NotificationTime);
        }
    }
}
