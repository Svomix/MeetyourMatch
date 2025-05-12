package com.javanostra.meetyourmatch.persistance;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;


import com.javanostra.meetyourmatch.persistance.entity.ChatNotificationDTO;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessageNotification;
import com.javanostra.meetyourmatch.persistance.entity.GroupChatMessageRequest;

import java.io.IOException; 
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public class ChatWebSocketManager {

    private static final String TAG = "ChatWebSocketManager";
    private static final String WEBSOCKET_URL = "ws://10.0.2.2:8080/ws";
    // private static final String WEBSOCKET_URL = "ws://46.0.192.151:8000/ws";
    //private static final String WEBSOCKET_URL = "ws://192.168.1.65:8080/ws";

    private static volatile ChatWebSocketManager instance;

    public static ChatWebSocketManager getInstance() {
        if (instance == null) {
            synchronized (ChatWebSocketManager.class) {
                if (instance == null) {
                    instance = new ChatWebSocketManager();
                }
            }
        }
        return instance;
    }

    private final Gson gson = buildGson();

    public enum ConnectionState {INITIAL, CONNECTING, CONNECTED, DISCONNECTED, ERROR}

    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>(ConnectionState.INITIAL);
    public final LiveData<ConnectionState> connectionState = _connectionState;

    private final MutableLiveData<ChatMessage> _newMessage = new MutableLiveData<>();
    public final LiveData<ChatMessage> newMessage = _newMessage;

    private final MutableLiveData<GroupChatMessageNotification> _newGroupMessage = new MutableLiveData<>();
    public final LiveData<GroupChatMessageNotification> newGroupMessage = _newGroupMessage;

    private String currentUserId;
    private String authToken;

    private CompositeDisposable compositeDisposable;
    private StompClient mStompClient;
    private Disposable topicSubscriptionDisposable;
    private final Map<Long, Disposable> groupTopicSubscriptions = new HashMap<>();

    private ChatWebSocketManager() {
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }

    private static final ThreadLocal<DateFormat> iso8601FormatLocal = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    });
    private static final ThreadLocal<DateFormat> iso8601FormatLocalNoMillis = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    });

    private static class TimestampTypeAdapter extends TypeAdapter<Timestamp> {
        @Override
        public void write(JsonWriter out, Timestamp value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                // out.value(iso8601FormatLocal.get().format(value));
                out.value(value.getTime());
            }
        }

        @Override
        public Timestamp read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek == JsonToken.NUMBER) {
                long millis = in.nextLong();
                return new Timestamp(millis);
            } else if (peek == JsonToken.STRING) {
                String dateString = in.nextString();
                try {
                    Date parsedDate = iso8601FormatLocal.get().parse(dateString);
                    return new Timestamp(parsedDate.getTime());
                } catch (ParseException e1) {
                    try {
                        Date parsedDate = iso8601FormatLocalNoMillis.get().parse(dateString);
                        return new Timestamp(parsedDate.getTime());
                    } catch (ParseException e2) {
                        Log.e(TAG, "Could not parse timestamp string in ChatWebSocketManager: " + dateString, e2);
                        throw new IOException("Could not parse timestamp string: " + dateString, e2);
                    }
                }
            } else {
                Log.w(TAG, "Unexpected token type for Timestamp in ChatWebSocketManager: " + peek + ". Skipping value.");
                in.skipValue();
                return null;
            }
        }
    }

    private static class DateTypeAdapter extends TypeAdapter<Date> {
        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.getTime());
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek == JsonToken.NUMBER) {
                return new Date(in.nextLong());
            } else if (peek == JsonToken.STRING) {
                String dateString = in.nextString();
                try {
                    return iso8601FormatLocal.get().parse(dateString);
                } catch (ParseException e1) {
                    try {
                        return iso8601FormatLocalNoMillis.get().parse(dateString);
                    } catch (ParseException e2) {
                        Log.e(TAG, "Could not parse date string in ChatWebSocketManager: " + dateString, e2);
                        throw new IOException("Could not parse date string: " + dateString, e2);
                    }
                }
            } else {
                Log.w(TAG, "Unexpected token type for Date in ChatWebSocketManager: " + peek + ". Skipping value.");
                in.skipValue();
                return null;
            }
        }
    }

    public void connect(String userId, @Nullable String token) {
        Log.d(TAG, "Connect requested for user: " + userId +
                ". Current state: " + (_connectionState.getValue() != null ? _connectionState.getValue().name() : "null") +
                ", Current user context: " + this.currentUserId);

        if (this.currentUserId != null && this.currentUserId.equals(userId) && this.mStompClient != null) {
            ConnectionState currentState = _connectionState.getValue();
            if (currentState == ConnectionState.CONNECTED) {
                Log.d(TAG, "Already connected as user: " + userId + ". Ensuring subscription.");
                if (topicSubscriptionDisposable == null || topicSubscriptionDisposable.isDisposed()) {
                    subscribeToUserQueue();
                }
                return;
            } else if (currentState == ConnectionState.CONNECTING) {
                Log.d(TAG, "Connection attempt already in progress for user: " + userId);
                return;
            }
        }

        if (this.mStompClient != null) {
            Log.d(TAG, "New connect call. Disconnecting previous client (ID: " + System.identityHashCode(this.mStompClient) +
                    ") for user: " + this.currentUserId);
            disconnectPreviousClientInternal();
        }

        this.currentUserId = userId;
        this.authToken = token;
        _connectionState.postValue(ConnectionState.CONNECTING);

        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()){
            this.compositeDisposable.dispose();
        }
        this.compositeDisposable = new CompositeDisposable();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(15, TimeUnit.SECONDS)
                .build();
        this.mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL, null, okHttpClient);

        final StompClient clientForThisAttempt = this.mStompClient;
        final String userIdForThisAttempt = this.currentUserId;

        Log.d(TAG, "Initializing StompClient (ID: " + System.identityHashCode(clientForThisAttempt) + ") for user: " + userIdForThisAttempt);

        Disposable lifecycleDisposable = clientForThisAttempt.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    if (clientForThisAttempt != this.mStompClient || !userIdForThisAttempt.equals(this.currentUserId)) {
                        Log.w(TAG, "LifecycleEvent (" + lifecycleEvent.getType() + ") for STALE client. Ignoring.");
                        if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED && clientForThisAttempt.isConnected()) {
                            clientForThisAttempt.disconnect();
                        }
                        return;
                    }
                    Log.d(TAG, "STOMP LifecycleEvent: " + lifecycleEvent.getType() + " for user: " + userIdForThisAttempt);
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "WebSocket OPENED. Waiting for STOMP handshake via ping send.");
                            break;
                        case ERROR:
                            Log.e(TAG, "WebSocket ERROR", lifecycleEvent.getException());
                            if (_connectionState.getValue() == ConnectionState.CONNECTING || _connectionState.getValue() == ConnectionState.CONNECTED) {
                                _connectionState.setValue(ConnectionState.ERROR);
                            }
                            break;
                        case CLOSED:
                            Log.i(TAG, "WebSocket CLOSED.");
                            if (_connectionState.getValue() != ConnectionState.DISCONNECTED && _connectionState.getValue() != ConnectionState.INITIAL &&
                                    (_connectionState.getValue() == ConnectionState.CONNECTING || _connectionState.getValue() == ConnectionState.CONNECTED)) {
                                _connectionState.setValue(ConnectionState.DISCONNECTED);
                            }
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.w(TAG, "WebSocket FAILED_SERVER_HEARTBEAT.");
                            if (_connectionState.getValue() == ConnectionState.CONNECTING || _connectionState.getValue() == ConnectionState.CONNECTED) {
                                _connectionState.setValue(ConnectionState.ERROR);
                            }
                            break;
                    }
                }, throwable -> {
                    if (clientForThisAttempt == this.mStompClient && userIdForThisAttempt.equals(this.currentUserId)) {
                        Log.e(TAG, "Error in STOMP lifecycle subscription for user: " + userIdForThisAttempt, throwable);
                        if (_connectionState.getValue() == ConnectionState.CONNECTING || _connectionState.getValue() == ConnectionState.CONNECTED) {
                            _connectionState.postValue(ConnectionState.ERROR);
                        }
                    } else {
                        Log.w(TAG, "Error in STALE lifecycle subscription. Ignoring for global state.", throwable);
                    }
                });
        this.compositeDisposable.add(lifecycleDisposable);

        List<StompHeader> connectHeaders = new ArrayList<>();
        clientForThisAttempt.connect(connectHeaders);
        Log.d(TAG, "Called StompClient.connect() for user: " + userIdForThisAttempt + ". Now sending ping to confirm STOMP handshake.");

        StompMessage pingMessage = new StompMessage(
                StompCommand.SEND,
                Collections.singletonList(new StompHeader(StompHeader.DESTINATION, "/app/client-handshake-check")), // Можно использовать любой некритичный endpoint
                null
        );

        Disposable pingDisposable = clientForThisAttempt.send(pingMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (clientForThisAttempt == this.mStompClient && userIdForThisAttempt.equals(this.currentUserId)) {
                        if (clientForThisAttempt.isConnected()) {
                            Log.i(TAG, "STOMP handshake CONFIRMED (via ping send) for user: " + this.currentUserId +
                                    ". Client (ID: " + System.identityHashCode(clientForThisAttempt) + ") isConnected: true.");
                            _connectionState.setValue(ConnectionState.CONNECTED);
                            subscribeToUserQueue();
                        } else {
                            Log.w(TAG, "STOMP handshake ping send completed, but client (ID: " + System.identityHashCode(clientForThisAttempt) +
                                    ") reports NOT connected for user: " + this.currentUserId + ". Setting to ERROR.");
                            _connectionState.setValue(ConnectionState.ERROR);
                        }
                    } else {
                        Log.w(TAG, "STOMP handshake ping completed for a STALE client or user. Ignoring. Stale client ID: " + System.identityHashCode(clientForThisAttempt));
                        if (clientForThisAttempt.isConnected()) {
                            clientForThisAttempt.disconnect();
                        }
                    }
                }, throwable -> {
                    if (clientForThisAttempt == this.mStompClient && userIdForThisAttempt.equals(this.currentUserId)) {
                        Log.e(TAG, "STOMP handshake FAILED (via ping send error) for user: " + this.currentUserId, throwable);
                        _connectionState.setValue(ConnectionState.ERROR);
                    } else {
                        Log.w(TAG, "STOMP handshake ping error for a STALE client or user. Ignoring. Stale client ID: " + System.identityHashCode(clientForThisAttempt), throwable);
                    }
                });
        this.compositeDisposable.add(pingDisposable);
    }

    private void subscribeToUserQueue() {
        final StompClient clientForSubscription = this.mStompClient;
        final String userIdForSubscription = this.currentUserId;

        Log.d(TAG, "Attempting to subscribe to user queue. StompClient ID: " +
                (clientForSubscription != null ? System.identityHashCode(clientForSubscription) : "null") +
                ", isConnected: " + (clientForSubscription != null && clientForSubscription.isConnected()) +
                ", User ID: " + userIdForSubscription);

        if (clientForSubscription == null || !clientForSubscription.isConnected() || userIdForSubscription == null) {
            Log.w(TAG, "Cannot subscribe: Conditions not met. client connected: " +
                    (clientForSubscription != null && clientForSubscription.isConnected()) +
                    ", userId: " + userIdForSubscription +
                    ". This is unexpected after connectCompletable success.");
            if (_connectionState.getValue() == ConnectionState.CONNECTED) {
                _connectionState.postValue(ConnectionState.ERROR);
            }
            return;
        }

        if (topicSubscriptionDisposable != null && !topicSubscriptionDisposable.isDisposed()) {
            Log.d(TAG, "Disposing previous topic subscription.");
            topicSubscriptionDisposable.dispose();
        }

        String destination = "/user/" + userIdForSubscription + "/queue/messages";
        Log.d(TAG, "Subscribing to STOMP topic: " + destination + " with StompClient (ID: " + System.identityHashCode(clientForSubscription) + ")");

        topicSubscriptionDisposable = clientForSubscription.topic(destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((StompMessage stompMessage) -> {
                    if (clientForSubscription != this.mStompClient || !userIdForSubscription.equals(this.currentUserId)) {
                        Log.w(TAG, "Received STOMP message on a STALE topic subscription. Ignoring.");
                        return;
                    }
                    Log.d(TAG, "Received raw message from " + destination + ": " + stompMessage.getPayload());
                    try {
                        ChatNotificationDTO notification = gson.fromJson(stompMessage.getPayload(), ChatNotificationDTO.class);
                        if (notification != null) {
                            Log.d(TAG, "Parsed notification: Sender=" + notification.getSenderId() + ", Content=" + notification.getContent());
                            _newMessage.postValue(notification.toChatMessage());
                        } else {
                            Log.w(TAG, "Parsed notification is null.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing received message payload from " + destination, e);
                    }
                }, throwable -> {
                    if (clientForSubscription == this.mStompClient && userIdForSubscription.equals(this.currentUserId)) {
                        Log.e(TAG, "Error on STOMP topic subscription: " + destination + " for StompClient (ID: " + System.identityHashCode(clientForSubscription) + ")", throwable);
                        _connectionState.postValue(ConnectionState.ERROR);
                    } else {
                        Log.w(TAG, "Error on STALE topic subscription. Ignoring for global state.", throwable);
                    }
                });

        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.add(topicSubscriptionDisposable);
            Log.d(TAG, "Topic subscription added to CompositeDisposable.");
        } else {
            Log.e(TAG, "FATAL: CompositeDisposable is null or disposed when trying to add topic subscription. This will leak memory/resources.");
        }
    }

    private void disconnectPreviousClientInternal() {
        Log.d(TAG, "disconnectPreviousClientInternal called for mStompClient (ID: " +
                (this.mStompClient != null ? System.identityHashCode(this.mStompClient) : "null") + ")");

        for (Disposable subscription : groupTopicSubscriptions.values()) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
        groupTopicSubscriptions.clear();
        Log.d(TAG, "All group topic subscriptions disposed and cleared.");

        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.dispose();
            Log.d(TAG, "Previous CompositeDisposable disposed.");
        }
        this.compositeDisposable = null;

        if (this.mStompClient != null) {
            final StompClient clientToDisconnect = this.mStompClient;
            this.mStompClient = null;

            if (clientToDisconnect.isConnected()) {
                Log.d(TAG, "Disconnecting previously connected StompClient (ID: " + System.identityHashCode(clientToDisconnect) + ")");
                clientToDisconnect.disconnect();
            } else {
                Log.d(TAG, "Previous StompClient (ID: " + System.identityHashCode(clientToDisconnect) + ") was not connected or already disconnected.");
            }
        }
        this.topicSubscriptionDisposable = null;
    }

    public void subscribeToGroupTopic(Long groupId) {
        final StompClient clientForSubscription = this.mStompClient;
        final String userIdForContext = this.currentUserId;

        if (clientForSubscription == null || !clientForSubscription.isConnected() || userIdForContext == null) {
            Log.w(TAG, "Cannot subscribe to group topic: STOMP client not connected or user context lost.");
            if (_connectionState.getValue() == ConnectionState.CONNECTED) {
                _connectionState.postValue(ConnectionState.ERROR);
            }
            return;
        }

        if (groupTopicSubscriptions.containsKey(groupId)) {
            Disposable oldSubscription = groupTopicSubscriptions.get(groupId);
            if (oldSubscription != null && !oldSubscription.isDisposed()) {
                Log.d(TAG, "Disposing previous subscription for group topic: " + groupId);
                oldSubscription.dispose();
            }
        }

        String destination = "/topic/group/" + groupId;
        Log.d(TAG, "Subscribing to STOMP group topic: " + destination);

        Disposable groupSubscriptionDisposable = clientForSubscription.topic(destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((StompMessage stompMessage) -> {
                    if (clientForSubscription != this.mStompClient || !userIdForContext.equals(this.currentUserId)) {
                        Log.w(TAG, "Received STOMP message on a STALE group topic subscription. Ignoring.");
                        return;
                    }
                    Log.d(TAG, "Received raw message from " + destination + ": " + stompMessage.getPayload());
                    try {
                        GroupChatMessageNotification notification = gson.fromJson(stompMessage.getPayload(), GroupChatMessageNotification.class);
                        if (notification != null) {
                            Log.d(TAG, "Parsed group notification: GroupID=" + notification.getGroupChatId() +", Sender=" + notification.getSenderUsername() + ", Content=" + notification.getContent());
                            _newGroupMessage.postValue(notification);
                        } else {
                            Log.w(TAG, "Parsed group notification is null.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing received message payload from " + destination, e);
                    }
                }, throwable -> {
                    if (clientForSubscription == this.mStompClient && userIdForContext.equals(this.currentUserId)) {
                        Log.e(TAG, "Error on STOMP group topic subscription: " + destination, throwable);
                        groupTopicSubscriptions.remove(groupId);
                    } else {
                        Log.w(TAG, "Error on STALE group topic subscription. Ignoring for global state.", throwable);
                    }
                });

        groupTopicSubscriptions.put(groupId, groupSubscriptionDisposable);
        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.add(groupSubscriptionDisposable);
            Log.d(TAG, "Group topic subscription for " + groupId + " added to CompositeDisposable.");
        } else {
            Log.e(TAG, "CompositeDisposable is null or disposed when trying to add group topic subscription.");
        }
    }

    public void unsubscribeFromGroupTopic(Long groupId) {
        if (groupTopicSubscriptions.containsKey(groupId)) {
            Disposable subscription = groupTopicSubscriptions.get(groupId);
            if (subscription != null && !subscription.isDisposed()) {
                Log.d(TAG, "Unsubscribing from group topic: " + groupId);
                subscription.dispose();
            }
            groupTopicSubscriptions.remove(groupId);
        }
    }

    public void disconnect() {
        Log.d(TAG, "Public disconnect() called for user: " + this.currentUserId +
                " with StompClient ID: " + (this.mStompClient != null ? System.identityHashCode(this.mStompClient) : "null"));

        for (Disposable subscription : groupTopicSubscriptions.values()) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
        groupTopicSubscriptions.clear();
        Log.d(TAG, "All group topic subscriptions disposed and cleared in public disconnect().");

        final StompClient clientToDisconnect = this.mStompClient;
        final String userBeingDisconnected = this.currentUserId;

        if (_connectionState.getValue() != ConnectionState.DISCONNECTED && _connectionState.getValue() != ConnectionState.INITIAL) {
            _connectionState.postValue(ConnectionState.DISCONNECTED);
        }

        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.dispose();
            Log.d(TAG, "CompositeDisposable disposed in public disconnect().");
        }
        this.compositeDisposable = null;
        this.topicSubscriptionDisposable = null;

        if (clientToDisconnect != null) {
            this.mStompClient = null;
            if (clientToDisconnect.isConnected()) {
                Log.d(TAG, "Disconnecting StompClient (ID: " + System.identityHashCode(clientToDisconnect) + ")");
                clientToDisconnect.disconnect();
            } else {
                Log.d(TAG, "StompClient (ID: " + System.identityHashCode(clientToDisconnect) + ") was not connected for public disconnect().");
            }
        }

        this.currentUserId = null;
        this.authToken = null;

        Log.i(TAG, "STOMP Client disconnect process initiated for user: " + userBeingDisconnected +
                ". Local resources and user context cleared.");
    }

    public boolean isConnectedOrConnecting() {
        ConnectionState state = _connectionState.getValue();
        return state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING;
    }

    public boolean isConnected() {
        return mStompClient != null && mStompClient.isConnected() && _connectionState.getValue() == ConnectionState.CONNECTED;
    }

    public void sendMessage(ChatMessage chatMessage) {
        final StompClient currentActiveClient = this.mStompClient;
        if (currentActiveClient == null || !currentActiveClient.isConnected() || _connectionState.getValue() != ConnectionState.CONNECTED) {
            Log.w(TAG, "Cannot send message: STOMP client is not connected or in wrong state. Current client ID: " +
                    (currentActiveClient != null ? System.identityHashCode(currentActiveClient) : "null") +
                    ", isConnected: " + (currentActiveClient != null && currentActiveClient.isConnected()) +
                    ", manager state: " + (_connectionState.getValue() != null ? _connectionState.getValue().name() : "null"));
            _connectionState.postValue(ConnectionState.ERROR);
            return;
        }

        String destination = "/app/chat";
        String payload = gson.toJson(chatMessage);
        Log.d(TAG, "Sending message to " + destination + " with StompClient (ID: " + System.identityHashCode(currentActiveClient) + "): " + payload);

        Disposable sendDisposable = currentActiveClient.send(destination, payload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, "STOMP send() completed successfully for message to " + destination),
                        throwable -> {
                            Log.e(TAG, "Error sending STOMP message to " + destination + " with StompClient (ID: " + System.identityHashCode(currentActiveClient) + ")", throwable);
                        }
                );

        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.add(sendDisposable);
        } else {
            Log.w(TAG, "Cannot add sendDisposable to CompositeDisposable as it's null or disposed. Send operation might not be cancellable if disconnect happens rapidly.");
        }
    }

    public void sendGroupMessage(GroupChatMessageRequest groupMessageRequest) {
        final StompClient currentActiveClient = this.mStompClient;
        if (currentActiveClient == null || !currentActiveClient.isConnected() || _connectionState.getValue() != ConnectionState.CONNECTED) {
            Log.w(TAG, "Cannot send group message: STOMP client is not connected or in wrong state.");
            _connectionState.postValue(ConnectionState.ERROR);
            return;
        }

        String destination = "/app/groupChat";
        String payload = gson.toJson(groupMessageRequest);
        Log.d(TAG, "Sending group message to " + destination + ": " + payload);

        Disposable sendDisposable = currentActiveClient.send(destination, payload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, "STOMP send() for group message completed successfully to " + destination),
                        throwable -> Log.e(TAG, "Error sending STOMP group message to " + destination, throwable)
                );
        if (this.compositeDisposable != null && !this.compositeDisposable.isDisposed()) {
            this.compositeDisposable.add(sendDisposable);
        } else {
            Log.w(TAG, "Cannot add group sendDisposable to CompositeDisposable as it's null or disposed.");
        }
    }
}