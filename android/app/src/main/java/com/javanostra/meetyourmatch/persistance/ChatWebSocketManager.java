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

import java.io.IOException; 
import java.sql.Timestamp; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public class ChatWebSocketManager {

    private static final String TAG = "ChatWebSocketManager";
    private static final String WEBSOCKET_URL = "ws://10.0.2.2:8080/ws"; 

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

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    
    private final Gson gson = buildGson();
    

    public enum ConnectionState { INITIAL, CONNECTING, CONNECTED, DISCONNECTED, ERROR }

    private final MutableLiveData<ConnectionState> _connectionState = new MutableLiveData<>(ConnectionState.INITIAL);
    public final LiveData<ConnectionState> connectionState = _connectionState;

    private final MutableLiveData<ChatMessage> _newMessage = new MutableLiveData<>();
    public final LiveData<ChatMessage> newMessage = _newMessage;

    private String currentUserId;
    private String authToken;

    private Disposable lifecycleDisposable;
    private Disposable topicSubscriptionDisposable;


    private ChatWebSocketManager() { }

    
    private Gson buildGson() {
        return new GsonBuilder()
                
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }
    

    
    
    private static class TimestampTypeAdapter extends TypeAdapter<Timestamp> {
        @Override
        public void write(JsonWriter out, Timestamp value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                
                out.value(value.getTime());
            }
        }

        @Override
        public Timestamp read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            
            long millis = in.nextLong();
            return new Timestamp(millis);
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
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            
            long millis = in.nextLong();
            return new Date(millis);
        }
    }
    


    public void connect(String userId, @Nullable String token) {
        
        if (isConnectedOrConnecting()) {
            if (userId.equals(this.currentUserId)) {
                Log.w(TAG, "Already connected or connecting for user: " + userId);
                if (topicSubscriptionDisposable == null || topicSubscriptionDisposable.isDisposed()) {
                    subscribeToUserQueue();
                }
                return;
            } else {
                Log.w(TAG,"Connection request for different user. Disconnecting first.");
                disconnect();
            }
        }

        Log.d(TAG, "Connecting WebSocket for user: " + userId);
        this.currentUserId = userId;
        this.authToken = token;
        _connectionState.postValue(ConnectionState.CONNECTING);

        disposeSubscriptions();
        compositeDisposable = new CompositeDisposable();

        
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(15, TimeUnit.SECONDS) 
                .build();

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL, null, okHttpClient);

        
        List<StompHeader> headers = new ArrayList<>();
        if (authToken != null && !authToken.isEmpty()) {
            Log.d(TAG, "Adding Auth header");
            
            
        }

        
        lifecycleDisposable = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "STOMP connection opened");
                            _connectionState.setValue(ConnectionState.CONNECTED);
                            subscribeToUserQueue(); 
                            break;
                        case ERROR:
                            Log.e(TAG, "STOMP connection error", lifecycleEvent.getException());
                            _connectionState.setValue(ConnectionState.ERROR);
                            disposeSubscriptions(); 
                            break;
                        case CLOSED:
                            Log.i(TAG, "STOMP connection closed");
                            
                            if (_connectionState.getValue() != ConnectionState.DISCONNECTED) {
                                _connectionState.setValue(ConnectionState.DISCONNECTED);
                            }
                            disposeSubscriptions(); 
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.w(TAG, "STOMP server heartbeat failed");
                            _connectionState.setValue(ConnectionState.ERROR); 
                            disposeSubscriptions();
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Error in STOMP lifecycle subscription", throwable);
                    _connectionState.postValue(ConnectionState.ERROR);
                    disposeSubscriptions();
                });

        compositeDisposable.add(lifecycleDisposable);

        
        Log.d(TAG, "Executing STOMP connect...");
        mStompClient.connect(headers);
    }

    private void subscribeToUserQueue() {
        
        if (mStompClient == null || !mStompClient.isConnected() || currentUserId == null) {
            Log.w(TAG, "Cannot subscribe: Not connected or userId is null.");
            _connectionState.postValue(ConnectionState.ERROR); 
            return;
        }

        
        if (topicSubscriptionDisposable != null && !topicSubscriptionDisposable.isDisposed()) {
            Log.d(TAG, "Disposing previous topic subscription.");
            topicSubscriptionDisposable.dispose();
        }

        String destination = "/user/" + currentUserId + "/queue/messages";
        Log.d(TAG, "Subscribing to: " + destination);

        topicSubscriptionDisposable = mStompClient.topic(destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((StompMessage stompMessage) -> {
                    Log.d(TAG, "Received raw message from " + destination + ": " + stompMessage.getPayload());
                    try {
                        
                        ChatNotificationDTO notification = gson.fromJson(stompMessage.getPayload(), ChatNotificationDTO.class);
                        Log.d(TAG, "Parsed notification: Sender=" + notification.getSenderId() + ", Content=" + notification.getContent());
                        
                        if (notification != null) {
                            _newMessage.postValue(notification.toChatMessage()); 
                        } else {
                            Log.w(TAG, "Parsed notification is null.");
                        }
                    } catch (Exception e) {
                        
                        Log.e(TAG, "Error parsing received message payload from " + destination, e);
                    }
                }, throwable -> {
                    
                    Log.e(TAG, "Error on topic subscription: " + destination, throwable);
                    
                    _connectionState.postValue(ConnectionState.ERROR);
                });

        
        if (compositeDisposable != null) {
            compositeDisposable.add(topicSubscriptionDisposable);
            Log.d(TAG, "Topic subscription added to CompositeDisposable.");
        } else {
            Log.e(TAG, "CompositeDisposable is null, cannot add topic subscription!");
        }
    }

    public void sendMessage(ChatMessage chatMessage) {
        if (mStompClient == null || !mStompClient.isConnected()) {
            Log.w(TAG, "Cannot send message: STOMP client is not connected.");
            
            _connectionState.postValue(ConnectionState.ERROR); 
            return;
        }

        String destination = "/app/chat";
        
        String payload = gson.toJson(chatMessage);
        
        Log.d(TAG, "Sending message to " + destination + ": " + payload); 

        if (compositeDisposable == null) {
            Log.e(TAG, "CompositeDisposable is null, creating new one for sending.");
            compositeDisposable = new CompositeDisposable();
        }

        
        Disposable sendDisposable = mStompClient.send(destination, payload)
                .subscribeOn(Schedulers.io())      
                .observeOn(AndroidSchedulers.mainThread()) 
                .subscribe(
                        () -> Log.d(TAG, "STOMP send() completed successfully for message to " + destination), 
                        throwable -> { 
                            Log.e(TAG, "Error sending STOMP message to " + destination, throwable);
                            
                        }
                );
        
        compositeDisposable.add(sendDisposable);
    }


    public void disconnect() {
        
        Log.d(TAG, "Disconnect requested.");

        
        _connectionState.postValue(ConnectionState.DISCONNECTED);

        
        disposeSubscriptions();

        
        if (mStompClient != null) {
            if (mStompClient.isConnected()) {
                mStompClient.disconnect(); 
            }
            
            
        }

        
        currentUserId = null;
        authToken = null;
        Log.i(TAG,"STOMP Client disconnected command sent and local resources released.");
    }

    private boolean isConnectedOrConnecting() {
        
        ConnectionState state = _connectionState.getValue();
        return state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING;
    }

    public boolean isConnected() {
        
        return mStompClient != null && mStompClient.isConnected() && _connectionState.getValue() == ConnectionState.CONNECTED;
    }

    private void disposeSubscriptions() {
        
        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed()) {
            lifecycleDisposable.dispose();
            Log.d(TAG,"Lifecycle subscription disposed.");
        }
        if (topicSubscriptionDisposable != null && !topicSubscriptionDisposable.isDisposed()) {
            topicSubscriptionDisposable.dispose();
            Log.d(TAG,"Topic subscription disposed.");
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear(); 
            Log.d(TAG,"CompositeDisposable cleared.");
        } else {
            compositeDisposable = new CompositeDisposable(); 
        }
        
        topicSubscriptionDisposable = null;
        lifecycleDisposable = null;
    }
}