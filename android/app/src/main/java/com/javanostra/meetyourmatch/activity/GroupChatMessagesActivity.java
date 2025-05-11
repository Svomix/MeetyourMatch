package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.DateHeaderItem;
import com.javanostra.meetyourmatch.adapter.DateUtils;
import com.javanostra.meetyourmatch.adapter.GroupMessageAdapter;
import com.javanostra.meetyourmatch.fragment.ChatFragment;
import com.javanostra.meetyourmatch.persistance.ChatWebSocketManager;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.TokenHelper;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessage;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessageNotification;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessageRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatMessagesActivity extends AppCompatActivity {

    private static final String TAG = "GroupChatMessagesAct";

    public static final String EXTRA_GROUP_ID = "GROUP_ID";
    public static final String EXTRA_GROUP_NAME = "GROUP_NAME";
    public static final String EXTRA_GROUP_AVATAR_URL = "GROUP_AVATAR_URL";

    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private TextView statusTextView;
    private Toolbar toolbar;
    private ImageButton backButton;
    private ImageView groupAvatarImageView;
    private TextView groupNameTextView;
    private LinearLayout groupProfileClickableArea;


    private GroupMessageAdapter groupMessageAdapter;
    private List<GroupChatMessage> rawMessageList = new ArrayList<>();
    private List<Object> displayList = new ArrayList<>();

    private ChatWebSocketManager webSocketManager;
    private ChatApiService chatApiService;

    private Long currentGroupId = -1L;
    private String currentGroupName;
    private String currentGroupAvatarUrl;
    private Long currentAppUserLongId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_messages);

        String token = new CookieManager(this).getCookie();
        if (token != null) {
            currentAppUserLongId = TokenHelper.extractUserLongIdFromToken(token);
        }
        if (currentAppUserLongId == -1L) {
            Log.e(TAG, "Failed to get current user Long ID. Cannot proceed.");
            Toast.makeText(this, "Error: User not identified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!parseIntentExtras()) {
            finish();
            return;
        }

        initViews();
        setupCustomToolbar();
        setupRecyclerView();

        chatApiService = RetrofitClient.getRetrofit(this).create(ChatApiService.class);
        webSocketManager = ChatWebSocketManager.getInstance();

        sendButton.setOnClickListener(v -> attemptSendMessage());
        backButton.setOnClickListener(v -> onBackPressed());
        // groupProfileClickableArea.setOnClickListener(v -> openGroupDetails()); // TODO: Позже - экран деталей группы

        observeWebSocketEvents();
        fetchMessageHistory();
    }

    private boolean parseIntentExtras() {
        currentGroupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1L);
        currentGroupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        currentGroupAvatarUrl = getIntent().getStringExtra(EXTRA_GROUP_AVATAR_URL);

        if (currentGroupId == -1L || currentGroupName == null) {
            Log.e(TAG, "Required group data (ID, Name) not passed in Intent extras.");
            Toast.makeText(this, R.string.error_loading_chat_info, Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d(TAG, "Group chat opened. GroupID: " + currentGroupId + ", GroupName: " + currentGroupName + ", CurrentUserLongID: " + currentAppUserLongId);
        return true;
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_group_chat);
        messagesRecyclerView = findViewById(R.id.group_messages_recycler_view);
        messageEditText = findViewById(R.id.group_message_edit_text);
        sendButton = findViewById(R.id.group_send_button);
        progressBar = findViewById(R.id.group_chat_progressBar);
        statusTextView = findViewById(R.id.group_chat_status_text_view);

        backButton = findViewById(R.id.group_chat_back_button);
        groupAvatarImageView = findViewById(R.id.group_chat_avatar);
        groupNameTextView = findViewById(R.id.group_chat_name_text);
        groupProfileClickableArea = findViewById(R.id.group_profile_clickable_area);
    }

    private void setupCustomToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        groupNameTextView.setText(currentGroupName);
        Glide.with(this)
                .load(currentGroupAvatarUrl)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .transform(new CircleCrop())
                .into(groupAvatarImageView);
    }

    private void setupRecyclerView() {
        groupMessageAdapter = new GroupMessageAdapter(currentAppUserLongId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(groupMessageAdapter);
    }

    private void observeWebSocketEvents() {
        webSocketManager.connectionState.observe(this, state -> {
            if (!isFinishing()) {
                Log.d(TAG, "WebSocket Connection State Changed: " + state);
                updateStatusUI(state);
            }
        });

        webSocketManager.newGroupMessage.observe(this, notification -> {
            if (notification != null && !isFinishing()) {
                if (Objects.equals(notification.getGroupChatId(), currentGroupId)) {
                    Log.d(TAG, "New group message observed via WebSocket: " + notification.getContent());
                    GroupChatMessage chatMessage = notification.toGroupChatMessage();

                    boolean exists = false;
                    for(Object item : displayList) {
                        if (item instanceof GroupChatMessage) {
                            if (Objects.equals(((GroupChatMessage) item).getId(), chatMessage.getId())) {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (!exists) {
                        rawMessageList.add(chatMessage);
                        Collections.sort(rawMessageList, (m1, m2) -> Long.compare(m1.getTimestamp().getTime(), m2.getTimestamp().getTime()));
                        processMessagesWithDateHeaders(new ArrayList<>(rawMessageList));
                        updateAdapterDataAndScroll();

                        Intent intent = new Intent(ChatFragment.ACTION_UPDATE_GROUP_CHAT_ITEM);
                        intent.putExtra(ChatFragment.EXTRA_GROUP_ID_FOR_UPDATE, currentGroupId);
                        intent.putExtra(ChatFragment.EXTRA_LAST_MESSAGE, chatMessage.getContent());
                        if (chatMessage.getTimestamp() != null) {
                            intent.putExtra(ChatFragment.EXTRA_LAST_MESSAGE_TIME, chatMessage.getTimestamp().getTime());
                        }
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    } else {
                        Log.w(TAG, "Duplicate group message detected by ID, ignoring: " + chatMessage.getContent());
                    }
                } else {
                    Log.w(TAG, "Received group message for a different group ("+notification.getGroupChatId()+"). Ignoring.");
                }
            }
        });
    }

    private void updateAdapterDataAndScroll() {
        List<Object> newListForAdapter = new ArrayList<>(displayList);
        groupMessageAdapter.submitList(newListForAdapter, this::scrollToBottomIfNeeded);
    }


    private void scrollToBottomIfNeeded() {
        if (groupMessageAdapter.getItemCount() > 0) {
            messagesRecyclerView.post(() -> messagesRecyclerView.smoothScrollToPosition(groupMessageAdapter.getItemCount() - 1));
        }
    }


    private void updateStatusUI(ChatWebSocketManager.ConnectionState state) {
        if (isFinishing()) return;
        switch (state) {
            case CONNECTING: setLoadingState(true, getString(R.string.status_connecting)); sendButton.setEnabled(false); messageEditText.setEnabled(false); break;
            case CONNECTED: setLoadingState(false, ""); sendButton.setEnabled(true); messageEditText.setEnabled(true); break;
            case DISCONNECTED: setLoadingState(false, getString(R.string.status_disconnected)); sendButton.setEnabled(false); messageEditText.setEnabled(false); break;
            case ERROR: setLoadingState(false, getString(R.string.status_connection_error)); sendButton.setEnabled(false); messageEditText.setEnabled(false); break;
            case INITIAL: setLoadingState(true, getString(R.string.status_initializing)); sendButton.setEnabled(false); messageEditText.setEnabled(false); break;
        }
    }

    private void fetchMessageHistory() {
        setLoadingState(true, getString(R.string.status_loading_history));
        Log.d(TAG, "Fetching history for Group ID: " + currentGroupId);

        if (chatApiService == null) {
            Log.e(TAG, "ChatApiService is null.");
            setLoadingState(false, getString(R.string.error_generic));
            return;
        }

        chatApiService.getGroupChatHistory(currentGroupId).enqueue(new Callback<List<GroupChatMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupChatMessage>> call, @NonNull Response<List<GroupChatMessage>> response) {
                if (isFinishing()) return;
                setLoadingState(false, "");

                if (response.isSuccessful() && response.body() != null) {
                    rawMessageList = response.body();
                    Collections.sort(rawMessageList, (m1, m2) -> Long.compare(m1.getTimestamp().getTime(), m2.getTimestamp().getTime()));

                    Log.d(TAG, "Group history received: " + rawMessageList.size() + " messages.");
                    processMessagesWithDateHeaders(new ArrayList<>(rawMessageList));
                    updateAdapterDataAndScroll();
                    connectAndSubscribeWebSocket();
                } else {
                    Log.e(TAG, "Failed to fetch group history: " + response.code() + " - " + response.message());
                    Toast.makeText(GroupChatMessagesActivity.this, R.string.error_loading_history, Toast.LENGTH_SHORT).show();
                    statusTextView.setText(R.string.error_loading_history);
                    statusTextView.setVisibility(View.VISIBLE);
                    rawMessageList.clear();
                    processMessagesWithDateHeaders(new ArrayList<>(rawMessageList));
                    updateAdapterDataAndScroll();
                    connectAndSubscribeWebSocket();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupChatMessage>> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                setLoadingState(false, getString(R.string.error_network_history));
                Log.e(TAG, "Error fetching group history", t);
                Toast.makeText(GroupChatMessagesActivity.this, R.string.error_network_history, Toast.LENGTH_SHORT).show();
                rawMessageList.clear();
                processMessagesWithDateHeaders(new ArrayList<>(rawMessageList));
                updateAdapterDataAndScroll();
                connectAndSubscribeWebSocket();
            }
        });
    }

    private void processMessagesWithDateHeaders(List<GroupChatMessage> messages) {
        displayList.clear();
        GroupChatMessage previousMessage = null;
        for (GroupChatMessage currentMessage : messages) {
            if (currentMessage == null || currentMessage.getTimestamp() == null || currentMessage.getTimestamp().getTime() <= 0) continue;

            if (DateUtils.shouldAddDateHeader(previousMessage != null ? previousMessage.getTimestamp() : null, currentMessage.getTimestamp())) {
                displayList.add(new DateHeaderItem(currentMessage.getTimestamp().getTime()));
            }
            displayList.add(currentMessage);
            previousMessage = currentMessage;
        }
        Log.d(TAG, "Processed group message list size (with headers): " + displayList.size());
    }

    private void connectAndSubscribeWebSocket() {
        if (currentAppUserLongId == -1L || currentGroupId == -1L) {
            Log.e(TAG, "Cannot connect WebSocket: user or group ID is invalid!");
            updateStatusUI(ChatWebSocketManager.ConnectionState.ERROR);
            return;
        }

        String currentAppUsername = TokenHelper.extractUsernameFromToken(new CookieManager(this).getCookie());
        if (currentAppUsername == null) {
            Log.e(TAG, "Cannot connect WebSocket: current username is null!");
            updateStatusUI(ChatWebSocketManager.ConnectionState.ERROR);
            return;
        }


        if (webSocketManager != null) {
            ChatWebSocketManager.ConnectionState currentState = webSocketManager.connectionState.getValue();
            if (currentState != ChatWebSocketManager.ConnectionState.CONNECTED &&
                    currentState != ChatWebSocketManager.ConnectionState.CONNECTING) {
                Log.d(TAG, "Attempting to connect WebSocket general session...");
                webSocketManager.connect(currentAppUsername, null);
            } else {
                Log.d(TAG, "WebSocket general session already connected or connecting.");
            }
            webSocketManager.subscribeToGroupTopic(currentGroupId);
        } else {
            Log.e(TAG, "WebSocketManager is null.");
        }
    }

    private void attemptSendMessage() {
        String messageContent = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(messageContent)) {
            return;
        }

        if (webSocketManager == null || !webSocketManager.isConnected()) {
            Toast.makeText(this, R.string.error_not_connected_cant_send, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAppUserLongId == -1L || currentGroupId == -1L) {
            Log.e(TAG, "Cannot send message: user or group ID is invalid.");
            Toast.makeText(this, "Error: Cannot identify sender or group.", Toast.LENGTH_SHORT).show();
            return;
        }

        GroupChatMessageRequest messageRequest = new GroupChatMessageRequest(
                currentGroupId,
                currentAppUserLongId,
                messageContent
        );

        Log.d(TAG, "Attempting to send group message via WebSocket: " + messageContent);
        webSocketManager.sendGroupMessage(messageRequest);

        messageEditText.setText("");
    }


    private void setLoadingState(boolean isLoading, String status) {
        if (isFinishing()) return;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (status != null && !status.isEmpty()) {
            statusTextView.setText(status);
            statusTextView.setVisibility(View.VISIBLE);
        } else {
            statusTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSocketManager != null && webSocketManager.isConnected() && currentGroupId != -1L) {
            webSocketManager.subscribeToGroupTopic(currentGroupId);
        } else if (webSocketManager != null && currentGroupId != -1L) {
            connectAndSubscribeWebSocket();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketManager != null && currentGroupId != -1L) {
            // webSocketManager.unsubscribeFromGroupTopic(currentGroupId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called for group: " + currentGroupId);
        if (webSocketManager != null && currentGroupId != -1L) {
            webSocketManager.unsubscribeFromGroupTopic(currentGroupId);
        }

        if (webSocketManager != null && webSocketManager.isConnected()) {
            webSocketManager.disconnect();
        }
    }
}