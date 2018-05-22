package com.example.pavol.popularmovies.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.pavol.popularmovies.content_provider.FavMoviesContract.FavouriteMoviesEntry.TABLE_NAME;

/**
 * Created by pavol on 07/04/2018.
 */

public class FavMoviesContentProvider extends ContentProvider {

    public static final int FAVOURITE_MOVIES = 100;
    public static final int FAVOURITE_MOVIES_ROW = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_MOVIES, FAVOURITE_MOVIES);
        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_MOVIES + "/#", FAVOURITE_MOVIES_ROW);

        return uriMatcher;
    }

    private FavMoviesDbHelper mFavMoviesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavMoviesDbHelper = new FavMoviesDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase database = mFavMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVOURITE_MOVIES:
                long id = database.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavMoviesContract.FavouriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase database = mFavMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursorObject;

        switch (match) {
            case FAVOURITE_MOVIES:
                cursorObject = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITE_MOVIES_ROW:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                cursorObject = database.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        cursorObject.setNotificationUri(getContext().getContentResolver(), uri);

        return cursorObject;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mFavMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int tasksDeleted;

        switch (match) {
            case FAVOURITE_MOVIES_ROW:

                String id = uri.getPathSegments().get(1);
                String mWhereClause = "_id=?";
                String[] mWhereArgs = new String[]{id};
                tasksDeleted = database.delete(TABLE_NAME, mWhereClause, mWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
