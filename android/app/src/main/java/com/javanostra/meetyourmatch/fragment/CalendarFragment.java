package com.javanostra.meetyourmatch.fragment;

import static android.app.Activity.RESULT_OK;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.EventDetailsActivity;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.Day;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.UserActionDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserEventDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements GestureDetector.OnGestureListener {

    private Calendar calendar;
    private TextView monthTitle;
    private GridLayout calendarGrid;
    private int currentYear, currentMonth;

    AlertDialog dialog;
    private GestureDetector gestureDetector;

    UserProfileDTO currentUser;
    private List<Event> userEventsItem;
    private List<UserActionDTO> userEvents;

    private ActivityResultLauncher<Intent> eventDetailsLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        userEventsItem = new ArrayList<>();

        fetchAccountInfo();

        eventDetailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        userEventsItem = new ArrayList<>();
                        fetchAllUserEvents(currentUser.getId());
                    }
                }
        );

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

        //updateCalendar();
        return view;
    }

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
                for (Event event : userEventsItem) {
                    Timestamp eventDate = event.getDate();
                    if (currentYear == eventDate.getYear()+1900 && currentMonth == eventDate.getMonth() - 1 && day == eventDate.getDate()) {
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
                        showEventDetailsSheet(currentDay.getEvents());
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

    private void showEventDetailsSheet(ArrayList<Event> events) {
        if (events == null || events.isEmpty()) {
            Log.w("CalendarFragment", "\n" +
                    "An attempt to show details for an empty list of events.");
            return;
        }
        EventBottomSheetDialogFragment bottomSheet = EventBottomSheetDialogFragment.newInstance(events);
        bottomSheet.show(getParentFragmentManager(), "EventDetailsBottomSheetTag");
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
        eventDetailsLauncher.launch(intent);
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

    private void fetchAccountInfo() {
        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(requireActivity().getApplicationContext()).create(AccountApiService.class);

        apiServiceAcc.getAccountInfo().enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("GetAccountInfo", "Successful: " + response.message());
                    currentUser = response.body();

                    fetchAllUserEvents(currentUser.getId());
                } else {
                    Log.e("GetAccountInfo", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                Log.e("GetAccountInfo", "Network error: " + t.getMessage());
            }
        });
    }

    public void fetchAllUserEvents(Long userId) {
        AccountApiService apiService = RetrofitClient.getRetrofit(requireActivity().getApplicationContext()).create(AccountApiService.class);

        apiService.getCalendar().enqueue(new Callback<List<UserActionDTO>>() {
            @Override
            public void onResponse(Call<List<UserActionDTO>> call, Response<List<UserActionDTO>> response) {
                if (response.isSuccessful()) {
                    userEvents = response.body();

                    for (UserActionDTO userAction : userEvents) {
                        fetchEventById(userAction.getEvent().getId());
                    }

                    updateCalendar();
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<UserActionDTO>> call, Throwable t) {
            }
        });
    }

    public void fetchEventById(Long eventId) {
        EventApiService apiService = RetrofitClient.getRetrofit(requireActivity().getApplicationContext()).create(EventApiService.class);

        apiService.findEventById(eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    userEventsItem.add(response.body());
                } else {
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
