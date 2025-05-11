package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // Используем AppCompatActivity для базовой функциональности
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.SelectedParticipantsAdapter;
import com.javanostra.meetyourmatch.fragment.ChatFragment; // Для LocalBroadcast
import com.javanostra.meetyourmatch.fragment.SearchUsersBottomSheetFragment;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager; // Для получения ID текущего пользователя
import com.javanostra.meetyourmatch.persistance.cookie.TokenHelper;   // Для извлечения ID
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.localbroadcastmanager.content.LocalBroadcastManager; // Для обновления списка чатов

public class CreateGroupActivity extends AppCompatActivity
        implements SearchUsersBottomSheetFragment.OnUserSelectedListener,
        SelectedParticipantsAdapter.OnParticipantRemoveListener {

    private static final String TAG = "CreateGroupActivity";

    private EditText editTextGroupName;
    private Button buttonAddParticipants;
    private RecyclerView recyclerViewSelectedParticipants;
    private TextView textViewSelectedParticipantsLabel;
    private Button buttonCreateGroup;
    private ProgressBar progressBarCreateGroup;
    private ImageButton backButton;
    // private ImageView groupAvatarImageView; // TODO: Позже для выбора аватара

    private SelectedParticipantsAdapter selectedParticipantsAdapter;
    private List<UserProfileDTO> selectedUsersList = new ArrayList<>();
    private ChatApiService chatApiService;
    private Long currentAppUserLongId = -1L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        currentAppUserLongId = TokenHelper.extractUserLongIdFromToken(new CookieManager(this).getCookie());
        if (currentAppUserLongId == -1L) {
            Toast.makeText(this, "Ошибка: не удалось определить пользователя.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Добавляем текущего пользователя в список участников по умолчанию
        // Для этого нам нужен UserProfileDTO текущего пользователя.
        // Это может потребовать дополнительного запроса или хранения данных пользователя локально.
        // Пока что пропустим этот шаг, пользователь должен будет добавить себя сам, если это нужно.
        // Либо, на сервере при создании группы, если memberIds не содержит ID создателя, он добавляется автоматически.
        // Уточните, как это должно работать. Пока что, создатель не будет в списке по умолчанию.

        initViews();
        setupRecyclerView();
        setupListeners();

        chatApiService = RetrofitClient.getRetrofit(this).create(ChatApiService.class);
        updateCreateButtonState();
    }

    private void initViews() {
        editTextGroupName = findViewById(R.id.edit_text_group_name);
        buttonAddParticipants = findViewById(R.id.button_add_participants);
        recyclerViewSelectedParticipants = findViewById(R.id.recycler_view_selected_participants);
        textViewSelectedParticipantsLabel = findViewById(R.id.text_view_selected_participants_label);
        buttonCreateGroup = findViewById(R.id.button_create_group);
        progressBarCreateGroup = findViewById(R.id.progress_bar_create_group);
        backButton = findViewById(R.id.create_group_back_button);
        // groupAvatarImageView = findViewById(R.id.group_avatar_image_view);
    }

    private void setupRecyclerView() {
        selectedParticipantsAdapter = new SelectedParticipantsAdapter(this);
        recyclerViewSelectedParticipants.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSelectedParticipants.setAdapter(selectedParticipantsAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        buttonAddParticipants.setOnClickListener(v -> {
            SearchUsersBottomSheetFragment bottomSheet = SearchUsersBottomSheetFragment.newInstance();
            // Передаем this в качестве listener'а. SearchUsersBottomSheetFragment должен быть адаптирован
            // для вызова OnUserSelectedListener, который реализует эта Activity.
            bottomSheet.setOnUserSelectedListener(this);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        buttonCreateGroup.setOnClickListener(v -> createGroup());

        editTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCreateButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Реализация OnUserSelectedListener из SearchUsersBottomSheetFragment
    @Override
    public void onUserSelectedForDetails(UserProfileDTO user) {
        // Этот метод из SearchUsersBottomSheetFragment вызывается, когда пользователь выбран.
        // Добавляем его в список, если его еще нет.
        if (user != null && user.getId() != null) {
            if (user.getId().equals(currentAppUserLongId)) {
                Toast.makeText(this, "Вы уже являетесь участником (создатель)", Toast.LENGTH_SHORT).show();
                return; // Не добавляем создателя, если он уже "неявно" участник
            }
            boolean alreadyExists = false;
            for (UserProfileDTO selectedUser : selectedUsersList) {
                if (selectedUser.getId().equals(user.getId())) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                selectedUsersList.add(user);
                selectedParticipantsAdapter.setParticipants(selectedUsersList);
                updateSelectedParticipantsView();
                updateCreateButtonState();
            } else {
                Toast.makeText(this, user.getUsername() + " уже добавлен.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Реализация OnParticipantRemoveListener из SelectedParticipantsAdapter
    @Override
    public void onParticipantRemoved(UserProfileDTO user) {
        selectedUsersList.remove(user);
        selectedParticipantsAdapter.setParticipants(selectedUsersList); // Обновляем адаптер
        updateSelectedParticipantsView();
        updateCreateButtonState();
    }

    private void updateSelectedParticipantsView() {
        if (selectedUsersList.isEmpty()) {
            recyclerViewSelectedParticipants.setVisibility(View.GONE);
            textViewSelectedParticipantsLabel.setVisibility(View.GONE);
        } else {
            recyclerViewSelectedParticipants.setVisibility(View.VISIBLE);
            textViewSelectedParticipantsLabel.setVisibility(View.VISIBLE);
        }
    }

    private void updateCreateButtonState() {
        boolean isGroupNameValid = !TextUtils.isEmpty(editTextGroupName.getText().toString().trim());
        // Группа должна иметь хотя бы одного участника (кроме создателя, если он добавляется автоматически на сервере)
        // или хотя бы N участников, если есть такое требование.
        // Пока что: название не пустое и хотя бы 1 выбранный участник (если создатель не добавляется автоматом)
        // Если создатель добавляется автоматически, то можно selectedUsersList.size() >= 0 (т.е. можно создать группу только с собой)
        boolean areParticipantsSelected = !selectedUsersList.isEmpty(); // Или selectedUsersList.size() >= N_MIN_PARTICIPANTS

        buttonCreateGroup.setEnabled(isGroupNameValid && areParticipantsSelected);
    }

    private void createGroup() {
        String groupName = editTextGroupName.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(this, "Введите название группы", Toast.LENGTH_SHORT).show();
            return;
        }

        // Собираем ID участников
        List<Long> memberIds = selectedUsersList.stream()
                .map(UserProfileDTO::getId)
                .collect(Collectors.toList());
        // Важно: Добавить ID текущего пользователя (создателя) в список участников,
        // если он не добавляется на сервере автоматически.
        // На данный момент, серверный эндпоинт /groupChatCreate принимает List<Long> memberIds.
        // Предположим, он ожидает, что ID создателя уже включен.
        if (!memberIds.contains(currentAppUserLongId)) {
            memberIds.add(0, currentAppUserLongId); // Добавляем создателя в начало списка
        }


        if (memberIds.size() < 2) { // Группа должна состоять хотя бы из 2 человек (создатель + 1)
            Toast.makeText(this, "Для создания группы нужно как минимум 2 участника (включая вас).", Toast.LENGTH_LONG).show();
            return;
        }


        Log.d(TAG, "Creating group: " + groupName + " with members: " + memberIds);
        setLoadingState(true);

        // String avatarPath = null; // TODO: Реализовать выбор аватара
        chatApiService.createGroupChat(memberIds, groupName, null /* avatarPath */).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    Long newGroupId = response.body();
                    Toast.makeText(CreateGroupActivity.this, "Группа \"" + groupName + "\" создана!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Group created successfully. ID: " + newGroupId);

                    // Оповещаем ChatFragment, что список чатов нужно обновить
                    // Можно передать данные о новой группе, чтобы ChatFragment добавил ее локально,
                    // или просто заставить его перезапросить список с сервера.
                    // Простой вариант - просто обновить.
                    Intent updateIntent = new Intent(ChatFragment.ACTION_UPDATE_CHAT_LIST_REQUEST); // Новый Action
                    LocalBroadcastManager.getInstance(CreateGroupActivity.this).sendBroadcast(updateIntent);


                    // Опционально: сразу открыть экран чата новой группы
                    Intent intent = new Intent(CreateGroupActivity.this, GroupChatMessagesActivity.class);
                    intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_ID, newGroupId);
                    intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_NAME, groupName);
                    // intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_AVATAR_URL, avatarPath); // Если был выбран
                    startActivity(intent);
                    finish(); // Закрываем активити создания группы
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to create group: " + response.code() + " - " + errorBody);
                        Toast.makeText(CreateGroupActivity.this, "Ошибка создания группы: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to create group and parse error body", e);
                        Toast.makeText(CreateGroupActivity.this, "Ошибка создания группы.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                setLoadingState(false);
                Log.e(TAG, "Network error creating group", t);
                Toast.makeText(CreateGroupActivity.this, "Сетевая ошибка при создании группы.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        progressBarCreateGroup.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonCreateGroup.setEnabled(!isLoading && !TextUtils.isEmpty(editTextGroupName.getText().toString().trim()) && !selectedUsersList.isEmpty());
        buttonAddParticipants.setEnabled(!isLoading);
        editTextGroupName.setEnabled(!isLoading);
    }
}