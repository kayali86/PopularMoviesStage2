package com.kayali_developer.popularmoviesstage2.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.kayali_developer.popularmoviesstage2.BuildConfig;
import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.adapters.MovieAdapter;
import com.kayali_developer.popularmoviesstage2.data.AppDatabase;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.utilities.MovieNetworkUtils;
import com.kayali_developer.popularmoviesstage2.view_models.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler {
    private Context context;
    // API Key - You can put your own KEY in gradle.properties file
    public static final String API_KEY = BuildConfig.API_KEY;
    // Key to retrieve current movie from Intent
    public static final String CURRENT_MOVIE_PARCELABLE_KEY = "current_movie";
    // Constant to specify poster width in grid view
    private static final int POSTER_WIDTH = 700;
    public static final String REQUEST_URL = "https://api.themoviedb.org/3";
    private RecyclerView moviesRecyclerView;
    private MovieAdapter movieAdapter;
    // Empty View to display when there is no data to display
    private View emptyView;
    // Instance to database
    AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        emptyView = findViewById(R.id.empty_view);
        moviesRecyclerView = findViewById(R.id.movies_recycler_view);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, calculateBestSpanCount(POSTER_WIDTH));
        moviesRecyclerView.setLayoutManager(layoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this);
        moviesRecyclerView.setAdapter(movieAdapter);
        mDb = AppDatabase.getInstance(context);
        // Setup MainViewModel to get data from API or DB and set data to MoviesAdapter
        setupViewModel();
    }

    // Helper Method to make the layout more responsive by controlling poster width
    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

    // Helper Method to hide emptyView and show recyclerView to display retrieved data
    private void showMoviesDataView() {
        emptyView.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    // Helper Method to hide recyclerView and show emptyView
    private void showErrorMessage() {
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }

    // If an Item clicked - start DetailsActivity and pass bundle as extra using an Intent
    @Override
    public void onClick(Movie currentMovie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(CURRENT_MOVIE_PARCELABLE_KEY, currentMovie);
        startActivity(intent);
    }

    // Setup MainViewModel to get data from API or DB and set data to MoviesAdapter
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = sharedPrefs.getString(
                context.getString(R.string.settings_sort_by_key),
                context.getString(R.string.settings_sort_by_default));
        if (MovieNetworkUtils.isConnected(context)) {
            loadDataInOnlineMode(viewModel, sortBy);
        } else {
            loadFavorites(sortBy, viewModel);
        }
    }

    // Load data from ViewModel when no internet connection
    private void loadDataInOnlineMode(MainViewModel viewModel, String sortBy) {
        if (sortBy.equals(context.getString(R.string.settings_sort_by_most_popular_value)) || sortBy.equals(context.getString(R.string.settings_sort_by_highest_rated_value))) {
            showMoviesDataView();
            viewModel.getMoviesFromWebService().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    movieAdapter.setMoviesData(movies);
                    movieAdapter.notifyDataSetChanged();
                }
            });
        } else {
            loadFavorites(sortBy, viewModel);
        }
    }

    // Load favorite movie's data from viewModel which retrieved from DB
    private void loadFavorites(String sortBy, MainViewModel viewModel) {
        if (sortBy.equals(context.getString(R.string.settings_sort_by_favorites_value))) {
            showMoviesDataView();
            viewModel.getMoviesFromDb().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    if (movies != null && movies.size() > 0) {
                        movieAdapter.setMoviesData(movies);
                    } else {
                        showErrorMessage();
                    }
                }
            });
        } else {
            showErrorMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
