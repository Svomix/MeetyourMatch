package com.javanostra.meetyourmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.CommentDTO;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<CommentDTO> comments = new ArrayList<>();
    private final SimpleDateFormat dateFormat;

    public CommentsAdapter(Context context) {
        dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    }

    public void setComments(List<CommentDTO> newComments) {
        this.comments.clear();
        if (newComments != null) {
            this.comments.addAll(newComments);
        }
        notifyDataSetChanged(); // TODO: DiffUtil
    }

    public void addComment(CommentDTO newComment) {
        if (newComment != null) {
            //comments.add(0, newComment); // Add to top
            comments.add(newComment); // add to bottom
            //notifyItemInserted(0); // Notify at top
            notifyItemInserted(comments.size() - 1); // Notify at bottom
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentDTO comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView commentDateTextView;
        TextView commentContentTextView;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            commentDateTextView = itemView.findViewById(R.id.textViewCommentDate);
            commentContentTextView = itemView.findViewById(R.id.textViewCommentContent);
        }

        void bind(CommentDTO comment) {
            if (comment.getUser() != null) {
                userNameTextView.setText(comment.getUser().getUsername());
            } else {
                userNameTextView.setText("Unknown User");
            }

            if (comment.getDate() != null) {
                commentDateTextView.setText(formatTimestamp(comment.getDate()));
            } else {
                commentDateTextView.setText("");
            }

            commentContentTextView.setText(comment.getContent());
        }

        private String formatTimestamp(Timestamp timestamp) {
            if (timestamp == null) return "";
            try {
                return dateFormat.format(timestamp);
            } catch (Exception e) {
                return "";
            }
        }
    }
}