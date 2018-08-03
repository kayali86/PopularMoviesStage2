package com.kayali_developer.popularmoviesstage2.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity (tableName = "movies")
public class Movie implements Parcelable {
    final private String originalTitle, posterPath, overview, releaseDate;
    final private double rating;
    @PrimaryKey(autoGenerate = true)
    final private int id;
    // Base images Url to append with image path
    // Constructor
    public Movie(String originalTitle, String posterPath, String overview, double rating, String releaseDate, int id) {
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.id = id;
    }
    // Getters
    public String getOriginalTitle() {
        return originalTitle;
    }
    public String getPosterPath() {
        return this.posterPath;
    }
    public String getOverview() {
        return overview;
    }
    public double getRating() {
        return rating;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public int getId() {
        return id;
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeInt(id);
    }

    @Ignore
    private Movie(Parcel in){
        this.originalTitle = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.id = in.readInt();
    }

    @Ignore
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}


