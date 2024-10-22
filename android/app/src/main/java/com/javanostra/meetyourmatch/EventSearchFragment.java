package com.javanostra.meetyourmatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class EventSearchFragment extends Fragment {

    private GridLayout eventGrid;
    private EditText searchBar;
    private ImageView filterButton;
    private List<Event> events;

    private int itemSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_search, container, false);

        eventGrid = view.findViewById(R.id.event_grid);
        searchBar = view.findViewById(R.id.search_bar);
        filterButton = view.findViewById(R.id.filter_button);

        filterButton.setOnClickListener(v -> openFilterDialog());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        events = new ArrayList<>();
        events.add(new Event("Event 1", "Tags 1", 8, 7, 2024));
        events.add(new Event("Event 2", "Tags 2", 12, 8, 2024));
        events.add(new Event("Event 3", "Tags 3", 1, 9, 2024));
        events.add(new Event("Event 4", "Tags 4", 19, 9, 2024));
        events.add(new Event("Event 5", "Tags 5", 26, 8, 2024));
        events.add(new Event("Event 6", "Tags 6", 21, 7, 2024));
        events.add(new Event("Event 7", "Tags 7", 15, 6, 2024));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        itemSize = screenWidth / 2 - 84-20;

        populateGrid();

        return view;
    }

    private void populateGrid() {
        eventGrid.removeAllViews();
        eventGrid.setRowCount((int) Math.ceil(events.size() / 2.0));

        for (int i = 0; i < events.size(); i++) {
            View eventItem = LayoutInflater.from(getContext()).inflate(R.layout.event_item, eventGrid, false);
            setItemParams(eventItem, i);
            eventGrid.addView(eventItem);
        }
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void filterEvents(String query) {
        eventGrid.removeAllViews();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getTitle().toLowerCase().contains(query.toLowerCase())) {
                View eventItem = LayoutInflater.from(getContext()).inflate(R.layout.event_item, eventGrid, false);
                setItemParams(eventItem, i);
                eventGrid.addView(eventItem);
            }
        }
    }

    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        TextView titleView = dialogView.findViewById(R.id.dialog_title);
        titleView.setText("Фильтры");

        LinearLayout container = dialogView.findViewById(R.id.events_container);

        String[] filters = {"Понравившиеся", "Добавленные в календарь", "Теги"};
        boolean[] checkedFilters = {false, false, false};

        for (int i = 0; i < filters.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(filters[i]);
            checkBox.setChecked(checkedFilters[i]);

            int finalI = i;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkedFilters[finalI] = isChecked;
            });

            container.addView(checkBox);
        }

        builder.setView(dialogView);
        builder.setNeutralButton("Выбрать теги", (dialog, which) -> openTagSelectionDialog());
        builder.setPositiveButton("Применить", (dialog, which) -> applyFilters(checkedFilters));
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openTagSelectionDialog() {
        String[] tags = {"Музыка", "Искусство", "Спорт", "Театр"};
        boolean[] checkedTags = {false, false, false, false};

        AlertDialog.Builder tagBuilder = new AlertDialog.Builder(getContext());
        tagBuilder.setTitle("Выберите теги");

        tagBuilder.setMultiChoiceItems(tags, checkedTags, (dialog, which, isChecked) -> {
            checkedTags[which] = isChecked;
        });

        tagBuilder.setPositiveButton("Применить", (dialog, which) -> {

        });

        tagBuilder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        tagBuilder.create().show();
    }

    private void applyFilters(boolean[] filters) {
        if (filters[0]) {
            // понравившиеся
        }
        if (filters[1]) {
            // добавленные в календарь
        }
        if (filters[2]) {
            // открыть окно для выбора тегов
        }
    }

    private void setItemParams(View eventItem, int i) {
        CardView eventCardImage = eventItem.findViewById(R.id.cardView);
        ImageView eventImage = eventItem.findViewById(R.id.event_image);
        TextView eventTitle = eventItem.findViewById(R.id.event_title);
        TextView eventTags = eventItem.findViewById(R.id.event_tags);

        eventImage.setImageResource(R.drawable.ic_mym_128);
        eventTitle.setText(events.get(i).getTitle());
        eventTags.setText(events.get(i).getDescription());

        eventCardImage.getLayoutParams().width = itemSize;
        eventCardImage.getLayoutParams().height = itemSize;

        int finalI = i;
        eventItem.setOnClickListener(v -> openEventDetails(events.get(finalI)));
    }
}