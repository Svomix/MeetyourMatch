package com.javanostra.meetyourmatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);

        List<User> users = List.of(
                new User("Руслан Максудов", "Interests", "fish@lang.com", 19),
                new User("Максим Корчак", "Interests", "fish@lang.com", 19),
                new User("Дмитрий Чернышов", "Interests", "fish@lang.com", 19),
                new User("Кирилл Новосельцев", "Interests", "fish@lang.com", 19),
                new User("Максим Новосельцев", "Interests", "fish@lang.com", 19),
                new User("Антон Перепелкин", "Interests", "fish@lang.com", 19),
                new User("Вован Сараев", "Interests", "fish@lang.com", 19),
                new User("Александр Коваленко", "Interests", "fish@lang.com", 19),
                new User("Валерия Ткаченко", "Interests", "fish@lang.com", 19),
                new User("Рефат Решитов", "Interests", "fish@lang.com", 19),
                new User("Рамазан Шихларов", "Interests", "fish@lang.com", 19),
                new User("Рычка Игорь", "Interests", "fish@lang.com", 19),
                new User("Рабош Никита", "Interests", "fish@lang.com", 19)
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new ChatAdapter(getActivity().getApplicationContext(), users));

        return view;
    }
}
