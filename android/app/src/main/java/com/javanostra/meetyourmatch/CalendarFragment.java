package com.javanostra.meetyourmatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private Calendar calendar;
    private TextView monthTitle;
    private GridLayout calendarGrid;
    private int currentYear, currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);

        monthTitle = view.findViewById(R.id.month_title);
        calendarGrid = view.findViewById(R.id.calendar_grid);

        view.findViewById(R.id.prev_month_button).setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < Calendar.JANUARY) {
                currentMonth = Calendar.DECEMBER;
                currentYear--;
            }
            updateCalendar();
        });

        view.findViewById(R.id.next_month_button).setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > Calendar.DECEMBER) {
                currentMonth = Calendar.JANUARY;
                currentYear++;
            }
            updateCalendar();
        });

        updateCalendar();
        return view;
    }

    Event[] events = new Event[]{
            new Event("Name", "Desc", 8, 7, 2024),
            new Event("Name", "Desc", 12, 8, 2024),
            new Event("Name", "Desc", 22, 9, 2024),
            new Event("Name", "Desc", 25, 10, 2024),
            new Event("Name", "Desc", 30, 11, 2024)
    };

    private void updateCalendar() {
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        String[] months = new String[]{
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };
        String monthName = months[currentMonth];
        monthTitle.setText(monthName + " " + currentYear);

        calendarGrid.removeAllViews();

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7;
        }

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfMonth + daysInMonth; i++) {
            View dayItemView = LayoutInflater.from(getContext()).inflate(R.layout.day_item, null);

            TextView dayText = dayItemView.findViewById(R.id.day_text);
            TextView eventCountText = dayItemView.findViewById(R.id.event_count_text);

            dayText.setTextSize(18f);

            if (i < firstDayOfMonth) {
                dayText.setText("");
                eventCountText.setText("");
                eventCountText.setVisibility(View.GONE);
            } else {
                int day = i - firstDayOfMonth + 1;
                dayText.setText(String.valueOf(day));

                Day currentDay = new Day(day);
                for (Event event : events) {
                    if (currentYear == event.getYear() && currentMonth == event.getMonth() && day == event.getDate()) {
                        currentDay.addEvent(event);
                    }
                }

                int eventCount = currentDay.getEventCount();
                if (eventCount > 0) {
                    eventCountText.setText(String.valueOf(eventCount));
                    eventCountText.setVisibility(View.VISIBLE);
                } else {
                    eventCountText.setVisibility(View.GONE);
                }

                if (isToday(day, currentMonth, currentYear)) {
                    dayItemView.setBackgroundResource(R.drawable.today_border);
                } else {
                    dayItemView.setBackgroundResource(R.drawable.everyday_border);
                }

                int finalDay = day;
                dayItemView.setOnClickListener(v -> showEventsDialog(finalDay));
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            dayItemView.setLayoutParams(params);
            calendarGrid.addView(dayItemView);
        }
    }

    private boolean isToday(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == year &&
                today.get(Calendar.MONTH) == month &&
                today.get(Calendar.DAY_OF_MONTH) == day;
    }

    private void showEventsDialog(int day) {
        // Сделать логику
    }
}
