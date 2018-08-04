package com.kayali_developer.popularmoviesstage2.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kayali_developer.popularmoviesstage2.AppExecutors;
import com.kayali_developer.popularmoviesstage2.R;
import com.kayali_developer.popularmoviesstage2.adapters.ReviewsAdapter;
import com.kayali_developer.popularmoviesstage2.adapters.ReviewsAdapter.ReviewsAdapterOnClickHandler;
import com.kayali_developer.popularmoviesstage2.adapters.TrailerVideosAdapter;
import com.kayali_developer.popularmoviesstage2.adapters.TrailerVideosAdapter.TrailerVideosAdapterOnClickHandler;
import com.kayali_developer.popularmoviesstage2.data.AppDatabase;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.data.model.Review;
import com.kayali_developer.popularmoviesstage2.data.model.TrailerVideo;
import com.kayali_developer.popularmoviesstage2.utilities.MovieNetworkUtils;
import com.kayali_developer.popularmoviesstage2.view_models.MovieDetailsViewModel;
import com.kayali_developer.popularmoviesstage2.view_models.MovieDetailsViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    // Base Url to be appended to images Url
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private Context context = this;
    private AppDatabase mDb;
    // Declare ViewModel
    private MovieDetailsViewModel viewModel;
    private TrailerVideosAdapter trailerVideosAdapter;
    private ReviewsAdapter reviewsAdapter;
    private TextView titleView;
    private ImageView posterView;
    private TextView overviewView;
    private TextView ratingView;
    private RatingBar rating_bar;
    private TextView releaseDateView;
    private TextView trailerVideosLabelView;
    private TextView reviewsLabelView;
    // Reference to Favorites menuItem to change icon according to isFavorite boolean
    private MenuItem mFavoritesMenuItem;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // Retrieve extras from intent
        Bundle extras = getIntent().getExtras();
        mDb = AppDatabase.getInstance(getApplicationContext());
        // Avoid NullPointerException by checking if extras is null
        if (extras != null && extras.size() != 0) {
            // Initialize views
            titleView = findViewById(R.id.tv_original_title);
            posterView = findViewById(R.id.iv_poster_detail);
            overviewView = findViewById(R.id.tv_overview);
            ratingView = findViewById(R.id.tv_user_rating);
            rating_bar = findViewById(R.id.rating_bar);
            releaseDateView = findViewById(R.id.tv_release_date);
            trailerVideosLabelView = findViewById(R.id.tv_trailers_label);
            reviewsLabelView = findViewById(R.id.tv_reviews_label);
            // Hide Label TextView for trailers and review if no internet connection
            trailerVideosLabelView.setVisibility(View.GONE);
            reviewsLabelView.setVisibility(View.GONE);
            // Get current Movie from Intent
            Movie currentMovie = extras.getParcelable(MainActivity.CURRENT_MOVIE_PARCELABLE_KEY);
            // Declare viewFactory to create MovieDetailsViewModel
            MovieDetailsViewModelFactory factory = new MovieDetailsViewModelFactory(mDb, currentMovie, context);
            viewModel = ViewModelProviders.of(this, factory).get(MovieDetailsViewModel.class);
        } else {
            // When retrieving the extras from Intent failed
            showToastMessage(getString(R.string.details_error_loading_data));
        }

        trailerVideosAdapter = new TrailerVideosAdapter(trailerVideosAdapterOnClickHandler);
        GridLayoutManager trailerVideosLayoutManager
                = new GridLayoutManager(this, 2);
        RecyclerView trailerVideosRecyclerView = findViewById(R.id.rv_trailer_videos);
        trailerVideosRecyclerView.setLayoutManager(trailerVideosLayoutManager);
        trailerVideosRecyclerView.setHasFixedSize(true);
        trailerVideosRecyclerView.setAdapter(trailerVideosAdapter);

        reviewsAdapter = new ReviewsAdapter(reviewsAdapterOnClickHandler);
        LinearLayoutManager reviewsLayoutManager
                = new LinearLayoutManager(this);
        RecyclerView reviewsRecyclerView = findViewById(R.id.rv_reviews);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        // Setup ViewModel to get data from API or DB and populate data
        setupViewModel();
    }

    private void setupViewModel() {
        checkIfFavoriteAndSetActionbarIcon();
        if (MovieNetworkUtils.isConnected(context)) {
            populateTrailersAndReviewsInOnlineMode();
        } else {
            populateOnlyReviewsInOfflineMode();
        }
    }

    // Populate current movie data
    private void populateMovie(Movie currentMovie) {
        if (currentMovie != null) {
            titleView.setText(currentMovie.getOriginalTitle());
            String posterPath = POSTER_BASE_URL + currentMovie.getPosterPath();
            Picasso.with(this)
                    .load(posterPath)
                    .placeholder(R.drawable.no_poster)
                    .error(R.drawable.no_poster)
                    .into(posterView);

            if (currentMovie.getOverview().isEmpty()) {
                overviewView.setText(getString(R.string.no_data_found_message));
            }
            overviewView.setText(currentMovie.getOverview());

            if (currentMovie.getRating() >= 0.0) {
                ratingView.setText(getString(R.string.no_data_found_message));
            }
            float ratingForRatingBar = (float) (currentMovie.getRating() / 2.0);
            rating_bar.setRating(ratingForRatingBar);
            ratingView.setText(String.valueOf(currentMovie.getRating()));

            if (currentMovie.getReleaseDate().isEmpty()) {
                releaseDateView.setText(getString(R.string.no_data_found_message));
            }
            releaseDateView.setText(currentMovie.getReleaseDate());
        } else {
            finish();
            showToastMessage(getString(R.string.no_internet_connection));
        }
    }

    // Populate current movie's trailers and reviews when an Internet connection is available
    private void populateTrailersAndReviewsInOnlineMode() {
        populateMovie(viewModel.getCurrentMovie());
        viewModel.getMovieTrailerVideosFromWebService().observe(this, new Observer<List<TrailerVideo>>() {
            @Override
            public void onChanged(@Nullable List<TrailerVideo> trailerVideos) {
                trailerVideosLabelView.setVisibility(View.VISIBLE);
                trailerVideosAdapter.setTrailerVideosData(trailerVideos);
                trailerVideosAdapter.setTrailerVideosData(trailerVideos);
            }
        });

        viewModel.getMovieReviewsFromWebService().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                reviewsLabelView.setVisibility(View.VISIBLE);
                reviewsAdapter.setReviewsData(reviews);
                reviewsAdapter.setReviewsData(reviews);
            }
        });
    }

    // Populate only current movie's reviews if no internet connection
    private void populateOnlyReviewsInOfflineMode() {
        viewModel.getMovieFromDb().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                populateMovie(movie);
            }
        });

        viewModel.getReviewsFromDb().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                reviewsAdapter.setReviewsData(reviews);
            }
        });
        showToastMessage(getString(R.string.offline_mode_message));
    }

    TrailerVideosAdapterOnClickHandler trailerVideosAdapterOnClickHandler = new TrailerVideosAdapterOnClickHandler() {
        @Override
        public void onClick(String currentTrailerVideoKey) {
            watchYoutubeVideo(context, currentTrailerVideoKey);
        }
    };

    public static void watchYoutubeVideo(Context context, String currentTrailerVideoKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + currentTrailerVideoKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + currentTrailerVideoKey));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    // Instance of OnClickHandler to make reviews clickable
    ReviewsAdapterOnClickHandler reviewsAdapterOnClickHandler = new ReviewsAdapterOnClickHandler() {
        @Override
        public void onClick(String currentReviewUrl) {
            openReviewLink(context, currentReviewUrl);
        }
    };

    // Helper method to open web browser and show review's details
    public static void openReviewLink(Context context, String currentReviewUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentReviewUrl));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    // When favorite button (in action bar) clicked
    private void addRemoveFavorite() {
        //invalidateOptionsMenu();
        if (isFavorite) {
            removeFavoriteWithDialog();
        } else {
            addFavorite();
        }
    }

    // Add movie data and reviews data to favorites (To database)
    private void addFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int insertedReviewsCount = 0;
                // insert new Movie
                long insertedMovieId = mDb.movieDao().insertMovie(viewModel.getCurrentMovie());
                List<Review> currentMovieReviews = viewModel.getMovieReviewsFromWebService().getValue();
                if (currentMovieReviews != null && currentMovieReviews.size() > 0) {
                    for (Review review : currentMovieReviews) {
                        mDb.reviewDao().insertReview(review);
                        insertedReviewsCount++;
                    }
                }
                if (insertedMovieId != 0 && insertedReviewsCount != 0) {
                    isFavorite = true;
                }
            }

        });
    }

    // Show dialog before removing movie from favorites
    private void removeFavoriteWithDialog() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeFavorite();
                    }
                };
        // Show a dialog that notifies the user they have unsaved changes
        showDeleteConfirmDialog(discardButtonClickListener);
    }

    private void removeFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().deleteMovie(viewModel.getCurrentMovie());
                mDb.reviewDao().deleteReviewsByMovieId(viewModel.getCurrentMovie().getId());
            }
        });
        showToastMessage(getString(R.string.movie_removed));
    }

    // Display an alert dialog before deleting Movie
    private void showDeleteConfirmDialog(
            DialogInterface.OnClickListener deleteButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_movie_confirm_dialog_msg);
        builder.setPositiveButton(R.string.delete_movie_confirm_dialog_delete, deleteButtonClickListener);
        builder.setNegativeButton(R.string.delete_movie_confirm_dialog_back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Observe if movie is favorite and update icon and isFavorite variable
    private void checkIfFavoriteAndSetActionbarIcon() {
        viewModel.isFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    isFavorite = aBoolean;
                    if (mFavoritesMenuItem != null) {
                        if (aBoolean) {
                            mFavoritesMenuItem.setIcon(R.drawable.baseline_favorite_white_48);
                        } else {
                            mFavoritesMenuItem.setIcon(R.drawable.baseline_favorite_border_white_48);
                        }
                    }
                }
            }
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mFavoritesMenuItem = menu.findItem(R.id.action_favorite);
        // I used setupViewModel here to avoid NullPointerException on favoriteMenuItem declaration
        setupViewModel();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            addRemoveFavorite();
        }
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
