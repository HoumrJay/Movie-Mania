package com.example.pavol.popularmovies.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pavol on 07/04/2018.
 */

public class FavMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favMoviesDb.db";

    /* Additionally, I added another column so I needed to update the table version number.*/
    private static final int VERSION = 2;

    FavMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE " + FavMoviesContract.FavouriteMoviesEntry.TABLE_NAME + " (" +
                FavMoviesContract.FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE + " TEXT, " +
                FavMoviesContract.FavouriteMoviesEntry.PLOT_SYNOPSIS + " TEXT, " +
                FavMoviesContract.FavouriteMoviesEntry.MOVIE_RATING + " DOUBLE, " +
                FavMoviesContract.FavouriteMoviesEntry.RELEASE_DATE + " TEXT, " +
                FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER + " BLOB, " +
                FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER_URL + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMoviesContract.FavouriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
