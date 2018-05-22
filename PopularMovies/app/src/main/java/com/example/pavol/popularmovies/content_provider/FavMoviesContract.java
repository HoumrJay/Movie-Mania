package com.example.pavol.popularmovies.content_provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by pavol on 27/03/2018.
 */

public class FavMoviesContract {

    public static final String AUTHORITY = "com.example.pavol.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "favourite_movies";

    public static final class FavouriteMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favourite_movies";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String PLOT_SYNOPSIS = "movie_plot";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String MOVIE_POSTER = "movie_poster";
        public static final String MOVIE_POSTER_URL = "movie_poster_url";
    }
}
