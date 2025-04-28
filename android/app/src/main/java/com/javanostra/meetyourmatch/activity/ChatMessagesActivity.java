package com.javanostra.meetyourmatch.activity;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.MessageAdapter;
import com.javanostra.meetyourmatch.adapter.DateHeaderItem;
import com.javanostra.meetyourmatch.persistance.ChatWebSocketManager;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import com.javanostra.meetyourmatch.adapter.DateUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects; 

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessagesActivity extends AppCompatActivity {

    private static final String TAG = "ChatMessagesActivity";

    
    public static final String EXTRA_SENDER_ID = "SENDER_ID"; 
    
    public static final String EXTRA_RECIPIENT_ID = "RECIPIENT_ID"; 
    
    public static final String EXTRA_RECIPIENT_LONG_ID = "RECIPIENT_LONG_ID";
    public static final String EXTRA_RECIPIENT_USERNAME = "RECIPIENT_USERNAME"; 
    public static final String EXTRA_RECIPIENT_IMAGE_URL = "RECIPIENT_IMAGE_URL"; 
    public static final String EXTRA_IS_BLOCKED = "IS_BLOCKED"; 

    
    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private TextView statusTextView;
    private LinearLayout inputLayout;
    private TextView blockStatusTextView;
    
    private ImageButton backButton;
    private ImageView recipientAvatarImageView;
    private TextView recipientUsernameTextView;
    private LinearLayout profileClickableArea;

    
    private MessageAdapter messageAdapter;
    
    private List<ChatMessage> rawMessageList = new ArrayList<>();
    
    private List<Object> displayList = new ArrayList<>();

    
    private ChatWebSocketManager webSocketManager;
    private ChatApiService chatApiService;

    
    private String currentUserId;       
    private String recipientApiId;      
    private long recipientLongId = -1L; 
    private String recipientUsername;   
    private String recipientAvatarUrl;  
    private boolean isBlocked;          

    private String userAuthToken = null; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        if (!parseIntentExtras()) {
            finish(); 
            return;
        }

        initViews();
        setupCustomToolbar(); 
        setupRecyclerView();

        updateUIForBlockStatus(); 

        chatApiService = RetrofitClient.getRetrofit(this).create(ChatApiService.class);
        webSocketManager = ChatWebSocketManager.getInstance();

        sendButton.setOnClickListener(v -> attemptSendMessage());
        backButton.setOnClickListener(v -> onBackPressed()); 

        observeWebSocketEvents();
        fetchMessageHistory();
    }

    
    private boolean parseIntentExtras() {
        currentUserId = getIntent().getStringExtra(EXTRA_SENDER_ID);
        recipientApiId = getIntent().getStringExtra(EXTRA_RECIPIENT_ID); 
        recipientLongId = getIntent().getLongExtra(EXTRA_RECIPIENT_LONG_ID, -1L); 
        recipientUsername = getIntent().getStringExtra(EXTRA_RECIPIENT_USERNAME);
        recipientAvatarUrl = getIntent().getStringExtra(EXTRA_RECIPIENT_IMAGE_URL);
        isBlocked = getIntent().getBooleanExtra(EXTRA_IS_BLOCKED, false);

        if (currentUserId == null || recipientApiId == null || recipientUsername == null || recipientLongId == -1L) {
            Log.e(TAG, "Required user data (currentUserId, recipientApiId, recipientUsername, recipientLongId) not passed in Intent extras.");
            Toast.makeText(this, R.string.error_loading_chat_info, Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d(TAG, "Chat initiated. CurrentUser: " + currentUserId + ", RecipientAPI_ID: " + recipientApiId + ", RecipientLongID: " + recipientLongId + ", RecipientUsername: " + recipientUsername + ", IsBlocked: " + isBlocked);
        return true;
    }

    
    private void initViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);
        inputLayout = findViewById(R.id.inputLayout);
        blockStatusTextView = findViewById(R.id.blockStatusTextView);

        
        View customToolbar = findViewById(R.id.custom_toolbar_layout);
        if (customToolbar == null) {
            Log.e(TAG, "FATAL: Included toolbar layout (R.id.custom_toolbar_layout) not found!");
            
            return;
        }
        backButton = customToolbar.findViewById(R.id.backButton);
        recipientAvatarImageView = customToolbar.findViewById(R.id.recipientAvatar);
        recipientUsernameTextView = customToolbar.findViewById(R.id.recipientUsernameText);
        profileClickableArea = customToolbar.findViewById(R.id.profileClickableArea);

        
        if (backButton == null || recipientAvatarImageView == null || recipientUsernameTextView == null || profileClickableArea == null) {
            Log.e(TAG, "FATAL: One or more views inside the custom toolbar are null!");
        }
    }

    
    private void setupCustomToolbar() {
        if (recipientUsernameTextView != null) {
            recipientUsernameTextView.setText(recipientUsername);
        }

        if (recipientAvatarImageView != null) {
            Glide.with(this)
                    .load(recipientAvatarUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .transform(new CircleCrop())
                    .into(recipientAvatarImageView);
        }

        if (profileClickableArea != null) {
            profileClickableArea.setOnClickListener(v -> {
                Log.d(TAG, "Profile area clicked. Opening AccountDetailsActivity for user ID: " + recipientLongId);
                Intent intent = new Intent(this, AccountDetailsActivity.class);
                intent.putExtra(AccountDetailsActivity.EXTRA_USER_ID, recipientLongId);
                intent.putExtra(AccountDetailsActivity.EXTRA_USERNAME, recipientUsername); 
                startActivity(intent);
                finish();
            });
        } else {
            Log.e(TAG, "profileClickableArea is null, cannot set listener.");
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        } else {
            Log.e(TAG, "backButton is null, cannot set listener.");
        }
    }

    
    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(currentUserId); 
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    
    private void updateUIForBlockStatus() {
        if (isBlocked) {
            inputLayout.setVisibility(View.GONE);
            blockStatusTextView.setVisibility(View.VISIBLE);
            blockStatusTextView.setText(R.string.chat_interaction_blocked);
        } else {
            inputLayout.setVisibility(View.VISIBLE);
            blockStatusTextView.setVisibility(View.GONE);
        }
    }

    
    private void observeWebSocketEvents() {
        
        webSocketManager.connectionState.observe(this, state -> {
            if (!isFinishing()) { 
                Log.d(TAG, "WebSocket Connection State Changed: " + state);
                if (!isBlocked) {
                    updateStatusUI(state);
                } else {
                    updateConnectionStatusOnly(state); 
                }
            }
        });

        
        webSocketManager.newMessage.observe(this, chatMessage -> {
            if (chatMessage != null && !isFinishing()) { 
                Log.d(TAG, "New message observed via WebSocket: " + chatMessage.getContent());
                
                boolean isRelevant = (Objects.equals(chatMessage.getSenderId(), recipientApiId) && Objects.equals(chatMessage.getRecipientId(), currentUserId)) ||
                        (Objects.equals(chatMessage.getSenderId(), currentUserId) && Objects.equals(chatMessage.getRecipientId(), recipientApiId));

                if (isRelevant) {
                    Log.d(TAG, "Updating UI on thread: " + Thread.currentThread().getName()); 
                    boolean exists = false;
                    for (ChatMessage msg : rawMessageList) {
                        if (Objects.equals(msg.getTimestamp(), chatMessage.getTimestamp()) &&
                                Objects.equals(msg.getSenderId(), chatMessage.getSenderId()) &&
                                Objects.equals(msg.getRecipientId(), chatMessage.getRecipientId())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        rawMessageList.add(chatMessage);
                        Collections.sort(rawMessageList, (m1, m2) -> Long.compare(m1.getTimestamp().getTime(), m2.getTimestamp().getTime()));
                        processMessagesWithDateHeaders(rawMessageList);
                        messageAdapter.submitList(new ArrayList<>(displayList), this::scrollToBottomIfNeeded);

         /*
         List<Object> listCopy = new ArrayList<>(displayList);
         new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
              messageAdapter.submitList(listCopy, this::scrollToBottomIfNeeded);
         });
         */
                        messagesRecyclerView.requestLayout();
                    } else {
                        Log.w(TAG,"Duplicate message detected, ignoring: " + chatMessage.getContent());
                    }

                } else {
                    Log.w(TAG, "Received message is not for the current chat context.");
                }
            }
        });
    }

    
    private void scrollToBottomIfNeeded() {
        int itemCount = messageAdapter.getItemCount();
        if (itemCount > 0) {
            messagesRecyclerView.smoothScrollToPosition(itemCount - 1);
            
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
        updateUIForBlockStatus(); 
    }

    
    private void updateConnectionStatusOnly(ChatWebSocketManager.ConnectionState state) {
        if (isFinishing()) return;
        switch (state) {
            case CONNECTING: setLoadingState(true, getString(R.string.status_connecting)); break;
            case CONNECTED: setLoadingState(false, ""); break; 
            case DISCONNECTED: setLoadingState(false, getString(R.string.status_disconnected)); break;
            case ERROR: setLoadingState(false, getString(R.string.status_connection_error)); break;
            case INITIAL: setLoadingState(true, getString(R.string.status_initializing)); break;
        }
    }

    
    private void fetchMessageHistory() {
        setLoadingState(true, getString(R.string.status_loading_history));
        Log.d(TAG, "Fetching history for recipient API ID: " + recipientApiId);

        if (chatApiService == null) {
            Log.e(TAG, "ChatApiService is null, cannot fetch history.");
            setLoadingState(false, getString(R.string.error_generic));
            return;
        }


        chatApiService.getChatMessages(recipientApiId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatMessage>> call, @NonNull Response<List<ChatMessage>> response) {
                if (isFinishing()) return; 
                setLoadingState(false, "");

                if (response.isSuccessful() && response.body() != null) {
                    rawMessageList = response.body();
                    Collections.sort(rawMessageList, (m1, m2) -> Long.compare(m1.getTimestamp().getTime(), m2.getTimestamp().getTime())); 

                    Log.d(TAG, "History received: " + rawMessageList.size() + " messages. Processing headers...");
                    processMessagesWithDateHeaders(rawMessageList);
                    messageAdapter.submitList(displayList, () -> {
                        if (!displayList.isEmpty()) {
                            
                            messagesRecyclerView.scrollToPosition(displayList.size() - 1);
                        }
                    });
                    connectWebSocketIfNeeded(); 
                } else {
                    Log.e(TAG, "Failed to fetch history: " + response.code() + " - " + response.message());
                    Toast.makeText(ChatMessagesActivity.this, R.string.error_loading_history, Toast.LENGTH_SHORT).show();
                    if (!isBlocked) statusTextView.setText(R.string.error_loading_history); else statusTextView.setText("");
                    statusTextView.setVisibility(!isBlocked ? View.VISIBLE : View.GONE);
                    rawMessageList.clear();
                    processMessagesWithDateHeaders(rawMessageList);
                    messageAdapter.submitList(displayList); 
                    connectWebSocketIfNeeded(); 
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatMessage>> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                setLoadingState(false, getString(R.string.error_network_history));
                Log.e(TAG, "Error fetching history", t);
                Toast.makeText(ChatMessagesActivity.this, R.string.error_network_history, Toast.LENGTH_SHORT).show();
                rawMessageList.clear();
                processMessagesWithDateHeaders(rawMessageList);
                messageAdapter.submitList(displayList);
                connectWebSocketIfNeeded();
            }
        });
    }

    
    private void processMessagesWithDateHeaders(List<ChatMessage> messages) {
        displayList.clear();
        ChatMessage previousMessage = null;
        for (ChatMessage currentMessage : messages) {
            if (currentMessage == null || currentMessage.getTimestamp().getTime() <= 0) continue;

            if (DateUtils.shouldAddDateHeader(previousMessage, currentMessage)) {
                displayList.add(new DateHeaderItem(currentMessage.getTimestamp().getTime()));
            }
            displayList.add(currentMessage);
            previousMessage = currentMessage;
        }
        Log.d(TAG, "Processed message list size (with headers): " + displayList.size());
    }


    
    private void connectWebSocketIfNeeded() {
        if (isBlocked) {
            Log.w(TAG, "WebSocket connection skipped because chat is blocked.");
            updateUIForBlockStatus(); 
            return;
        }
        
        if (webSocketManager != null && webSocketManager.connectionState.getValue() != ChatWebSocketManager.ConnectionState.CONNECTED &&
                webSocketManager.connectionState.getValue() != ChatWebSocketManager.ConnectionState.CONNECTING) {
            Log.d(TAG, "Attempting to connect WebSocket...");
            webSocketManager.connect(currentUserId, userAuthToken); 
        } else if (webSocketManager != null){
            Log.d(TAG,"WebSocket already connected or connecting. Updating UI based on current state.");
            
            updateStatusUI(webSocketManager.connectionState.getValue());
        } else {
            Log.e(TAG, "WebSocketManager is null, cannot connect.");
        }
    }

    
    private void attemptSendMessage() {
        if (isBlocked) {
            Toast.makeText(this, R.string.error_cant_send_blocked, Toast.LENGTH_SHORT).show();
            return;
        }
        String messageContent = messageEditText.getText().toString().trim();
        if (messageContent.isEmpty()) { return; }

        if (webSocketManager == null || !webSocketManager.isConnected()) {
            Toast.makeText(this, R.string.error_not_connected_cant_send, Toast.LENGTH_SHORT).show();
            
            return;
        }

        
        long timestamp = System.currentTimeMillis();
        ChatMessage messageToSend = new ChatMessage(currentUserId, recipientApiId, messageContent, new Timestamp(timestamp));

        Log.d(TAG, "Attempting to send message via WebSocket: [" + timestamp + "] " + messageContent);
        webSocketManager.sendMessage(messageToSend);
        messageEditText.setText(""); 

        
        
        
        
        
        
    }

    
    private void setLoadingState(boolean isLoading, String status) {
        if (isFinishing()) return;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (!isBlocked && status != null && !status.isEmpty()) {
            statusTextView.setText(status);
            statusTextView.setVisibility(View.VISIBLE);
        } else {
            
            statusTextView.setVisibility(View.GONE);
        }
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Disconnecting WebSocket if connected.");
        
        
        if (webSocketManager != null && webSocketManager.isConnected()) {
            webSocketManager.disconnect();
        }
        
        
        
    }

    
}