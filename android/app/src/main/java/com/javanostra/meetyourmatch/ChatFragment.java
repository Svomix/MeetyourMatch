package com.javanostra.meetyourmatch;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private final User[] users = {
            new User("Ростислав Козлов", "Interests", "fish@lang.com", 19),
            new User("Егор Ситников", "Interests", "fish@lang.com", 19),
            new User("Тихон Щербаков", "Interests", "fish@lang.com", 19),
            new User("Сергей Лукин", "Interests", "fish@lang.com", 19),
            new User("Александр Пушкин", "Interests", "fish@lang.com", 19),
            new User("Антон Перепелкин", "Interests", "fish@lang.com", 19),
            new User("Артемий Бурах", "Interests", "fish@lang.com", 19),
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
