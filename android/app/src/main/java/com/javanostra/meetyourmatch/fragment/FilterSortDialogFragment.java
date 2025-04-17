package com.javanostra.meetyourmatch.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.Interest;
import com.javanostra.meetyourmatch.persistance.entity.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterSortDialogFragment extends DialogFragment {

    private static final String TAG = "FilterSortDialog";
    private static final String ARG_CURRENT_LIKED = "current_liked";
    private static final String ARG_CURRENT_CALENDAR = "current_calendar";
    private static final String ARG_CURRENT_SORT = "current_sort";
    private static final String ARG_CURRENT_TAG_IDS = "current_tag_ids";
    private static final String ARG_AVAILABLE_TAGS = "available_tags";

    public interface FilterSortListener {
        void onFilterSortApplied(boolean liked, boolean calendar, Set<Long> tagIds, EventSearchFragment.SortCriteria sortCriteria);
    }

    private FilterSortListener listener;

    private CheckBox checkLiked;
    private CheckBox checkCalendar;
    private Button btnSelectTags;
    private TextView textSelectedTags;
    private RadioGroup sortGroup;
    private RadioButton radioDefault;
    private RadioButton radioLikes;
    private RadioButton radioCalendar;
    private Button btnApply;
    private Button btnCancel;

    private boolean initialLiked;
    private boolean initialCalendar;
    private EventSearchFragment.SortCriteria initialSort;
    private Set<Long> initialTagIds;
    private ArrayList<Interest> availableTags = new ArrayList<>();

    private Set<Long> tempSelectedTagIds = new HashSet<>();

    public static FilterSortDialogFragment newInstance(
            boolean currentLiked,
            boolean currentCalendar,
            EventSearchFragment.SortCriteria currentSort,
            Set<Long> currentTagIds,
            List<Interest> availableTagsList,
            FilterSortListener listener) {

        FilterSortDialogFragment fragment = new FilterSortDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_CURRENT_LIKED, currentLiked);
        args.putBoolean(ARG_CURRENT_CALENDAR, currentCalendar);
        args.putSerializable(ARG_CURRENT_SORT, currentSort);
        args.putSerializable(ARG_CURRENT_TAG_IDS, new HashSet<>(currentTagIds));
        if (availableTagsList instanceof ArrayList) {
            args.putSerializable(ARG_AVAILABLE_TAGS, (ArrayList<Interest>)availableTagsList);
        } else {
            args.putSerializable(ARG_AVAILABLE_TAGS, new ArrayList<>(availableTagsList));
        }

        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(FilterSortListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialLiked = getArguments().getBoolean(ARG_CURRENT_LIKED);
            initialCalendar = getArguments().getBoolean(ARG_CURRENT_CALENDAR);
            initialSort = (EventSearchFragment.SortCriteria) getArguments().getSerializable(ARG_CURRENT_SORT);
            Serializable tagsSerializable = getArguments().getSerializable(ARG_CURRENT_TAG_IDS);
            if (tagsSerializable instanceof Set) {
                try {
                    initialTagIds = (HashSet<Long>) tagsSerializable;
                    tempSelectedTagIds.addAll(initialTagIds);
                } catch (ClassCastException e){
                    Log.e(TAG, "Failed to cast tag IDs set", e);
                    initialTagIds = new HashSet<>();
                    tempSelectedTagIds = new HashSet<>();
                }
            } else {
                initialTagIds = new HashSet<>();
                tempSelectedTagIds = new HashSet<>();
            }
            Serializable availableTagsSerializable = getArguments().getSerializable(ARG_AVAILABLE_TAGS);
            if (availableTagsSerializable instanceof ArrayList) {
                try {
                    availableTags = (ArrayList<Interest>) availableTagsSerializable;
                } catch (ClassCastException e){
                    Log.e(TAG, "Failed to cast available tags list", e);
                    availableTags = new ArrayList<>();
                }
            } else {
                availableTags = new ArrayList<>();
            }
        } else {
            initialLiked = false;
            initialCalendar = false;
            initialSort = EventSearchFragment.SortCriteria.DEFAULT;
            initialTagIds = new HashSet<>();
            tempSelectedTagIds = new HashSet<>();
            availableTags = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_filter_sort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkLiked = view.findViewById(R.id.dialog_checkbox_filter_liked);
        checkCalendar = view.findViewById(R.id.dialog_checkbox_filter_calendar);
        btnSelectTags = view.findViewById(R.id.dialog_button_select_tags);
        textSelectedTags = view.findViewById(R.id.dialog_text_selected_tags);
        sortGroup = view.findViewById(R.id.dialog_radiogroup_sort);
        radioDefault = view.findViewById(R.id.dialog_radio_sort_default);
        radioLikes = view.findViewById(R.id.dialog_radio_sort_likes);
        radioCalendar = view.findViewById(R.id.dialog_radio_sort_calendar);
        btnApply = view.findViewById(R.id.dialog_button_apply);
        btnCancel = view.findViewById(R.id.dialog_button_cancel);

        checkLiked.setChecked(initialLiked);
        checkCalendar.setChecked(initialCalendar);
        updateSelectedTagsTextView();

        switch (initialSort) {
            case LIKES: radioLikes.setChecked(true); break;
            case CALENDAR: radioCalendar.setChecked(true); break;
            case DEFAULT: default: radioDefault.setChecked(true); break;
        }

        btnSelectTags.setEnabled(!availableTags.isEmpty());
        btnSelectTags.setOnClickListener(v -> showTagSelectionDialog());

        btnApply.setOnClickListener(v -> applyChanges());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.90);
            // int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }


    private void showTagSelectionDialog() {
        if (getContext() == null || availableTags.isEmpty()) {
            Toast.makeText(getContext(), "Теги не загружены", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] tagNames = availableTags.stream().map(Interest::getName).toArray(String[]::new);
        boolean[] checkedItems = new boolean[availableTags.size()];
        for (int i = 0; i < availableTags.size(); i++) {
            checkedItems[i] = tempSelectedTagIds.contains(availableTags.get(i).getId());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppAlertDialogStyle);
        builder.setTitle("Выберите теги")
                .setMultiChoiceItems(tagNames, checkedItems, (dialog, which, isChecked) -> {
                    Long tagId = Long.valueOf(availableTags.get(which).getId());
                    if (isChecked) {
                        tempSelectedTagIds.add(tagId);
                    } else {
                        tempSelectedTagIds.remove(tagId);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    updateSelectedTagsTextView();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                });

        builder.create().show();
    }

    private void updateSelectedTagsTextView() {
        if (tempSelectedTagIds.isEmpty()) {
            textSelectedTags.setVisibility(View.GONE);
        } else {
            textSelectedTags.setVisibility(View.VISIBLE);
            String selectedNames = availableTags.stream()
                    .filter(tag -> tempSelectedTagIds.contains(tag.getId()))
                    .map(Interest::getName)
                    .filter(name -> !TextUtils.isEmpty(name))
                    .collect(Collectors.joining(", "));
            textSelectedTags.setText("Выбрано: " + selectedNames);
        }
    }


    private void applyChanges() {
        if (listener != null) {
            boolean liked = checkLiked.isChecked();
            boolean calendar = checkCalendar.isChecked();
            EventSearchFragment.SortCriteria sortCriteria = EventSearchFragment.SortCriteria.DEFAULT;
            int selectedSortId = sortGroup.getCheckedRadioButtonId();
            if (selectedSortId == R.id.dialog_radio_sort_likes) {
                sortCriteria = EventSearchFragment.SortCriteria.LIKES;
            } else if (selectedSortId == R.id.dialog_radio_sort_calendar) {
                sortCriteria = EventSearchFragment.SortCriteria.CALENDAR;
            }
            listener.onFilterSortApplied(liked, calendar, new HashSet<>(tempSelectedTagIds), sortCriteria);
        }
        dismiss();
    }
}