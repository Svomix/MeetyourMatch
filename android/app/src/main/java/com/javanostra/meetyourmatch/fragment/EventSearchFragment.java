package com.javanostra.meetyourmatch.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.EventDetailsActivity;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.PagedResponse;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.UserInterest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventSearchFragment extends Fragment
        implements FilterSortDialogFragment.FilterSortListener {

    private static final String TAG = "EventSearchFragment";
    private static final int ITEMS_PER_PAGE = 30;

    private GridLayout eventGrid;
    private ScrollView scrollViewEvents;
    private EditText searchBar;
    private ImageView filterButton;
    private ImageButton buttonPrevPage, buttonNextPage;
    private TextView textPageInfo;
    private ProgressBar progressBar;

    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredAndSortedEvents = new ArrayList<>();
    private List<UserInterest> availableTags = new ArrayList<>();
    private boolean tagsFetched = false;

    private String currentSearchQuery = "";
    private boolean isLikedFilterActive = false;
    private boolean isInCalendarFilterActive = false;
    private Set<Long> selectedTagIds = new HashSet<>();
    private SortCriteria currentSortCriteria = SortCriteria.DEFAULT;
    private FilterSortDialogFragment.FilterSortListener filterSortListener;

    private int currentPage = 1;
    private int totalPages = 1;

    private int itemSize;

    enum SortCriteria {
        DEFAULT, LIKES, CALENDAR
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterSortListener = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_search, container, false);

        eventGrid = view.findViewById(R.id.event_grid);
        searchBar = view.findViewById(R.id.search_bar);
        filterButton = view.findViewById(R.id.filter_button);
        buttonPrevPage = view.findViewById(R.id.button_prev_page);
        buttonNextPage = view.findViewById(R.id.button_next_page);
        textPageInfo = view.findViewById(R.id.text_page_info);
        scrollViewEvents = view.findViewById(R.id.scrollViewEvents);
        

        setupListeners();
        calculateItemSize();

        fetchAllEvents();
        fetchAvailableTags();

        return view;
    }

    private void setupListeners() {
        filterButton.setOnClickListener(v -> showCustomFilterDialog());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPageData();
            }
        });

        buttonNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadPageData();
            }
        });

        textPageInfo.setOnClickListener(v -> openPageInputDialog());
    }

    private void showCustomFilterDialog() {
        if (!tagsFetched && getContext() != null) {
            Toast.makeText(getContext(), "Загрузка списка тегов...", Toast.LENGTH_SHORT).show();
            fetchAvailableTags();
            
        }

        FilterSortDialogFragment dialogFragment = FilterSortDialogFragment.newInstance(
                isLikedFilterActive,
                isInCalendarFilterActive,
                currentSortCriteria,
                selectedTagIds,
                availableTags,
                filterSortListener
        );

        dialogFragment.show(getParentFragmentManager(), "FilterSortDialog");
    }

    private void calculateItemSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int spacing = dpToPx(32);
            int margins = dpToPx(16);
            itemSize = (screenWidth - spacing - margins) / 2;
            Log.d(TAG, "Calculated item size: " + itemSize);
        } else {
            itemSize = dpToPx(150);
            Log.w(TAG, "Activity is null during item size calculation");
        }
        eventGrid.setColumnCount(2);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void fetchAllEvents() {
        
        if (getContext() == null) return;

        EventApiService apiService = RetrofitClient.getRetrofit(getContext()).create(EventApiService.class);

        apiService.findAllEvents(1, Integer.MAX_VALUE, "")
                .enqueue(new Callback<PagedResponse<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<PagedResponse<Event>> call, @NonNull Response<PagedResponse<Event>> response) {
                        if (!isAdded() || getContext() == null || getView() == null) {
                            Log.w(TAG, "fetchAllEvents onResponse: Fragment not attached or view destroyed, ignoring response.");
                            return;
                        }

                        
                        if (response.isSuccessful() && response.body() != null) {
                            allEvents = response.body().getContent();
                            Log.d(TAG, "Fetched " + allEvents.size() + " events successfully.");
                            applyFiltersAndSort();
                        } else {
                            Log.e(TAG, "Failed to fetch events: " + response.code());
                            Toast.makeText(getContext(), "Ошибка загрузки событий: " + response.code(), Toast.LENGTH_SHORT).show();
                            allEvents.clear();
                            applyFiltersAndSort();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PagedResponse<Event>> call, @NonNull Throwable t) {
                        if (!isAdded() || getContext() == null || getView() == null) {
                            Log.w(TAG, "fetchAllEvents onFailure: Fragment not attached or view destroyed, ignoring failure.");
                            return;
                        }

                        
                        Log.e(TAG, "Failure fetching events", t);
                        Toast.makeText(getContext(), "Ошибка сети при загрузке событий", Toast.LENGTH_SHORT).show();
                        allEvents.clear();
                        applyFiltersAndSort();
                    }
                });
    }

    private void fetchAvailableTags() {
        if (getContext() == null || tagsFetched) return;

        AccountApiService apiServiceAcc = RetrofitClient.getRetrofit(getContext()).create(AccountApiService.class);
        apiServiceAcc.getMyInterests().enqueue(new Callback<Set<UserInterest>>() {
            @Override
            public void onResponse(Call<Set<UserInterest>> call, Response<Set<UserInterest>> response) {
                if (!isAdded() || getContext() == null || getView() == null) {
                    Log.w(TAG, "fetchAvailableTags onResponse: Fragment not attached or view destroyed.");
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    availableTags = new ArrayList<UserInterest>(response.body());
                    tagsFetched = true;
                    Log.d(TAG, "Fetched " + availableTags.size() + " tags.");
                } else {
                    Log.e(TAG, "Failed to fetch tags: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Set<UserInterest>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null || getView() == null) {
                    Log.w(TAG, "fetchAvailableTags onFailure: Fragment not attached or view destroyed.");
                    return;
                }

                Log.e(TAG, "Failure fetching tags", t);
            }
        });
    }

    @Override
    public void onFilterSortApplied(boolean liked, boolean calendar, Set<Long> tagIds, SortCriteria sortCriteria) {
        Log.d(TAG, "Filters applied from dialog: Liked=" + liked + ", Calendar=" + calendar + ", Tags=" + tagIds.size() + ", Sort=" + sortCriteria);
        this.isLikedFilterActive = liked;
        this.isInCalendarFilterActive = calendar;
        this.selectedTagIds = tagIds;
        this.currentSortCriteria = sortCriteria;

        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        List<Event> currentlyFiltered = new ArrayList<>();

        for (Event event : allEvents) {
            if (matchesFilters(event)) {
                currentlyFiltered.add(event);
            }
        }

        sortEvents(currentlyFiltered);

        filteredAndSortedEvents = currentlyFiltered;
        Log.d(TAG, "Applied filters/sort. Result size: " + filteredAndSortedEvents.size());

        currentPage = 1;
        updatePagination();
        loadPageData();
    }

    private boolean matchesFilters(Event event) {
        if (!currentSearchQuery.isEmpty()) {
            boolean titleMatch = event.getTitle() != null && event.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
            boolean descMatch = event.getDescription() != null && event.getDescription().toLowerCase().contains(currentSearchQuery.toLowerCase());
            if (!titleMatch && !descMatch) {
                return false;
            }
        }

        if (isLikedFilterActive) {
            if (event.getUserAction() == null || event.getUserAction().getLiked() == null || !event.getUserAction().getLiked()) {
                return false;
            }
        }

        if (isInCalendarFilterActive) {
            if (event.getUserAction() == null || event.getUserAction().getInCalendar() == null || !event.getUserAction().getInCalendar()) {
                return false;
            }
        }

        if (!selectedTagIds.isEmpty()) {













        }

        return true;
    }

    private void sortEvents(List<Event> eventsToSort) {
        Collections.sort(eventsToSort, (e1, e2) -> {
            int comparisonResult = 0;
            switch (currentSortCriteria) {
                case LIKES:
                    Long likes1 = (e1.getUserActionCounters() != null && e1.getUserActionCounters().getLikedCounter() != null) ? e1.getUserActionCounters().getLikedCounter() : 0L;
                    Long likes2 = (e2.getUserActionCounters() != null && e2.getUserActionCounters().getLikedCounter() != null) ? e2.getUserActionCounters().getLikedCounter() : 0L;
                    comparisonResult = Long.compare(likes2, likes1);
                    break;
                case CALENDAR:
                    Long calendar1 = (e1.getUserActionCounters() != null && e1.getUserActionCounters().getCalendarCounter() != null) ? e1.getUserActionCounters().getCalendarCounter() : 0L;
                    Long calendar2 = (e2.getUserActionCounters() != null && e2.getUserActionCounters().getCalendarCounter() != null) ? e2.getUserActionCounters().getCalendarCounter() : 0L;
                    comparisonResult = Long.compare(calendar2, calendar1);
                    break;
                case DEFAULT:
                default:
                    if (e1.getDate() != null && e2.getDate() != null) {
                        comparisonResult = e1.getDate().compareTo(e2.getDate());
                    } else if (e1.getDate() != null) {
                        comparisonResult = 1;
                    } else if (e2.getDate() != null) {
                        comparisonResult = -1;
                    }
                    if (comparisonResult == 0) {
                        String title1 = e1.getTitle() != null ? e1.getTitle() : "";
                        String title2 = e2.getTitle() != null ? e2.getTitle() : "";
                        comparisonResult = title1.compareToIgnoreCase(title2);
                    }
                    break;
            }
            return comparisonResult;
        });
    }

    private void updatePagination() {
        int itemCount = filteredAndSortedEvents.size();
        totalPages = (int) Math.ceil((double) itemCount / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        textPageInfo.setText(getString(R.string.page_info_format, currentPage, totalPages));
        buttonPrevPage.setEnabled(currentPage > 1);
        buttonNextPage.setEnabled(currentPage < totalPages);

        Log.d(TAG, "Pagination updated: CurrentPage=" + currentPage + ", TotalPages=" + totalPages);

    }

    private void loadPageData() {
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredAndSortedEvents.size());

        if (startIndex < 0 || startIndex > filteredAndSortedEvents.size()) {
            startIndex = 0;
            Log.w(TAG, "Invalid start index calculated, resetting to 0.");
        }
        if (endIndex < startIndex) {
            endIndex = startIndex;
            Log.w(TAG, "Invalid end index calculated, setting to start index.");
        }

        List<Event> currentPageEvents = new ArrayList<>();
        if (startIndex < endIndex) {
            currentPageEvents = filteredAndSortedEvents.subList(startIndex, endIndex);
        }

        Log.d(TAG, "Loading page " + currentPage + ". Items: " + startIndex + " to " + (endIndex - 1) + ". Count: " + currentPageEvents.size());


        populateGrid(currentPageEvents);
        updatePagination();
        scrollViewEvents.smoothScrollTo(0, 0);
    }

    private void populateGrid(List<Event> eventsToShow) {
        if (getContext() == null) return;

        eventGrid.removeAllViews();

        if (eventsToShow.isEmpty()) {
            Log.d(TAG, "No events to display on this page.");
            return;
        }

        for (Event event : eventsToShow) {
            View eventItem = LayoutInflater.from(getContext()).inflate(R.layout.event_item, eventGrid, false);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = itemSize;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            
            eventItem.setLayoutParams(params);

            CardView eventCardImage = eventItem.findViewById(R.id.cardView);
            ImageView eventImage = eventItem.findViewById(R.id.event_image);
            TextView eventTitle = eventItem.findViewById(R.id.event_title);
            TextView eventTagsText = eventItem.findViewById(R.id.event_tags);

            ViewGroup.LayoutParams cardParams = eventCardImage.getLayoutParams();
            cardParams.width = itemSize;
            cardParams.height = itemSize;
            eventCardImage.setLayoutParams(cardParams);


            Glide.with(this)
                    .load(event.getCoverImgUrl())
                    .placeholder(R.drawable.ic_mym_128_round)
                    .error(R.drawable.ic_mym_128_round)
                    .centerCrop()
                    .into(eventImage);

            eventTitle.setText(event.getTitle());

            eventTagsText.setText("");












            eventItem.setOnClickListener(v -> openEventDetails(event));
            eventGrid.addView(eventItem);
        }

        eventGrid.requestLayout();
    }

    private void openPageInputDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppAlertDialogStyle);
        builder.setTitle("Перейти к странице");
        builder.show();
    }


    private void openEventDetails(Event event) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }

}