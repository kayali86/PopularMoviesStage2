package com.kayali_developer.popularmoviesstage2.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kayali_developer.popularmoviesstage2.data.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> loadAllMovies();

    // Check in database if movie is favorite and return an observable boolean
    @Query("SELECT CASE WHEN EXISTS (SELECT * FROM movies WHERE id = :id) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END")
    LiveData<Boolean> isFavorite(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Movie> loadMovieById(int id);
}
