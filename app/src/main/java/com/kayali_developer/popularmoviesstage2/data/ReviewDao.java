package com.kayali_developer.popularmoviesstage2.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kayali_developer.popularmoviesstage2.data.model.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReview(Review review);

    // I used query to delete multiple reviews with specific id
    @Query("DELETE FROM reviews WHERE  movie_id = :id")
    void deleteReviewsByMovieId(int id);

    @Query("SELECT * FROM reviews WHERE movie_id = :id")
    LiveData<List<Review>> loadReviewsByMovieId(int id);

}
