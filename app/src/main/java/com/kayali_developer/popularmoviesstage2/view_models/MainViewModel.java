package com.kayali_developer.popularmoviesstage2.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.kayali_developer.popularmoviesstage2.BuildConfig;
import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.data.AppDatabase;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.utilities.MovieJSONUtils;
import com.kayali_developer.popularmoviesstage2.utilities.MovieNetworkUtils;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private Context context;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String REQUEST_URL = "https://api.themoviedb.org/3";
    private MutableLiveData<List<Movie>> moviesFromWebService = new MutableLiveData<>();
    private LiveData<List<Movie>> moviesFromDb;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        context = application.getApplicationContext();
        moviesFromDb = db.movieDao().loadAllMovies();
        // Load movies from API and set the retrieved value to MutableLiveData object
        loadMovies();
    }

    // Helper method to allow getting movies that retrieved from database
    public LiveData<List<Movie>> getMoviesFromDb() {
        return moviesFromDb;
    }

    // Helper method to allow getting movies that retrieved from API
    public LiveData<List<Movie>> getMoviesFromWebService() {
        return moviesFromWebService;
    }

    // Set the retrieved Movies value to MutableLiveData object
    private void setLoadedMoviesToLiveDataObject(List<Movie> movies) {
        this.moviesFromWebService.setValue(movies);
    }

    // Load movies from API and set the retrieved value to MutableLiveData object
    private void loadMovies() {
        if (MovieNetworkUtils.isConnected(context)) {
            // Sort by preferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String sortBy = sharedPrefs.getString(
                    context.getString(R.string.settings_sort_by_key),
                    context.getString(R.string.settings_sort_by_default));
            if (sortBy.equals(context.getString(R.string.settings_sort_by_most_popular_value)) || sortBy.equals(context.getString(R.string.settings_sort_by_highest_rated_value))) {
                // Parse Url
                Uri baseUri = Uri.parse(REQUEST_URL);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendEncodedPath(sortBy);
                uriBuilder.appendQueryParameter("api_key", API_KEY);
                MovieAsyncTask task = new MovieAsyncTask();
                task.execute(uriBuilder.toString());
            }
        }
    }

    // AsyncTask to load movies asynchronously
    private class MovieAsyncTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return MovieJSONUtils.extractMoviesData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                // Set the retrieved Movies value to MutableLiveData object
                setLoadedMoviesToLiveDataObject(movies);
            }
        }
    }
}
