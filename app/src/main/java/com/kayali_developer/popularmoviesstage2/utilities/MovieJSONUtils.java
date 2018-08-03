package com.kayali_developer.popularmoviesstage2.utilities;

import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.data.model.Review;
import com.kayali_developer.popularmoviesstage2.data.model.TrailerVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class MovieJSONUtils {
    // Movies JSON response Keys
    private static final String RESULTS_KEY = "results";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String OVERVIEW_KEY = "overview";
    private static final String VOTE_AVERAGE_KEY = "vote_average";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String MOVIE_ID_KEY = "id";
    // TrailerVideo JSON response Keys
    private static final String TRAILER_VIDEO_ID_KEY = "id";
    private static final String TRAILER_VIDEO_KEY_KEY = "key";
    private static final String TRAILER_VIDEO_NAME_KEY = "name";
    private static final String TRAILER_VIDEO_SITE_KEY = "site";
    // Review JSON response Keys
    private static final String REVIEW_AUTHOR_KEY  = "author";
    private static final String REVIEW_CONTENT_KEY  = "content";
    private static final String REVIEW_ID_KEY  = "id";
    private static final String REVIEW_URL_KEY  = "url";

    // Constructor
    private MovieJSONUtils() {
        throw new AssertionError();
    }
    // Extract data Strings and return a list of "Movie"
    public static List<Movie> extractMoviesData(String stringUrl) {
        String jsonResponse = fetchResponse(stringUrl);
        if (jsonResponse == null) {
            return null;
        }
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray resultsJSONArray = baseJsonResponse.getJSONArray(RESULTS_KEY);
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject currentMovie = resultsJSONArray.getJSONObject(i);
                String originalTitle = currentMovie.optString(ORIGINAL_TITLE_KEY);
                String posterPath = currentMovie.optString(POSTER_PATH_KEY);
                String overview = currentMovie.optString(OVERVIEW_KEY);
                double userRating = currentMovie.getDouble(VOTE_AVERAGE_KEY);
                String releaseDate = currentMovie.optString(RELEASE_DATE_KEY);
                int movieId = currentMovie.optInt(MOVIE_ID_KEY);
                Movie movie = new Movie(originalTitle, posterPath, overview, userRating, releaseDate, movieId);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Extract data Strings and return a list of "TrailerVideo"
    public static List<TrailerVideo> extractTrailerVideoData(String stringUrl, int movieId) {
        String jsonResponse = fetchResponse(stringUrl);
        if (jsonResponse == null) {
            return null;
        }
        List<TrailerVideo> trailerVideos = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray resultsJSONArray = baseJsonResponse.getJSONArray(RESULTS_KEY);
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject currentTrailerVideo = resultsJSONArray.getJSONObject(i);
                String trailerVideoId = currentTrailerVideo.optString(TRAILER_VIDEO_ID_KEY);
                String key = currentTrailerVideo.optString(TRAILER_VIDEO_KEY_KEY);
                String name = currentTrailerVideo.optString(TRAILER_VIDEO_NAME_KEY);
                String site = currentTrailerVideo.optString(TRAILER_VIDEO_SITE_KEY);
                TrailerVideo trailerVideo = new TrailerVideo(movieId, trailerVideoId, key, name, site);
                trailerVideos.add(trailerVideo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerVideos;
    }


    //  Extract data Strings and return a list of "Review"
    public static List<Review> extractReviewData(String stringUrl, int movieId) {
        String jsonResponse = fetchResponse(stringUrl);
        if (jsonResponse == null) {
            return null;
        }
        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray resultsJSONArray = baseJsonResponse.getJSONArray(RESULTS_KEY);
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject currentMovie = resultsJSONArray.getJSONObject(i);
                String author = currentMovie.optString(REVIEW_AUTHOR_KEY);
                String content = currentMovie.optString(REVIEW_CONTENT_KEY);
                String reviewId = currentMovie.optString(REVIEW_ID_KEY);
                String reviewUrl = currentMovie.optString(REVIEW_URL_KEY);
                Review review = new Review(reviewId, movieId, author, content, reviewUrl);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Fetch a JSON response using API Url
    private static String fetchResponse(String stringUrl) {
        URL url = MovieNetworkUtils.createUrl(stringUrl);
        String jsonResponse = null;
        try {
            jsonResponse = MovieNetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}