package com.javanostra.meetyourmatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements GestureDetector.OnGestureListener {

    private Calendar calendar;
    private TextView monthTitle;
    private GridLayout calendarGrid;
    private int currentYear, currentMonth;

    AlertDialog dialog;
    private GestureDetector gestureDetector;

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

        gestureDetector = new GestureDetector(getContext(), this);
        ViewGroup rootView = view.findViewById(R.id.root_layout);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        updateCalendar();
        return view;
    }

    Event[] events = new Event[] {
            new Event("Name", "Desc", 8, 7, 2024),
            new Event("Name", "Desc", 12, 8, 2024),
            new Event("Name", "Desc", 22, 9, 2024),
            new Event("Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1Name1", "Desc", 25, 10, 2024),
            new Event("Name2", "Desc", 25, 10, 2024),
            new Event("Name3", "Desc", 25, 10, 2024),
            new Event("Name4", "Desc", 25, 10, 2024),
            new Event("Name5", "Desc", 25, 10, 2024),
            new Event("Name6", "Desc", 25, 10, 2024),
            new Event("Name7", "Desc", 25, 10, 2024),
            new Event("Name8", "Desc", 25, 10, 2024),
            new Event("Name9", "Desc", 25, 10, 2024),
            new Event("Name10", "Desc", 25, 10, 2024),
            new Event("Name11", "Desc", 25, 10, 2024),
            new Event("Name12", "Desc", 25, 10, 2024),
            new Event("Name13", "Desc", 25, 10, 2024),
            new Event("Name14", "Desc", 25, 10, 2024),
            new Event("Name15", "Desc", 25, 10, 2024),
            new Event("Name16", "Desc", 25, 10, 2024),
            new Event("Name17", "Desc", 25, 10, 2024),
            new Event("Name18", "Desc", 25, 10, 2024),
            new Event("Name19", "Desc", 25, 10, 2024),
            new Event("Name20", "Desc", 25, 10, 2024),
            new Event("Name21", "Desc", 25, 10, 2024),
            new Event("Name22", "Desc", 25, 10, 2024),
            new Event("Name23", "Desc", 25, 10, 2024),
            new Event("Name24", "Desc", 25, 10, 2024),
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
                    if (currentYear == event.getYear() && currentMonth == event.getMonthAsIndex() && day == event.getDate()) {
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
                } else if (currentDay.hasEvents()) {
                    dayItemView.setBackgroundResource(R.drawable.day_with_event_border);
                } else {
                    dayItemView.setBackgroundResource(R.drawable.everyday_border);
                }

                dayItemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return false;
                    }
                });

                dayItemView.setOnClickListener(v -> {
                    if (currentDay.hasEvents())
                        showEventsDialog(currentDay);
                });
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

    private void showEventsDialog(Day currentDay) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Мероприятия на " + currentDay.getDate() + " число");

        LinearLayout eventsContainer = dialogView.findViewById(R.id.events_container);

        for (Event event : currentDay.getEvents()) {
            TextView eventTextView = new TextView(getContext());
            eventTextView.setText(event.getTitle());
            eventTextView.setPadding(40, 55, 40, 55);
            eventTextView.setTextSize(16);
            eventTextView.setTextColor(Color.WHITE);
            eventTextView.setGravity(Gravity.CENTER_VERTICAL);
            eventTextView.setClickable(true);

            eventTextView.setOnClickListener(v -> openEventDetails(event));

            eventsContainer.addView(eventTextView);
        }
        builder.setView(dialogView);

        dialog = builder.create();
        dialog.setCancelable(true);

        dialog.show();
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        dialog.cancel();
        startActivity(intent);
    }

    private void goToPreviousMonth() {
        currentMonth--;
        if (currentMonth < Calendar.JANUARY) {
            currentMonth = Calendar.DECEMBER;
            currentYear--;
        }
        updateCalendar();
    }

    private void goToNextMonth() {
        currentMonth++;
        if (currentMonth > Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY;
            currentYear++;
        }
        updateCalendar();
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @Nullable MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        float SWIPE_THRESHOLD = 100;
        float SWIPE_VELOCITY_THRESHOLD = 100;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    animateSwipeToLeft(getView().findViewById(R.id.calendar_grid), this::goToPreviousMonth);
                } else {
                    animateSwipeToRight(getView().findViewById(R.id.calendar_grid), this::goToNextMonth);
                }
                return true;
            }
        }
        return false;
    }

    private void animateSwipeToLeft(View view, Runnable onSwipeComplete) {
        view.animate()
                .translationX(+view.getWidth())
                .setDuration(300)
                .withEndAction(() -> {
                    view.setTranslationX(0);
                    onSwipeComplete.run();
                    view.setAlpha(0f);
                    view.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void animateSwipeToRight(View view, Runnable onSwipeComplete) {
        view.animate()
                .translationX(-view.getWidth())
                .setDuration(300)
                .withEndAction(() -> {
                    view.setTranslationX(0);
                    onSwipeComplete.run();
                    view.setAlpha(0f);
                    view.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {return true;}
    @Override
    public void onShowPress(@NonNull MotionEvent e) {}
    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {return false;}
    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {return false;}
    @Override
    public void onLongPress(@NonNull MotionEvent e) {}
}
