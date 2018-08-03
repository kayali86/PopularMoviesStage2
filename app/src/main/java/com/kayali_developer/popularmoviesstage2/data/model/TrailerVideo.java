package com.kayali_developer.popularmoviesstage2.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trailer_videos")
public class TrailerVideo {
    @PrimaryKey(autoGenerate = true)
    private final int movieId;
    private final String trailerVideoId, key, name, site;

    public TrailerVideo(int movieId, String trailerVideoId, String key, String name, String site) {
        this.movieId = movieId;
        this.trailerVideoId = trailerVideoId;
        this.key = key;
        this.name = name;
        this.site = site;
    }

    public int getMovieId() {
        return movieId;
    }
    public String getTrailerVideoId() {
        return trailerVideoId;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getSite() {
        return site;
    }
}
