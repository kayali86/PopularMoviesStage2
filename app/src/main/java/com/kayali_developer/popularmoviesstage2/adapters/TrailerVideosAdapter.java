package com.kayali_developer.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.data.model.TrailerVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerVideosAdapter extends RecyclerView.Adapter<TrailerVideosAdapter.TrailerVideosAdapterViewHolder> {
    private List<TrailerVideo> currentMovieTrailerVideos;
    private final TrailerVideosAdapterOnClickHandler mClickHandler;

    // Declare OnclickHandler interface
    public interface TrailerVideosAdapterOnClickHandler {
        void onClick(String currentTrailerVideoKey);
    }

    // Constructor passes a clickHandler
    public TrailerVideosAdapter(TrailerVideosAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerVideosAdapterViewHolder holder, int position, @NonNull List<Object> payloads) {

        super.onBindViewHolder(holder, position, payloads);
    }

    @NonNull
    @Override
    public TrailerVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_videos_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // Inflate an item view and return a new viewHolder
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerVideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerVideosAdapterViewHolder holder, int position) {
        Context context = holder.trailerVideoView.getContext();
        TrailerVideo currentTrailerVideo = currentMovieTrailerVideos.get(position);
        String trailerVideosThumbnailUrl = "https://img.youtube.com/vi/" + currentTrailerVideo.getKey() + "/hqdefault.jpg";
        Picasso.with(context)
                .load(trailerVideosThumbnailUrl)
                .placeholder(R.drawable.no_poster)
                .into(holder.trailerVideoView);

    }

    @Override
    public int getItemCount() {
        if (currentMovieTrailerVideos == null) {
            return 0;
        }
        return currentMovieTrailerVideos.size();
    }

    // ViewHolder class implements an OnclickListener
    class TrailerVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView trailerVideoView;

        TrailerVideosAdapterViewHolder(View itemView) {
            super(itemView);
            trailerVideoView = itemView.findViewById(R.id.iv_trailer_video_item);
            itemView.setOnClickListener(this);
        }

        @Override
        // When listItem clicked
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            TrailerVideo currentTrailerVideo = currentMovieTrailerVideos.get(adapterPosition);
            String currentTrailerVideoKey = currentTrailerVideo.getKey();
            mClickHandler.onClick(currentTrailerVideoKey);
        }
    }

    // Set data to adapter an make the recyclerView notify new data
    public void setTrailerVideosData(List<TrailerVideo> trailerVideosData) {
        currentMovieTrailerVideos = trailerVideosData;
        notifyDataSetChanged();
    }
}