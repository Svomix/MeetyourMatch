package com.javanostra.meetyourmatch.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.EventApiService;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.CommentDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.adapter.CommentsAdapter;
import com.javanostra.meetyourmatch.persistance.entity.CommentDTO;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.api_service.UserApiService;
import com.javanostra.meetyourmatch.persistance.entity.CommentRequestDTO;
import com.javanostra.meetyourmatch.persistance.entity.FullEventDTO;


public class CommentsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_USER_ID = "user_id";
    private static final String TAG = "CommentsBottomSheet";

    private RecyclerView recyclerViewComments;
    private CommentsAdapter commentsAdapter;
    private TextView textViewNoComments;
    private ProgressBar progressBarComments; // TODO
    private EditText editTextCommentInput;
    private ImageButton buttonSendComment;

    private Long eventId;

    public static CommentsBottomSheetFragment newInstance(Long eventId) {
        CommentsBottomSheetFragment fragment = new CommentsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
        if (getArguments() != null) {
            eventId = getArguments().getLong(ARG_EVENT_ID);
        }

        if (eventId == null || eventId <= 0) {
            Log.e(TAG, "Event ID (" + eventId + ") is missing or invalid!");
            Toast.makeText(getContext(), "Error: Cannot load comments section.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_bottom_sheet, container, false);

        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        textViewNoComments = view.findViewById(R.id.textViewNoComments);
        progressBarComments = view.findViewById(R.id.progressBarComments);
        editTextCommentInput = view.findViewById(R.id.editTextCommentInput);
        buttonSendComment = view.findViewById(R.id.buttonSendComment);

        setupRecyclerView();
        setupSendButtonListener();

        fetchComments(eventId);

        return view;
    }

    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter(requireContext());
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewComments.setAdapter(commentsAdapter);
    }

    private void setupSendButtonListener() {
        editTextCommentInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                buttonSendComment.performClick();
                return true;
            }
            return false;
        });

        buttonSendComment.setOnClickListener(v -> {
            String commentText = editTextCommentInput.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            hideKeyboard(v);
            buttonSendComment.setEnabled(false);
            editTextCommentInput.setEnabled(false);

            CommentRequestDTO request = new CommentRequestDTO(commentText);

            postComment(eventId, request);
        });
    }

    private void postComment(Long eventId, CommentRequestDTO request) {
        EventApiService apiService = RetrofitClient.getRetrofit(requireContext()).create(EventApiService.class);
        apiService.addComment(eventId, request).enqueue(new Callback<CommentDTO>() {
            @Override
            public void onResponse(@NonNull Call<CommentDTO> call, @NonNull Response<CommentDTO> response) {
                buttonSendComment.setEnabled(true);
                editTextCommentInput.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    CommentDTO newComment = response.body();
                    Log.d(TAG, "Comment posted successfully. ID: " + newComment.getId());

                    editTextCommentInput.setText("");

                    showEmptyState(false);
                    commentsAdapter.addComment(newComment);
                    recyclerViewComments.scrollToPosition(commentsAdapter.getItemCount() - 1);

                } else {
                    Log.e(TAG, "Failed to post comment. Code: " + response.code() + ", Message: " + response.message());
                    showError("Failed to post comment. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentDTO> call, @NonNull Throwable t) {
                buttonSendComment.setEnabled(true);
                editTextCommentInput.setEnabled(true);
                Log.e(TAG, "Failed to post comment. Error: " + t.getMessage(), t);
                showError("Network error. Please try again.");
            }
        });
    }

    private void fetchComments(Long eventId) {
        showLoading(true);
        textViewNoComments.setVisibility(View.GONE);

        EventApiService apiService = RetrofitClient.getRetrofit(requireContext()).create(EventApiService.class);
        apiService.getFullEventDTO(eventId).enqueue(new Callback<FullEventDTO>() {
            @Override
            public void onResponse(@NonNull Call<FullEventDTO> call, @NonNull Response<FullEventDTO> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<CommentDTO> comments = response.body().getComments();
                    if (comments.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                        commentsAdapter.setComments(comments);
                    }
                    Log.d(TAG, "Comments fetched successfully: " + comments.size());
                } else {
                    Log.e(TAG, "Failed to fetch comments. Code: " + response.code() + ", Message: " + response.message());
                    showError("Failed to load comments. Code: " + response.code());
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FullEventDTO> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Failed to fetch comments. Error: " + t.getMessage(), t);
                showError("Network error. Please try again.");
                showEmptyState(true);
            }
        });
    }

    private void showLoading(boolean isLoading) {
    }

    private void showEmptyState(boolean show) {
        recyclerViewComments.setVisibility(show ? View.GONE : View.VISIBLE);
        textViewNoComments.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
    }

    private void hideKeyboard(View view) {
        if (view != null && getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}