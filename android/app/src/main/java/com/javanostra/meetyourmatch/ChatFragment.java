package com.javanostra.meetyourmatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private final User[] users = {
            new User("Руслан Максудов", "Interests", "fish@lang.com", 19),
            new User("Максим Корчак", "Interests", "fish@lang.com", 19),
            new User("Дмитрий Чернышов", "Interests", "fish@lang.com", 19),
            new User("Кирилл Новосельцев", "Interests", "fish@lang.com", 19),
            new User("Александр Ананасенко", "Interests", "fish@lang.com", 19),
            new User("Антон Перепелкин", "Interests", "fish@lang.com", 19),
            new User("Вован Сараев", "Interests", "fish@lang.com", 19),
            new User("Вова Вист", "Interests", "fish@lang.com", 19),
            new User("Уолтер Уайт", "Interests", "fish@lang.com", 19),
            new User("Гектор Саламандра", "Interests", "fish@lang.com", 19),
            new User("Густаво Фринг", "Interests", "fish@lang.com", 19),
            new User("Глеб Самойлов", "Interests", "fish@lang.com", 19),
            new User("Федор Меркурий", "Interests", "fish@lang.com", 19)
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.chatContainer);

        for (int i = 0; i < users.length; ++i) {
            LinearLayout chatBox = (LinearLayout) inflater.inflate(R.layout.chat_box, container, false);
            TextView username = (TextView) chatBox.findViewById(R.id.Username);
            username.setText(users[i].getUsername());
            linearLayout.addView(chatBox);
        }

        return view;
    }
}
