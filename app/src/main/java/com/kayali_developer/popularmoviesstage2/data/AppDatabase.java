package com.kayali_developer.popularmoviesstage2.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.kayali_developer.popularmoviesstage2.data.model.Movie;
import com.kayali_developer.popularmoviesstage2.data.model.Review;
import com.kayali_developer.popularmoviesstage2.data.model.TrailerVideo;

@Database(entities = {Movie.class, TrailerVideo.class, Review.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
    public abstract ReviewDao reviewDao();

}
