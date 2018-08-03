package com.kayali_developer.popularmoviesstage2.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.kayali_developer.popularmoviesstage2.activities.MainActivity;
import com.kayali_developer.popularmoviesstage2.data.AppDatabase;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.data.model.Review;
import com.kayali_developer.popularmoviesstage2.data.model.TrailerVideo;
import com.kayali_developer.popularmoviesstage2.utilities.MovieJSONUtils;
import com.kayali_developer.popularmoviesstage2.utilities.MovieNetworkUtils;

import java.util.List;

public class MovieDetailsViewModel extends ViewModel {
    private Context context;
    private Movie currentMovie;
    private LiveData<Movie> currentMovieFromDb;
    private LiveData<List<Review>> currentMovieReviewsFromDb;
    // LiveData boolean to observe if movie is favorite
    private LiveData<Boolean> isFavorite;
    private MutableLiveData<List<TrailerVideo>> currentMovieTrailerVideosFromWebService = new MutableLiveData<>();
    private MutableLiveData<List<Review>> currentMovieReviewsFromWebService = new MutableLiveData<>();

    MovieDetailsViewModel(AppDatabase database, Movie currentMovie, Context context) {
        this.context = context;
        this.currentMovie = currentMovie;
        currentMovieFromDb = database.movieDao().loadMovieById(currentMovie.getId());
        currentMovieReviewsFromDb = database.reviewDao().loadReviewsByMovieId(currentMovie.getId());
        isFavorite = database.movieDao().isFavorite(currentMovie.getId());
        loadTrailerVideos();
        loadReviews();
    }

    private void loadTrailerVideos() {
        if (MovieNetworkUtils.isConnected(context)) {
            // Parse Url
            Uri baseUri = Uri.parse(MainActivity.REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendEncodedPath("movie/" + currentMovie.getId() + "/videos");
            uriBuilder.appendQueryParameter("api_key", MainActivity.API_KEY);
            TrailerVideoAsyncTask trailerVideoAsyncTask = new TrailerVideoAsyncTask();
            trailerVideoAsyncTask.execute(uriBuilder.toString(), String.valueOf(currentMovie.getId()));
        }
    }

    private void loadReviews() {
        if (MovieNetworkUtils.isConnected(context)) {
            // Parse Url
            Uri baseUri = Uri.parse(MainActivity.REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendEncodedPath("movie/" + currentMovie.getId() + "/reviews");
            uriBuilder.appendQueryParameter("api_key", MainActivity.API_KEY);
            ReviewAsyncTask reviewAsyncTask = new ReviewAsyncTask();
            reviewAsyncTask.execute(uriBuilder.toString(), String.valueOf(currentMovie.getId()));
        }
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }

    public LiveData<Movie> getMovieFromDb() {
        return currentMovieFromDb;
    }

    public LiveData<List<Review>> getReviewsFromDb() {
        return currentMovieReviewsFromDb;
    }

    public LiveData<List<TrailerVideo>> getMovieTrailerVideosFromWebService() {
        return currentMovieTrailerVideosFromWebService;
    }

    public LiveData<List<Review>> getMovieReviewsFromWebService() {
        return currentMovieReviewsFromWebService;
    }

    // Helper method to allow getting isFavorite variable's value to check if movie is Favorite
    public LiveData<Boolean> isFavorite() {
        return isFavorite;
    }

    // AsyncTask to load trailers asynchronously
    private class TrailerVideoAsyncTask extends AsyncTask<String, Void, List<TrailerVideo>> {
        @Override
        protected List<TrailerVideo> doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            return MovieJSONUtils.extractTrailerVideoData(strings[0], Integer.parseInt(strings[1]));
        }

        @Override
        protected void onPostExecute(List<TrailerVideo> trailerVideos) {
            if (trailerVideos != null && !trailerVideos.isEmpty()) {
                setTrailerVideosToLiveDataObject(trailerVideos);
            }
        }
    }

    // Set the retrieved Trailers value to MutableLiveData object
    private void setTrailerVideosToLiveDataObject(List<TrailerVideo> trailerVideos) {
        this.currentMovieTrailerVideosFromWebService.setValue(trailerVideos);
    }

    // AsyncTask to load reviews asynchronously
    private class ReviewAsyncTask extends AsyncTask<String, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            return MovieJSONUtils.extractReviewData(strings[0], Integer.parseInt(strings[1]));
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                setReviewsToLiveDataObject(reviews);
            }
        }
    }

    // Set the retrieved reviews value to MutableLiveData object
    private void setReviewsToLiveDataObject(List<Review> reviews) {
        this.currentMovieReviewsFromWebService.setValue(reviews);
    }


}
