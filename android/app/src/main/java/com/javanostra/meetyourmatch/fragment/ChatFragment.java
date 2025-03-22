package com.javanostra.meetyourmatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.ChatMessagesActivity;
import com.javanostra.meetyourmatch.adapter.ChatAdapter;
import com.javanostra.meetyourmatch.persistance.entity.User;

import java.util.List;

public class ChatFragment extends Fragment implements ChatRecyclerViewInterface {


    public static final List<User> users = List.of(
            new User("Руслан Максудов", "fish@lang.com", "Password"),
            new User("Максим Корчак", "fish@lang.com", "Password"),
            new User("Дмитрий Чернышов", "fish@lang.com", "Password"),
            new User("Кирилл Новосельцев", "fish@lang.com", "Password"),
            new User("Максим Новосельцев", "fish@lang.com", "Password"),
            new User("Антон Перепелкин", "fish@lang.com", "Password"),
            new User("Вован Сараев", "fish@lang.com", "Password"),
            new User("Александр Коваленко", "fish@lang.com", "Password"),
            new User("Валерия Ткаченко", "fish@lang.com", "Password"),
            new User("Рефат Решитов", "fish@lang.com", "Password"),
            new User("Рамазан Шихларов", "fish@lang.com", "Password"),
            new User("Рычка Игорь", "fish@lang.com", "Password"),
            new User("Рабош Никита", "fish@lang.com", "Password")
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new ChatAdapter(getActivity().getApplicationContext(), users, this));

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ChatMessagesActivity.class);
        intent.putExtra("Username", users.get(position).getUsername());
        intent.putExtra("IDSender", users.get(position).getId());
        startActivity(intent);
    }
}
