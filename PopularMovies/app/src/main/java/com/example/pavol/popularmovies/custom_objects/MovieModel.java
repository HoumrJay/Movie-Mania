package com.example.pavol.popularmovies.custom_objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavol on 04/03/2018.
 */

public class MovieModel implements Parcelable {

    private String mOriginalTitle;
    private String mMoviePoster;
    private String mPlotSynopsis;
    private double mUserRating;
    private String mReleaseDate;
    private String mMovieId;


    public MovieModel(String movieId, String originalTitle, String moviePoster, String plotSynopsis,
                      double userRating, String releaseDate) {

        mMovieId = movieId;
        mOriginalTitle = originalTitle;
        mMoviePoster = moviePoster;
        mPlotSynopsis = plotSynopsis;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    private MovieModel(Parcel parcel) {
        mMovieId = parcel.readString();
        mOriginalTitle = parcel.readString();
        mMoviePoster = parcel.readString();
        mPlotSynopsis = parcel.readString();
        mUserRating = parcel.readDouble();
        mReleaseDate = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMovieId);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mPlotSynopsis);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mReleaseDate);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel parcel) {
            return new MovieModel(parcel);
        }

        @Override
        public MovieModel[] newArray(int i) {
            return new MovieModel[i];
        }
    };

    public String getMovieId() {
        return mMovieId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

}
