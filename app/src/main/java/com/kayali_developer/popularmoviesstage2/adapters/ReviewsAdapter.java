package com.kayali_developer.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.data.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private List<Review> currentMovieReviews;
    private final ReviewsAdapterOnClickHandler mClickHandler;

    // Declare OnclickHandler interface
    public interface ReviewsAdapterOnClickHandler {
        void onClick(String currentReviewUrl);
    }

    // Constructor passes a clickHandler
    public ReviewsAdapter(ReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // Inflate an item view and return a new viewHolder
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
        Review currentReview = currentMovieReviews.get(position);
        holder.reviewAuthorView.setText(currentReview.getAuthor());
        holder.reviewContentView.setText(currentReview.getContent());
    }

    @Override
    public int getItemCount() {
        if (currentMovieReviews == null) {
            return 0;
        }
        return currentMovieReviews.size();
    }

    // ViewHolder class implements an OnclickListener
    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView reviewAuthorView, reviewContentView;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            reviewAuthorView = itemView.findViewById(R.id.tv_review_author);
            reviewContentView = itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }
        @Override
        // When listItem clicked
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review currentReview = currentMovieReviews.get(adapterPosition);
            String currentReviewUrl = currentReview.getUrl();
            mClickHandler.onClick(currentReviewUrl);
        }
    }
    // Set data to adapter an make the recyclerView notify new data
    public void setReviewsData(List<Review> reviewsData) {
        currentMovieReviews = reviewsData;
        notifyDataSetChanged();
    }
}