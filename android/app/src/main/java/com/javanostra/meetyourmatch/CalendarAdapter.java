package com.javanostra.meetyourmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<Day> days;
    private Context context;
    private OnDayClickListener onDayClickListener;

    public CalendarAdapter(Context context, List<Day> days, OnDayClickListener onDayClickListener) {
        this.context = context;
        this.days = days;
        this.onDayClickListener = onDayClickListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.day_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Day day = days.get(position);
        holder.dayText.setText(String.valueOf(day.getDate()));

        holder.itemView.setOnClickListener(v -> {
            onDayClickListener.onDayClick(day);
        });

        if (day.hasEvents()) {
            // Сделать стиль для дня с событиями
        } else {
            // Сделать стиль для обычных дней
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.day_text);
        }
    }

    public interface OnDayClickListener {
        void onDayClick(Day day);
    }
}