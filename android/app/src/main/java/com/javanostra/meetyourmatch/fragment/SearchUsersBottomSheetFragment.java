package com.javanostra.meetyourmatch.fragment; 

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.UserSearchAdapter; 
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.PagedResponse;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.UserProfileDTO;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUsersBottomSheetFragment extends BottomSheetDialogFragment implements UserSearchAdapter.OnUserClickListener {

    private static final String TAG = "SearchUsersBSFragment";

    private EditText searchEditText;
    private RecyclerView usersRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;

    private UserSearchAdapter userSearchAdapter;
    private List<UserProfileDTO> allUsersList = new ArrayList<>(); 

    private OnUserSelectedListener userSelectedListener;

    
    public interface OnUserSelectedListener {
        void onUserSelectedForDetails(UserProfileDTO user);
        
    }

    
    public static SearchUsersBottomSheetFragment newInstance() {
        return new SearchUsersBottomSheetFragment();
    }

    
    public void setOnUserSelectedListener(OnUserSelectedListener listener) {
        this.userSelectedListener = listener;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        
        
        if (userSelectedListener == null) { 
            if (getParentFragment() instanceof OnUserSelectedListener) {
                userSelectedListener = (OnUserSelectedListener) getParentFragment();
            } else if (context instanceof OnUserSelectedListener) {
                userSelectedListener = (OnUserSelectedListener) context;
            } else if (getTargetFragment() instanceof OnUserSelectedListener) {
                userSelectedListener = (OnUserSelectedListener) getTargetFragment();
            }
            else {
                Log.w(TAG, context.toString() + " or parent/target fragment must implement OnUserSelectedListener");
                
                
                
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        
        userSelectedListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.fragment_search_users_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        
        searchEditText = view.findViewById(R.id.editTextSearchUsers);
        usersRecyclerView = view.findViewById(R.id.recyclerViewAllUsers);
        progressBar = view.findViewById(R.id.progressBarSearchUsers);
        emptyStateTextView = view.findViewById(R.id.textViewEmptyState);

        
        setupRecyclerView();

        
        setupSearchListener();

        
        fetchAllUsers();
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        
        userSearchAdapter = new UserSearchAdapter(this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(userSearchAdapter);
        




    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                if (userSearchAdapter != null) {
                    userSearchAdapter.filter(s.toString());
                }
                
                updateEmptyStateVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAllUsers() {
        if (getContext() == null || !isAdded()) return;

        setLoadingState(true); 

        
        UserApiService apiService = RetrofitClient.getRetrofit(getContext()).create(UserApiService.class);

        
        
        apiService.getAllUsers(1, 500, "") 
                .enqueue(new Callback<PagedResponse<UserProfileDTO>>() {
                    @Override
                    public void onResponse(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Response<PagedResponse<UserProfileDTO>> response) {
                        if (!isAdded()) return; 
                        setLoadingState(false); 

                        if (response.isSuccessful() && response.body() != null) {
                            allUsersList = response.body().getContent(); 
                            if (userSearchAdapter != null) {
                                userSearchAdapter.setUsers(allUsersList); 
                                userSearchAdapter.filter(searchEditText.getText().toString()); 
                            }
                            Log.d(TAG, "Fetched " + allUsersList.size() + " total users.");
                        } else {
                            
                            allUsersList.clear();
                            if (userSearchAdapter != null) userSearchAdapter.setUsers(allUsersList); 
                            Log.e(TAG, "Failed to fetch users: " + response.code() + " - " + response.message());
                            Toast.makeText(getContext(), R.string.error_loading_users, Toast.LENGTH_SHORT).show();
                        }
                        
                        updateEmptyStateVisibility();
                    }

                    @Override
                    public void onFailure(@NonNull Call<PagedResponse<UserProfileDTO>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setLoadingState(false);
                        allUsersList.clear();
                        if (userSearchAdapter != null) userSearchAdapter.setUsers(allUsersList);
                        Log.e(TAG, "Network error fetching users", t);
                        Toast.makeText(getContext(), R.string.error_network_loading_users, Toast.LENGTH_SHORT).show();
                        
                        updateEmptyStateVisibility();
                    }
                });
    }

    
    private void setLoadingState(boolean isLoading) {
        if (!isAdded()) return; 

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        searchEditText.setEnabled(!isLoading); 

        
        if (isLoading) {
            usersRecyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            
            updateEmptyStateVisibility();
        }
    }

    
    private void updateEmptyStateVisibility() {
        if (userSearchAdapter == null || progressBar.getVisibility() == View.VISIBLE) {
            
            usersRecyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.GONE);
            return;
        }

        boolean isListEmpty = userSearchAdapter.getFilteredList().isEmpty();
        emptyStateTextView.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
        usersRecyclerView.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
    }


    
    @Override
    public void onUserClick(UserProfileDTO user) {
        Log.d(TAG, "User clicked: " + user.getUsername() + " (ID: " + user.getId() + ")");
        if (userSelectedListener != null) {
            
            userSelectedListener.onUserSelectedForDetails(user);
        } else {
            Log.e(TAG, "OnUserSelectedListener is null, cannot pass user selection back.");
            
            
        }
        
        dismiss();
    }
}