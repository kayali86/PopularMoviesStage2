package com.kayali_developer.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    // Base Url to be appended to images Url
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private List<Movie> movies;
    private final MovieAdapterOnClickHandler mClickHandler;

    // Declare OnclickHandler interface
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie currentMovie);
    }

    // Constructor passes a clickHandler
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // Inflate an item view and return a new viewHolder
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Context context = holder.posterView.getContext();
        Movie currentMovie = movies.get(position);
        String posterPath = POSTER_BASE_URL + currentMovie.getPosterPath();
        Picasso.with(context)
                .load(posterPath)
                .placeholder(R.drawable.no_poster)
                .into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    // ViewHolder class implements an OnclickListener
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.iv_poster_item);
            itemView.setOnClickListener(this);
        }
        @Override
        // When listItem clicked
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = movies.get(adapterPosition);
            mClickHandler.onClick(currentMovie);
        }
    }
    // Set data to adapter an make the recyclerView notify new data
    public void setMoviesData(List<Movie> moviesData) {
        movies = moviesData;
        notifyDataSetChanged();
    }
}