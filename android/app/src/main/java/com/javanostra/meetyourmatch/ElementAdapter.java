package com.javanostra.meetyourmatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder> {

    private List<Element> elementsList;
    private List<Element> filteredList;

    public ElementAdapter(List<Element> elements) {
        this.elementsList = elements;
        this.filteredList = new ArrayList<>(elements);
        sortElementsForDisplay();
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_element, parent, false);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        Element element = filteredList.get(position);
        holder.itemTextView.setText(element.getName());

        holder.itemCheckBox.setChecked(element.isSelected());

        holder.itemView.setOnClickListener(v -> {
            element.setSelected(!element.isSelected());
            holder.itemCheckBox.setChecked(element.isSelected());
            sortElementsForDisplay();
            notifyDataSetChanged();
        });

        holder.itemCheckBox.setOnClickListener(v -> {
            element.setSelected(holder.itemCheckBox.isChecked());
            sortElementsForDisplay();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(elementsList);
        } else {
            for (Element element : elementsList) {
                if (element.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(element);
                }
            }
        }
        sortElementsForDisplay();
        notifyDataSetChanged();
    }

    private void sortElementsForDisplay() {
        Collections.sort(filteredList, (e1, e2) -> Boolean.compare(e2.isSelected(), e1.isSelected()));
    }

    static class ElementViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        CheckBox itemCheckBox;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            itemCheckBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }
}