package com.javanostra.meetyourmatch.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private OnEventClickListener clickListener;

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.clickListener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events == null ? new ArrayList<>() : events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_detail, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, clickListener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView backgroundImageView;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewEventTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewEventDescription);
            backgroundImageView = itemView.findViewById(R.id.imageViewEventBackground);
        }

        void bind(final Event event, final OnEventClickListener listener) {
            titleTextView.setText(event.getTitle());
            descriptionTextView.setText(event.getDescription());

            String imageUrl = event.getCoverImgUrl();
            RequestOptions options = RequestOptions
                    .bitmapTransform(new BlurTransformation(1, 1))
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .error(new ColorDrawable(Color.DKGRAY));

            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(itemView.getContext())
                        .asBitmap()
                        .load(imageUrl)
                        .apply(options)
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .into(backgroundImageView);
            } else {
                Glide.with(itemView.getContext()).clear(backgroundImageView);
                backgroundImageView.setImageDrawable(new ColorDrawable(Color.DKGRAY));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEventClick(event);
                    }
                }
            });
        }
    }
}