package com.javanostra.meetyourmatch.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.MessageAdapter;
import com.javanostra.meetyourmatch.persistance.entity.ChatNotificationDTO;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import com.javanostra.meetyourmatch.persistance.ChatWebSocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessagesActivity extends AppCompatActivity {

    private static final String TAG = "ChatMessagesActivity";

    public static final String EXTRA_RECIPIENT_ID = "RECIPIENT_ID";
    public static final String EXTRA_RECIPIENT_USERNAME = "RECIPIENT_USERNAME";
    // public static final String EXTRA_RECIPIENT_IMAGE_URL = "RECIPIENT_IMAGE_URL";

    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private TextView statusTextView;
    private Toolbar toolbar;

    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();

    private ChatWebSocketManager webSocketManager;

    private String currentUserId = "y4jij@ptct.net"; // TODO:
    private String recipientId;
    private String recipientUsername;
    private String userAuthToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_messages);

        recipientId = getIntent().getStringExtra(EXTRA_RECIPIENT_ID);
        recipientUsername = getIntent().getStringExtra(EXTRA_RECIPIENT_USERNAME);

        if (recipientId == null || recipientUsername == null) {
            Log.e(TAG, "Recipient ID or Username not passed in Intent extras.");
            Toast.makeText(this, "Error loading chat.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(recipientUsername);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);

        setupRecyclerView();

        webSocketManager = ChatWebSocketManager.getInstance();
        sendButton.setOnClickListener(v -> attemptSendMessage());

        observeWebSocketEvents();
        fetchMessageHistory();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(this, messageList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void observeWebSocketEvents() {
        webSocketManager.connectionState.observe(this, state -> {
            Log.d(TAG, "WebSocket Connection State: " + state);
            updateStatusUI(state);
        });

        webSocketManager.newMessage.observe(this, chatMessage -> {
            if (chatMessage != null) {
                Log.d(TAG, "New message observed via LiveData: " + chatMessage);
                boolean isFromCurrentRecipient = chatMessage.getSenderId().equals(recipientId) && chatMessage.getRecipientId().equals(currentUserId);
                boolean isSentByCurrentUserToRecipient = chatMessage.getSenderId().equals(currentUserId) && chatMessage.getRecipientId().equals(recipientId);

                if (isFromCurrentRecipient || isSentByCurrentUserToRecipient) {
                    messageList.add(chatMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    scrollToBottom();
                } else {
                    Log.w(TAG, "Received message is not for the current chat context.");
                }
            }
        });
    }

    private void updateStatusUI(ChatWebSocketManager.ConnectionState state) {
        switch (state) {
            case CONNECTING:
                progressBar.setVisibility(View.VISIBLE);
                statusTextView.setText("Connecting...");
                statusTextView.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false);
                messageEditText.setEnabled(false);
                break;
            case CONNECTED:
                progressBar.setVisibility(View.GONE);
                statusTextView.setVisibility(View.GONE);
                sendButton.setEnabled(true);
                messageEditText.setEnabled(true);
                break;
            case DISCONNECTED:
            case ERROR:
                progressBar.setVisibility(View.GONE);
                statusTextView.setText(state == ChatWebSocketManager.ConnectionState.ERROR ? "Connection Error" : "Disconnected");
                statusTextView.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false);
                messageEditText.setEnabled(false);
                Toast.makeText(this, statusTextView.getText(), Toast.LENGTH_SHORT).show();
                break;
            case INITIAL:
                progressBar.setVisibility(View.GONE);
                statusTextView.setText("Initializing...");
                statusTextView.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false);
                messageEditText.setEnabled(false);
                break;
        }
    }


    private void fetchMessageHistory() {
        setLoadingState(true, "Loading history...");
        Log.d(TAG, "Fetching history between " + currentUserId + " and " + recipientId);

        ChatApiService apiService = RetrofitClient.getRetrofit(this).create(ChatApiService.class);
        apiService.getChatMessages(currentUserId, recipientId)
                .enqueue(new Callback<List<ChatMessage>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                        setLoadingState(false, "");
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "History received: " + response.body().size() + " messages");
                            messageList.clear();
                            messageList.addAll(response.body());
                            messageAdapter.notifyDataSetChanged();
                            scrollToBottom();
                            connectWebSocket();
                        } else {
                            Log.e(TAG, "Failed to fetch history: " + response.code() + " - " + response.message());
                            Toast.makeText(ChatMessagesActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
                            statusTextView.setText("History Load Failed");
                            statusTextView.setVisibility(View.VISIBLE);
                            connectWebSocket();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                        setLoadingState(false, "History Load Error");
                        Log.e(TAG, "Error fetching history", t);
                        Toast.makeText(ChatMessagesActivity.this, "Network error loading history", Toast.LENGTH_SHORT).show();
                        connectWebSocket();
                    }
                });
    }

    private void connectWebSocket() {
        if (webSocketManager.connectionState.getValue() != ChatWebSocketManager.ConnectionState.CONNECTED &&
                webSocketManager.connectionState.getValue() != ChatWebSocketManager.ConnectionState.CONNECTING)
        {
            Log.d(TAG, "Attempting to connect WebSocket...");
            webSocketManager.connect(currentUserId, userAuthToken);
        } else {
            Log.d(TAG,"WebSocket already connected or connecting.");
            updateStatusUI(webSocketManager.connectionState.getValue());
        }
    }

    private void attemptSendMessage() {
        String messageContent = messageEditText.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!webSocketManager.isConnected()) {
            Toast.makeText(this, "Not connected, cannot send message", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatMessage messageToSend = new ChatMessage(currentUserId, recipientId, messageContent);

        webSocketManager.sendMessage(messageToSend);
        messageEditText.setText("");
    }


    private void scrollToBottom() {
        if (messageAdapter.getItemCount() > 0) {
            messagesRecyclerView.post(() ->
                    messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1)
            );
        }
    }

    private void setLoadingState(boolean isLoading, String status) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (status != null && !status.isEmpty()) {
            statusTextView.setText(status);
            statusTextView.setVisibility(View.VISIBLE);
        } else {
            statusTextView.setVisibility(View.GONE);
        }
        messageEditText.setEnabled(!isLoading && webSocketManager.isConnected());
        sendButton.setEnabled(!isLoading && webSocketManager.isConnected());
    }


    @Override
    protected void onStart() {
        super.onStart();
        // connectWebSocket();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // webSocketManager.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Disconnecting WebSocket if connected.");
        if (webSocketManager.connectionState.getValue() == ChatWebSocketManager.ConnectionState.CONNECTED ||
                webSocketManager.connectionState.getValue() == ChatWebSocketManager.ConnectionState.CONNECTING) {
            webSocketManager.disconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}