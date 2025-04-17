package com.javanostra.meetyourmatch.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.javanostra.meetyourmatch.R;
import android.content.Intent;
import android.widget.TextView;

import com.javanostra.meetyourmatch.activity.EventDetailsActivity;
import com.javanostra.meetyourmatch.adapter.EventDetailsAdapter;
import com.javanostra.meetyourmatch.persistance.entity.Event;
import com.javanostra.meetyourmatch.persistance.entity.Location;

import java.util.ArrayList;

public class EventBottomSheetDialogFragment extends BottomSheetDialogFragment
        implements EventDetailsAdapter.OnEventClickListener {

    private static final String ARG_EVENTS = "events_list";
    private RecyclerView recyclerView;
    private EventDetailsAdapter adapter;
    private ArrayList<Event> eventsToShow;

    private TextView locationTitleTextView;
    private TextView locationAddressTextView;

    public static EventBottomSheetDialogFragment newInstance(ArrayList<Event> events) {
        EventBottomSheetDialogFragment fragment = new EventBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_EVENTS, events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
        if (getArguments() != null) {
            eventsToShow = getArguments().getParcelableArrayList(ARG_EVENTS);
        }
        if (eventsToShow == null) {
            eventsToShow = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewEventDetails);
        locationTitleTextView = view.findViewById(R.id.textViewLocationTitle);
        locationAddressTextView = view.findViewById(R.id.textViewLocationAddress);

        setupRecyclerView();
        updateLocationHeader();

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Dialog dialog = getDialog();
            if (dialog instanceof BottomSheetDialog) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                FrameLayout bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    // behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                    // behavior.setHideable(true);
                    // behavior.setSkipCollapsed(true);
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new EventDetailsAdapter();
        adapter.setOnEventClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setEvents(eventsToShow);
        Log.d("BottomSheet", "Setting up RecyclerView with " + eventsToShow.size() + " events.");
    }

    private void updateLocationHeader() {
        if (eventsToShow == null || eventsToShow.isEmpty()) {
            locationTitleTextView.setText(R.string.no_events_at_location);
            locationAddressTextView.setVisibility(View.GONE);
            return;
        }

        Event firstEvent = eventsToShow.get(0);
        Location location = firstEvent.getLocation();

        if (location != null) {
            locationTitleTextView.setText(location.getTitle() != null ? location.getTitle() : getString(R.string.unknown_location));
            locationAddressTextView.setText(location.getAddress() != null ? location.getAddress() : "");
            locationAddressTextView.setVisibility(location.getAddress() != null ? View.VISIBLE : View.GONE);
        } else {
            locationTitleTextView.setText(R.string.location_data_missing);
            locationAddressTextView.setVisibility(View.GONE);
            Log.w("BottomSheet", "Локация не найдена для события: " + firstEvent.getTitle());
        }
    }

    @Override
    public void onEventClick(Event event) {
        Log.d("BottomSheet", "Event clicked: " + event.getTitle());
        openEventDetails(event);
        dismiss();
    }

    private void openEventDetails(Event event) {
        if (getContext() == null) {
            Log.e("BottomSheet", "Контекст null, не могу открыть EventDetailsActivity");
            return;
        }
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("BottomSheet", "Не удалось запустить EventDetailsActivity", e);
        }
    }
}