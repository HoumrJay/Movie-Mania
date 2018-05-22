package com.example.pavol.popularmovies.fragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pavol.popularmovies.MainActivity;
import com.example.pavol.popularmovies.adapters.MoviesCursorAdapter;
import com.example.pavol.popularmovies.R;
import com.example.pavol.popularmovies.content_provider.FavMoviesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavouriteMovies extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID = 0;
    private MoviesCursorAdapter cursorAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public FragmentFavouriteMovies() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.movie_list, container, false);
        ButterKnife.bind(this, mainView);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        cursorAdapter = new MoviesCursorAdapter(getContext());
        recyclerView.setAdapter(cursorAdapter);
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {

            Cursor receivedMovieData = null;

            @Override
            protected void onStartLoading() {
                if (receivedMovieData != null) {
                    deliverResult(receivedMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Context appContext = MainActivity.getAppContext();
                    return appContext.getContentResolver().query(FavMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                receivedMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
