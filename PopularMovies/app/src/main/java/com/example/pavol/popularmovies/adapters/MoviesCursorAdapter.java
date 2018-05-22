package com.example.pavol.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pavol.popularmovies.MovieDetailActivity;
import com.example.pavol.popularmovies.R;
import com.example.pavol.popularmovies.content_provider.FavMoviesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pavol on 14/04/2018.
 */

public class MoviesCursorAdapter extends RecyclerView.Adapter<MoviesCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context context;
    private Bitmap moviePosterBitmapSource;

    public static final String DATABASE_MOVIE_VALUE = "database movie";

    public MoviesCursorAdapter(Context mContext) {
        this.context = mContext;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_thumbnail)
        ImageView moviePoster;
        @BindView(R.id.card_view)
        CardView clickableCard;
        @BindView(R.id.grid_layout_rating)
        TextView gridLayoutRating;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new MovieViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.grid_layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry._ID);
        int ratingIndex = mCursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.MOVIE_RATING);
        int moviePosterIndex = mCursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER);

        mCursor.moveToPosition(position);

        // get values
        int id = mCursor.getInt(idIndex); //ID

        double rating = mCursor.getDouble(ratingIndex); //RATING
        String ratingString = String.valueOf(rating) + "/10";

        byte[] moviePosterByteArray = mCursor.getBlob(moviePosterIndex); //MOVIE POSTER BYTE ARRAY
        byteArrayToBitmap(moviePosterByteArray);


        //set values
        holder.itemView.setTag(id);
        holder.gridLayoutRating.setText(ratingString);
        holder.moviePoster.setImageBitmap(moviePosterBitmapSource);

        holder.clickableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int movieId = (int) holder.itemView.getTag();
                Intent movieDetailsIntent = new Intent(context, MovieDetailActivity.class);
                movieDetailsIntent.putExtra(MovieDetailActivity.MOVIE_INDEX, movieId);
                movieDetailsIntent.putExtra(MovieDetailActivity.MOVIE_SOURCE, DATABASE_MOVIE_VALUE);
                context.startActivity(movieDetailsIntent);

            }
        });

    }


    private void byteArrayToBitmap(byte[] imgByteArray) {
        moviePosterBitmapSource = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor) {

        if (mCursor == newCursor) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
