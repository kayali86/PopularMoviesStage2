package com.kayali_developer.popularmoviesstage2.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.kayali_developer.popularmoviesstage2.data.AppDatabase;
import com.kayali_developer.popularmoviesstage2.data.model.Movie;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final Movie currentMovie;
    private Context context;

    public MovieDetailsViewModelFactory(AppDatabase database, Movie currentMovie, Context context) {
        this.mDb = database;
        this.currentMovie = currentMovie;
        this.context = context;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailsViewModel(mDb, currentMovie, context);
    }
}
