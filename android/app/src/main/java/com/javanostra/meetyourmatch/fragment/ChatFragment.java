package com.javanostra.meetyourmatch.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.AccountDetailsActivity;
import com.javanostra.meetyourmatch.activity.ChatMessagesActivity;
import com.javanostra.meetyourmatch.activity.CreateGroupActivity;
import com.javanostra.meetyourmatch.activity.GroupChatMessagesActivity;
import com.javanostra.meetyourmatch.adapter.ChatAdapter;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.TokenHelper;
import com.javanostra.meetyourmatch.persistance.entity.ChatInfoDTO;
import com.javanostra.meetyourmatch.persistance.entity.ChatUserDTO;
import com.javanostra.meetyourmatch.persistance.entity.Relation; 
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRelationDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; 

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements ChatAdapter.OnChatItemClickListener, SearchUsersBottomSheetFragment.OnUserSelectedListener {

    private static final String TAG = "ChatFragment";

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ImageButton fabNewChat, fabNewGroupChat;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FrameLayout searchContainer;
    private EditText searchEditText;
    private ImageButton clearSearchButton;

    private Long currentAppUserLongId;
    private String currentAppUserId;
    private List<ChatUserDTO> fullChatList = new ArrayList<>();
    private ChatApiService chatApiService;
    private AccountApiService accountApiService;
    private UserApiService userApiService;
    private AtomicInteger pendingRelationRequests = new AtomicInteger(0);
    private AtomicInteger pendingOnlineStatusRequests = new AtomicInteger(0);

    private LocalBroadcastManager localBroadcastManager;
    public static final String ACTION_UPDATE_CHAT_ITEM = "com.javanostra.meetyourmatch.UPDATE_CHAT_ITEM";
    public static final String EXTRA_USER_ID_FOR_UPDATE = "extra_user_id_for_update";
    public static final String EXTRA_LAST_MESSAGE = "extra_last_message";
    public static final String EXTRA_LAST_MESSAGE_TIME = "extra_last_message_time";
    public static final String ACTION_UPDATE_GROUP_CHAT_ITEM = "com.javanostra.meetyourmatch.UPDATE_GROUP_CHAT_ITEM";
    public static final String EXTRA_GROUP_ID_FOR_UPDATE = "extra_group_id_for_update";
    public static final String ACTION_UPDATE_CHAT_LIST_REQUEST = "com.javanostra.meetyourmatch.UPDATE_CHAT_LIST_REQUEST";

    private final BroadcastReceiver chatUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                if (ACTION_UPDATE_CHAT_ITEM.equals(action)) {
                    long userIdToUpdate = intent.getLongExtra(EXTRA_USER_ID_FOR_UPDATE, -1L);
                    String lastMessage = intent.getStringExtra(EXTRA_LAST_MESSAGE);
                    long lastMessageTimeMillis = intent.getLongExtra(EXTRA_LAST_MESSAGE_TIME, 0);

                    if (userIdToUpdate != -1L && lastMessage != null && lastMessageTimeMillis > 0) {
                        Log.d(TAG, "Received broadcast to update PERSONAL chat for user ID: " + userIdToUpdate);
                        updateChatItemInMemory(userIdToUpdate, lastMessage, new Timestamp(lastMessageTimeMillis), false);
                    }
                } else if (ACTION_UPDATE_GROUP_CHAT_ITEM.equals(action)) {
                    long groupIdToUpdate = intent.getLongExtra(EXTRA_GROUP_ID_FOR_UPDATE, -1L);
                    String lastMessage = intent.getStringExtra(EXTRA_LAST_MESSAGE);
                    long lastMessageTimeMillis = intent.getLongExtra(EXTRA_LAST_MESSAGE_TIME, 0);

                    if (groupIdToUpdate != -1L && lastMessage != null && lastMessageTimeMillis > 0) {
                        Log.d(TAG, "Received broadcast to update GROUP chat for group ID: " + groupIdToUpdate);
                        updateChatItemInMemory(groupIdToUpdate, lastMessage, new Timestamp(lastMessageTimeMillis), true);
                    }
                } else if (ACTION_UPDATE_CHAT_LIST_REQUEST.equals(action)) {
                    Log.d(TAG, "Received request to refresh chat list.");
                    fetchChatData();
                }
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        String token = new CookieManager(context).getCookie();
        currentAppUserLongId = TokenHelper.extractUserLongIdFromToken(token);
        if (currentAppUserLongId == null) {
            Log.e(TAG, "Failed to get current user LongID in onAttach");
        }
        currentAppUserId = TokenHelper.extractUsernameFromToken(token);
        if (currentAppUserId == null) {
            Log.e(TAG, "Failed to get current user ID in onAttach");
        }
        chatApiService = RetrofitClient.getRetrofit(context).create(ChatApiService.class);
        userApiService = RetrofitClient.getRetrofit(context).create(UserApiService.class);
        accountApiService = RetrofitClient.getRetrofit(context).create(AccountApiService.class);
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_CHAT_ITEM);
        filter.addAction(ACTION_UPDATE_GROUP_CHAT_ITEM);
        filter.addAction(ACTION_UPDATE_CHAT_LIST_REQUEST);
        localBroadcastManager.registerReceiver(chatUpdateReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(chatUpdateReceiver);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        fabNewChat = view.findViewById(R.id.fab_new_chat);
        fabNewGroupChat = view.findViewById(R.id.fab_new_group_chat);
        progressBar = view.findViewById(R.id.progressBar_chat_list);
        emptyView = view.findViewById(R.id.empty_view_chat_list);
        searchContainer = view.findViewById(R.id.search_container);
        searchEditText = view.findViewById(R.id.et_chat_search);
        clearSearchButton = view.findViewById(R.id.ib_clear_search); 

        setupRecyclerView();
        setupSearch();

        fabNewGroupChat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(intent);
        });
        fabNewChat.setOnClickListener(v -> openNewChatDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - refreshing chat list");
        if (currentAppUserId != null) {
            fetchChatData(); 
        } else {
            showErrorState(getString(R.string.error_cannot_determine_user));
            Log.e(TAG, "Cannot fetch chats in onResume because current user ID is null");
        }
    }

    private void updateChatItemInMemory(long idToUpdate, String newLastMessage, Timestamp newLastMessageTime, boolean isGroupChat) {
        if (!isAdded()) return;

        boolean foundAndUpdated = false;
        List<ChatUserDTO> updatedList = new ArrayList<>();

        synchronized (fullChatList) {
            for (ChatUserDTO chatUser : fullChatList) {
                ChatInfoDTO profile = chatUser.getUserProfile();
                if (profile != null && profile.getId() != null && profile.getId().equals(idToUpdate) && profile.getIsGroup() == isGroupChat) {
                    ChatInfoDTO newProfile = new ChatInfoDTO(
                            profile.getId(),
                            profile.getUsername(),
                            profile.getAvatarPath(),
                            profile.getIsGroup(),
                            newLastMessage,
                            newLastMessageTime
                    );
                    ChatUserDTO updatedChatUser = new ChatUserDTO(newProfile, isGroupChat ? ChatUserDTO.RelationStatus.NONE : chatUser.getRelationStatus());
                    updatedList.add(updatedChatUser);
                    foundAndUpdated = true;
                } else {
                    updatedList.add(chatUser);
                }
            }

            if (foundAndUpdated) {
                fullChatList.clear();
                fullChatList.addAll(updatedList);
                Log.d(TAG, (isGroupChat ? "Group" : "Personal") + " chat item updated in memory. Triggering adapter update.");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        filterAndSortChats(searchEditText.getText().toString());
                    });
                }
            } else {
                Log.d(TAG, (isGroupChat ? "Group" : "Personal") + " ID " + idToUpdate + " not found in current chat list for update via broadcast.");

                fetchChatData(); // TODO: may produce infinite recursion
            }
        }
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(this);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSortChats(s.toString());
                
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override public void afterTextChanged(Editable s) { }
        });

        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
        });
    }

    private void updateAdapterData(List<ChatUserDTO> chatUsersToShow) {
        if (chatAdapter != null) {
            chatAdapter.submitList(chatUsersToShow);
            checkEmptyState(chatUsersToShow.isEmpty(), searchEditText.getText().toString());
        }
    }

    private void checkEmptyState(boolean isEmptyAfterFilter, String currentQuery) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (isEmptyAfterFilter) {
                if (currentQuery != null && !currentQuery.isEmpty()) {
                    emptyView.setText(getString(R.string.no_chats_match_search));
                } else {
                    emptyView.setText(R.string.no_chats_found);
                }
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setText(message);
        emptyView.setVisibility(View.VISIBLE);
        fullChatList.clear();
        if (chatAdapter != null) {
            chatAdapter.submitList(new ArrayList<>());
        }
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }


    private void fetchChatData() {
        if (chatApiService == null || accountApiService == null || !isAdded()) return;
        Log.d(TAG, "Fetching chats (ChatInfoDTO)...");
        showLoadingState();
        // fullChatList.clear();
        pendingRelationRequests.set(0);
        pendingOnlineStatusRequests.set(0);

        chatApiService.getUserChats().enqueue(new Callback<List<ChatInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatInfoDTO>> call, @NonNull Response<List<ChatInfoDTO>> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<ChatInfoDTO> fetchedChats = response.body();
                    Log.d(TAG, "Fetched " + fetchedChats.size() + " ChatInfoDTOs.");

                    List<ChatUserDTO> newOrUpdatedChatUsers = new ArrayList<>();
                    int pendingPersonalChats = 0;

                    for (ChatInfoDTO chatInfo : fetchedChats) {
                        if (chatInfo.getIsGroup()) {
                            newOrUpdatedChatUsers.add(new ChatUserDTO(chatInfo, ChatUserDTO.RelationStatus.NONE));
                        } else {
                            newOrUpdatedChatUsers.add(new ChatUserDTO(chatInfo, ChatUserDTO.RelationStatus.NONE));
                            pendingPersonalChats++;
                        }
                    }

                    synchronized (fullChatList) {
                        List<ChatUserDTO> tempList = new ArrayList<>(newOrUpdatedChatUsers.size());
                        for (ChatUserDTO newChatUser : newOrUpdatedChatUsers) {
                            boolean existingFound = false;
                            for (int i = 0; i < fullChatList.size(); i++) {
                                ChatUserDTO oldChatUser = fullChatList.get(i);
                                if (oldChatUser.getUserProfile() != null && newChatUser.getUserProfile() != null &&
                                        oldChatUser.getUserProfile().getId().equals(newChatUser.getUserProfile().getId()) &&
                                        oldChatUser.getUserProfile().getIsGroup() == newChatUser.getUserProfile().getIsGroup()) {

                                    ChatInfoDTO updatedProfile = newChatUser.getUserProfile();
                                    ChatUserDTO.RelationStatus statusToKeep = newChatUser.getUserProfile().getIsGroup() ?
                                            ChatUserDTO.RelationStatus.NONE :
                                            oldChatUser.getRelationStatus();
                                    tempList.add(new ChatUserDTO(updatedProfile, statusToKeep));
                                    existingFound = true;
                                    break;
                                }
                            }
                            if (!existingFound) {
                                tempList.add(newChatUser);
                            }
                        }

                        fullChatList.retainAll(tempList);

                        List<ChatUserDTO> finalChatList = new ArrayList<>();
                        for (ChatUserDTO newChat : tempList) {
                            finalChatList.add(newChat);
                        }
                        for (ChatUserDTO oldChat : fullChatList) {
                            boolean foundInNew = false;
                            for(ChatUserDTO newChat : tempList) {
                                if (newChat.getUserProfile().getId().equals(oldChat.getUserProfile().getId()) &&
                                        newChat.getUserProfile().getIsGroup() == oldChat.getUserProfile().getIsGroup()) {
                                    foundInNew = true;
                                    break;
                                }
                            }
                            if (!foundInNew) {
                            }
                        }
                        fullChatList.clear();
                        fullChatList.addAll(finalChatList);
                    }

                    if (pendingPersonalChats == 0) {
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> filterAndSortChats(searchEditText.getText().toString()));
                        }
                    } else {
                        pendingRelationRequests.set(pendingPersonalChats);
                        pendingOnlineStatusRequests.set(pendingPersonalChats);
                        for (ChatUserDTO chatUser : newOrUpdatedChatUsers) {
                            if (chatUser.getUserProfile() != null && !chatUser.getUserProfile().getIsGroup()) {
                                fetchRelationForUser(chatUser.getUserProfile());
                                fetchOnlineStatusForUser(chatUser.getUserProfile());
                            }
                        }
                    }
                    if (fetchedChats.isEmpty()) {
                        filterAndSortChats(searchEditText.getText().toString());
                    }

                } else {
                    Log.e(TAG, "Error fetching ChatInfoDTO list: " + response.code() + " - " + response.message());
                    if (response.code() == 404) {
                        fullChatList.clear();
                        filterAndSortChats("");
                    } else {
                        showErrorState(getString(R.string.error_loading_chat_list) + " (" + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatInfoDTO>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Log.e(TAG, "Network error fetching ChatInfoDTO list", t);
                showErrorState(getString(R.string.error_network));
            }
        });
    }

    
    private void fetchRelationForUser(ChatInfoDTO profileToFetchRelationFor) {
        if (accountApiService == null || profileToFetchRelationFor == null || profileToFetchRelationFor.getId() == null || !isAdded()) {
            //checkIfAllRelationsFetched();
            return;
        }

        accountApiService.getUserRelation(profileToFetchRelationFor.getId()).enqueue(new Callback<UserRelationDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserRelationDTO> call, @NonNull Response<UserRelationDTO> response) {
                if (!isAdded()) return; 

                ChatUserDTO.RelationStatus status = ChatUserDTO.RelationStatus.NONE;
                if (response.isSuccessful() && response.body() != null) {
                    UserRelationDTO relation = response.body();

                    if (relation.getMyRelation() == Relation.FRIEND || relation.getUserRelation() == Relation.FRIEND) {
                        status = ChatUserDTO.RelationStatus.FRIEND;
                    } else if (relation.getMyRelation() == Relation.BLOCKED || relation.getUserRelation() == Relation.BLOCKED) {
                        status = ChatUserDTO.RelationStatus.BLOCKED;
                    }
                    Log.d(TAG, "Relation for " + profileToFetchRelationFor.getUsername() + ": " + status);
                } else {
                    Log.w(TAG, "Failed to get relation for " + profileToFetchRelationFor.getUsername() + ": " + response.code());
                }

                synchronized (fullChatList) {
                    for (int i = 0; i < fullChatList.size(); i++) {
                        ChatUserDTO chatUser = fullChatList.get(i);
                        if (chatUser.getUserProfile() != null &&
                                chatUser.getUserProfile().getId().equals(profileToFetchRelationFor.getId()) &&
                                !chatUser.getUserProfile().getIsGroup()) {

                            chatUser.setRelationStatus(status);
                            fullChatList.set(i, chatUser);
                            break;
                        }
                    }
                }
                //checkIfAllRelationsFetched();
            }

            @Override
            public void onFailure(@NonNull Call<UserRelationDTO> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Network error getting relation for " + profileToFetchRelationFor.getUsername(), t);

                //checkIfAllRelationsFetched();
            }
        });
    }
    private void fetchOnlineStatusForUser(ChatInfoDTO profileToFetchOnlineStatusFor) {
        if (userApiService == null || profileToFetchOnlineStatusFor == null || profileToFetchOnlineStatusFor.getId() == null || !isAdded()) {
            checkIfAllOnlineStatusFetched();
            return;
        }

        userApiService.getLastSeenForUser(profileToFetchOnlineStatusFor.getId()).enqueue(
                new Callback<Timestamp>() {
                    @Override
                    public void onResponse(Call<Timestamp> call, Response<Timestamp> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            synchronized (fullChatList) {
                                for (int i = 0; i < fullChatList.size(); i++) {
                                    ChatUserDTO chatUser = fullChatList.get(i);
                                    if (chatUser.getUserProfile() != null &&
                                            chatUser.getUserProfile().getId().equals(profileToFetchOnlineStatusFor.getId()) &&
                                            !chatUser.getUserProfile().getIsGroup()) {

                                        chatUser.setLastSeenAt(response.body());

                                        break;
                                    }
                                }
                            }
                            Log.d(TAG, "Successful getting online status");
                        } else {
                            Log.d(TAG, "Failed to get online status for user");
                        }

                        checkIfAllOnlineStatusFetched();
                    }

                    @Override
                    public void onFailure(Call<Timestamp> call, Throwable throwable) {
                        if (!isAdded()) return;
                        Log.e(TAG, "Network error getting online status for " + profileToFetchOnlineStatusFor.getUsername(), throwable);

                        checkIfAllOnlineStatusFetched();
                    }
                }
        );
    }
    
    private void checkIfAllRelationsFetched() {
        int remaining = pendingRelationRequests.decrementAndGet(); 
        Log.d(TAG, "Relation requests remaining: " + remaining);
        if (remaining == 0 && isAdded()) { 
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Log.d(TAG, "All relations fetched. Filtering and sorting...");
                    filterAndSortChats(searchEditText.getText().toString());
                });
            }
        } else if (remaining < 0) {
            Log.w(TAG, "PendingRelationRequests count became negative!");
        }
    }

    private void checkIfAllOnlineStatusFetched() {
        int remaining = pendingOnlineStatusRequests.decrementAndGet();
        Log.d(TAG, "Online status requests remaining: " + remaining);
        if (remaining == 0 && isAdded()) {

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Log.d(TAG, "All online status fetched. Filtering and sorting...");
                    filterAndSortChats(searchEditText.getText().toString());
                });
            }
        } else if (remaining < 0) {
            Log.w(TAG, "PendingOnlineStatusRequests count became negative!");
        }
    }

    
    private void filterAndSortChats(String query) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        List<ChatUserDTO> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase().trim();

        
        synchronized (fullChatList) {
            if (lowerCaseQuery.isEmpty()) {
                filteredList.addAll(fullChatList);
            } else {
                for (ChatUserDTO chatUser : fullChatList) {
                    if (chatUser.getUserProfile() != null && chatUser.getUserProfile().getUsername() != null &&
                            chatUser.getUserProfile().getUsername().toLowerCase().contains(lowerCaseQuery)) {
                        filteredList.add(chatUser);
                    }
                }
            }
        }
        Log.d(TAG, "Filtering with query '" + query + "'. Filtered size: " + filteredList.size());

        Collections.sort(filteredList, (o1, o2) -> {
            ChatUserDTO.RelationStatus status1 = o1.getRelationStatus();
            ChatUserDTO.RelationStatus status2 = o2.getRelationStatus();
            ChatInfoDTO p1 = o1.getUserProfile();
            ChatInfoDTO p2 = o2.getUserProfile();

            if (status1 == ChatUserDTO.RelationStatus.FRIEND && status2 != ChatUserDTO.RelationStatus.FRIEND) return -1;
            if (status1 != ChatUserDTO.RelationStatus.FRIEND && status2 == ChatUserDTO.RelationStatus.FRIEND) return 1;

            if (status1 != ChatUserDTO.RelationStatus.BLOCKED && status2 != ChatUserDTO.RelationStatus.BLOCKED) {
                Timestamp time1 = (p1 != null) ? p1.getLastMessageTime() : null;
                Timestamp time2 = (p2 != null) ? p2.getLastMessageTime() : null;

                if (time1 != null && time2 != null) {
                    int timeCompare = time2.compareTo(time1);
                    if (timeCompare != 0) return timeCompare;
                } else if (time1 != null) {
                    return -1;
                } else if (time2 != null) {
                    return 1;
                }
            }

            String name1 = (p1 != null && p1.getUsername() != null) ? p1.getUsername() : "";
            String name2 = (p2 != null && p2.getUsername() != null) ? p2.getUsername() : "";
            return name1.compareToIgnoreCase(name2);
        });
        Log.d(TAG, "List sorted. Submitting to adapter.");

        updateAdapterData(filteredList);
    }

    
    @Override
    public void onItemClick(@NonNull ChatUserDTO chatUser) {
        if (chatUser.getUserProfile() == null) return;

        ChatInfoDTO selectedChatInfo = chatUser.getUserProfile();
        Log.d(TAG, "Chat item clicked: " + selectedChatInfo.getUsername() +
                ", IsGroup: " + selectedChatInfo.getIsGroup() +
                ", Status (if personal): " + chatUser.getRelationStatus());

        if (getActivity() == null || !isAdded()) { return; }

        if (selectedChatInfo.getIsGroup()) {
            Log.d(TAG, "Opening GroupChatMessagesActivity for group: " + selectedChatInfo.getUsername());
            Intent intent = new Intent(getActivity(), GroupChatMessagesActivity.class);
            intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_ID, selectedChatInfo.getId());
            intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_NAME, selectedChatInfo.getUsername());
            intent.putExtra(GroupChatMessagesActivity.EXTRA_GROUP_AVATAR_URL, selectedChatInfo.getAvatarPath());

            startActivity(intent);
        } else {
            if (currentAppUserId == null) {
                Log.e(TAG, "Current user ID (username) is null. Cannot open personal chat.");
                Toast.makeText(getContext(), R.string.error_cannot_determine_user, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatUser.getRelationStatus() == ChatUserDTO.RelationStatus.BLOCKED) {
                Log.d(TAG, "User is blocked, opening AccountDetailsActivity");
                Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
                if (selectedChatInfo.getId() == null) {
                    Log.e(TAG, "Cannot open profile, user ID is null for " + selectedChatInfo.getUsername());
                    Toast.makeText(getContext(), R.string.error_missing_user_id, Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(AccountDetailsActivity.EXTRA_USER_ID, selectedChatInfo.getId());
                intent.putExtra(AccountDetailsActivity.EXTRA_USERNAME, selectedChatInfo.getUsername());
                startActivity(intent);
            } else {
                Log.d(TAG, "User is not blocked, opening ChatMessagesActivity");
                Intent intent = new Intent(getActivity(), ChatMessagesActivity.class);
                if (selectedChatInfo.getUsername() == null) {
                    Log.e(TAG, "Cannot open chat, recipient username is null");
                    Toast.makeText(getContext(), R.string.error_missing_username, Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(ChatMessagesActivity.EXTRA_SENDER_ID, currentAppUserId);
                intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_ID, selectedChatInfo.getUsername());
                intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_LONG_ID, selectedChatInfo.getId());
                intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_USERNAME, selectedChatInfo.getUsername());
                intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_IMAGE_URL, selectedChatInfo.getAvatarPath());
                intent.putExtra(ChatMessagesActivity.EXTRA_IS_BLOCKED, chatUser.getRelationStatus() == ChatUserDTO.RelationStatus.BLOCKED);
                startActivity(intent);
            }
        }
    }
    
    @Override
    public void onUserSelectedForDetails(UserProfileDTO selectedUser) {
        if (selectedUser == null || !isAdded()) return;
        Log.d(TAG, "User selected from bottom sheet: " + selectedUser.getUsername());

        
        ChatUserDTO existingChat = null;
        synchronized (fullChatList) {
            for(ChatUserDTO chat : fullChatList) {
                if (chat.getUserProfile() != null && chat.getUserProfile().getId() != null && chat.getUserProfile().getId().equals(selectedUser.getId())) {
                    existingChat = chat;
                    break;
                }
            }
        }

        if (existingChat != null) {
            
            Log.d(TAG, "Existing chat found for selected user. Handling as item click.");
            onItemClick(existingChat);
        } else {
            
            Log.d(TAG, "No existing chat found. Opening profile for user " + selectedUser.getUsername());
            if (getActivity() == null) return;
            Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
            if (selectedUser.getId() != null) {
                intent.putExtra(AccountDetailsActivity.EXTRA_USER_ID, selectedUser.getId());
                intent.putExtra(AccountDetailsActivity.EXTRA_USERNAME, selectedUser.getUsername());
                startActivity(intent);
            } else {
                Log.w(TAG, "Selected user ID is null. Cannot open details without ID.");
                Toast.makeText(getContext(), R.string.error_missing_user_id, Toast.LENGTH_SHORT).show();
            }
        }
    }

    
    private void openNewChatDialog() {
        if (getContext() == null || !isAdded()) return;
        SearchUsersBottomSheetFragment bottomSheet = SearchUsersBottomSheetFragment.newInstance();
        bottomSheet.setOnUserSelectedListener(this);
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }
}