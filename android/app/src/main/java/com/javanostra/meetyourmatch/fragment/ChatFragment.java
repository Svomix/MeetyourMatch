package com.javanostra.meetyourmatch.fragment;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.activity.AccountDetailsActivity;
import com.javanostra.meetyourmatch.activity.ChatMessagesActivity;
import com.javanostra.meetyourmatch.adapter.ChatAdapter;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.AccountApiService;
import com.javanostra.meetyourmatch.persistance.api_service.ChatApiService;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;
import com.javanostra.meetyourmatch.persistance.cookie.TokenHelper;
import com.javanostra.meetyourmatch.persistance.entity.ChatUserDTO;
import com.javanostra.meetyourmatch.persistance.entity.Relation; 
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;
import com.javanostra.meetyourmatch.persistance.entity.UserRelationDTO;

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
    private ImageButton fabNewChat;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FrameLayout searchContainer;
    private EditText searchEditText;
    private ImageButton clearSearchButton;

    private String currentAppUserId;
    private List<ChatUserDTO> fullChatList = new ArrayList<>();
    private ChatApiService chatApiService;
    private AccountApiService accountApiService;
    private AtomicInteger pendingRelationRequests = new AtomicInteger(0); 

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        currentAppUserId = TokenHelper.extractUsernameFromToken(new CookieManager(context).getCookie());
        if (currentAppUserId == null) {
            Log.e(TAG, "Failed to get current user ID in onAttach");
        }
        chatApiService = RetrofitClient.getRetrofit(context).create(ChatApiService.class);
        accountApiService = RetrofitClient.getRetrofit(context).create(AccountApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        fabNewChat = view.findViewById(R.id.fab_new_chat);
        progressBar = view.findViewById(R.id.progressBar_chat_list);
        emptyView = view.findViewById(R.id.empty_view_chat_list);
        searchContainer = view.findViewById(R.id.search_container);
        searchEditText = view.findViewById(R.id.et_chat_search);
        clearSearchButton = view.findViewById(R.id.ib_clear_search); 

        setupRecyclerView();
        setupSearch(); 

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
        Log.d(TAG, "Fetching chats (UserProfileDTO) and relations separately...");
        showLoadingState();
        fullChatList.clear(); 
        pendingRelationRequests.set(0); 

        chatApiService.getUserChats().enqueue(new Callback<List<UserProfileDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserProfileDTO>> call, @NonNull Response<List<UserProfileDTO>> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<UserProfileDTO> profiles = response.body();
                    Log.d(TAG, "Fetched " + profiles.size() + " UserProfileDTOs. Now fetching relations...");
                    if (profiles.isEmpty()) {
                        filterAndSortChats(searchEditText.getText().toString()); 
                        return;
                    }

                    pendingRelationRequests.set(profiles.size()); 
                    for (UserProfileDTO profile : profiles) {
                        fetchRelationForUser(profile); 
                    }
                } else {
                    Log.e(TAG, "Error fetching UserProfileDTO list: " + response.code() + " - " + response.message());
                    if (response.code() == 404) {
                        filterAndSortChats(""); 
                    } else {
                        showErrorState(getString(R.string.error_loading_chat_list) + " (" + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserProfileDTO>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Log.e(TAG, "Network error fetching UserProfileDTO list", t);
                showErrorState(getString(R.string.error_network));
            }
        });
    }

    
    private void fetchRelationForUser(UserProfileDTO profile) {
        if (accountApiService == null || profile == null || profile.getId() == null || !isAdded()) {
            checkIfAllRelationsFetched(); 
            return;
        }

        accountApiService.getUserRelation(profile.getId()).enqueue(new Callback<UserRelationDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserRelationDTO> call, @NonNull Response<UserRelationDTO> response) {
                if (!isAdded()) return; 

                ChatUserDTO.RelationStatus status = ChatUserDTO.RelationStatus.NONE;
                if (response.isSuccessful() && response.body() != null) {
                    UserRelationDTO relation = response.body();
                    Relation myRelation = relation.getMyRelation();
                    Relation userRelation = relation.getUserRelation();

                    if (myRelation == Relation.FRIEND || userRelation == Relation.FRIEND) {
                        status = ChatUserDTO.RelationStatus.FRIEND;
                    } else if (myRelation == Relation.BLOCKED || userRelation == Relation.BLOCKED) {
                        status = ChatUserDTO.RelationStatus.BLOCKED;
                    }
                    Log.d(TAG, "Relation for " + profile.getUsername() + ": " + status);
                } else {
                    Log.w(TAG, "Failed to get relation for " + profile.getUsername() + ": " + response.code());
                }

                
                
                synchronized (fullChatList) {
                    fullChatList.add(new ChatUserDTO(profile, status));
                }
                checkIfAllRelationsFetched();
            }

            @Override
            public void onFailure(@NonNull Call<UserRelationDTO> call, @NonNull Throwable t) {
                if (!isAdded()) return; 
                Log.e(TAG, "Network error getting relation for " + profile.getUsername(), t);
                synchronized (fullChatList) {
                    fullChatList.add(new ChatUserDTO(profile, ChatUserDTO.RelationStatus.NONE));
                }
                checkIfAllRelationsFetched();
            }
        });
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
            UserProfileDTO p1 = o1.getUserProfile();
            UserProfileDTO p2 = o2.getUserProfile();
            String name1 = (p1 != null && p1.getUsername() != null) ? p1.getUsername() : "";
            String name2 = (p2 != null && p2.getUsername() != null) ? p2.getUsername() : "";

            if (status1 == ChatUserDTO.RelationStatus.FRIEND && status2 != ChatUserDTO.RelationStatus.FRIEND) return -1;
            if (status1 != ChatUserDTO.RelationStatus.FRIEND && status2 == ChatUserDTO.RelationStatus.FRIEND) return 1;
            return name1.compareToIgnoreCase(name2);
        });
        Log.d(TAG, "List sorted. Submitting to adapter.");

        updateAdapterData(filteredList);
    }

    
    @Override
    public void onItemClick(@NonNull ChatUserDTO chatUser) {
        if (chatUser.getUserProfile() == null) return;

        UserProfileDTO selectedUser = chatUser.getUserProfile();
        Log.d(TAG, "Chat item clicked: " + selectedUser.getUsername() + ", Status: " + chatUser.getRelationStatus());

        if (getActivity() == null || currentAppUserId == null || !isAdded()) { return; }

        if (chatUser.getRelationStatus() == ChatUserDTO.RelationStatus.BLOCKED) {
            Log.d(TAG, "User is blocked, opening AccountDetailsActivity");
            Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
            if (selectedUser.getId() == null) {
                Log.e(TAG, "Cannot open profile, user ID is null for " + selectedUser.getUsername());
                Toast.makeText(getContext(), R.string.error_missing_user_id, Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(AccountDetailsActivity.EXTRA_USER_ID, selectedUser.getId());
            intent.putExtra(AccountDetailsActivity.EXTRA_USERNAME, selectedUser.getUsername());
            startActivity(intent);
        } else {
            Log.d(TAG, "User is not blocked, opening ChatMessagesActivity");
            Intent intent = new Intent(getActivity(), ChatMessagesActivity.class);
            if (selectedUser.getUsername() == null) {
                Log.e(TAG, "Cannot open chat, recipient username is null");
                Toast.makeText(getContext(), R.string.error_missing_username, Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(ChatMessagesActivity.EXTRA_SENDER_ID, currentAppUserId);
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_ID, selectedUser.getUsername());
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_LONG_ID, selectedUser.getId());
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_USERNAME, selectedUser.getUsername());
            intent.putExtra(ChatMessagesActivity.EXTRA_RECIPIENT_IMAGE_URL, selectedUser.getAvatarPath());
            intent.putExtra(ChatMessagesActivity.EXTRA_IS_BLOCKED, chatUser.getRelationStatus() == ChatUserDTO.RelationStatus.BLOCKED);
            startActivity(intent);
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