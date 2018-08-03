package com.kayali_developer.popularmoviesstage2.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "reviews")
public class Review {
    @PrimaryKey()
    @NonNull
    private final String reviewId;
    private final int movie_id;
    private final String author, content, url;

    public Review(@NonNull String reviewId, int movie_id, String author, String content, String url) {
        this.reviewId = reviewId;
        this.movie_id = movie_id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public int getMovie_id() {
        return movie_id;
    }
    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }
    public String getReviewId() {
        return reviewId;
    }
    public String getUrl() {
        return url;
    }
}
