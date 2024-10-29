package com.javanostra.meetyourmatch;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InterestSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementAdapter adapter;
    private List<Element> elementList;
    private EditText searchEditText;

    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_selec);

        String[] hobbies = {"Чтение", "Рисование", "Фотография", "Плавание", "Теннис", "Шахматы", "Программирование", "Кулинария", "Путешествия", "Игра на гитаре", "Йога", "Коллекционирование марок", "Вышивание", "Велоспорт", "Танцы", "Садоводство", "Моделирование", "Бег", "Горные лыжи", "Бокс", "Актерское мастерство", "Игра на фортепиано", "Бильярд", "Рукоделие", "Аквариумистика", "Пение", "Лыжный спорт", "Рыбалка", "Скейтбординг", "Фитнес", "Фехтование", "Паркур", "Сноуборд", "Боевые искусства", "Настольные игры", "Игра на барабанах", "Катание на коньках", "Гольф", "Пилатес", "Медитация", "Оригами", "Скалолазание", "Пейнтбол", "Каякинг", "Картинг", "Роликовые коньки", "Гонки на велосипедах", "Косплей", "Квиллинг", "Флористика", "Кулинария", "Автомоделирование", "Гончарное дело", "Боди-арт", "Мотоспорт", "Спортивная стрельба", "Йога", "Миксология", "Зумба", "Зоология", "Астрономия", "История", "Энтомология", "Блоггинг", "Геймерство", "Робототехника", "Флэшмобы", "Бег на длинные дистанции", "Участие в марафонах", "Стрельба из лука", "Саморазвитие", "Шитье", "Волейбол", "Футбол", "Настольный теннис", "Ролики", "Коллекционирование монет", "Видеомонтаж", "Фотошоп", "Парусный спорт", "Путешествия по горам", "Хоккей", "Коллекционирование вин", "Триатлон", "Гребля", "Кроссфит", "Участие в театральных постановках", "Декупаж", "Эбру", "Создание ювелирных изделий", "Книгопечатание", "Резьба по дереву", "Мыловарение", "Плетение из бисера", "Серфинг", "Тяжелая атлетика", "Парапланеризм", "Фехтование на мечах", "Конный спорт", "Прыжки с парашютом", "Плавание на открытой воде", "Гравировка"};
        elementList = new ArrayList<>();
        for (int i = 0; i < 100; i++) elementList.add(new Element(hobbies[i], false));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ElementAdapter(elementList);
        recyclerView.setAdapter(adapter);

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.buttonClose3).setOnClickListener(v -> {
            finish();
        });

        buttonContinue = findViewById(R.id.buttonCompleteReg2);
        buttonContinue.setOnClickListener(v -> {
            Intent intent = new Intent(InterestSelectionActivity.this, MainScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}