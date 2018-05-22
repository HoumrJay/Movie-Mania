package com.example.pavol.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pavol.popularmovies.MovieDetailActivity;
import com.example.pavol.popularmovies.R;
import com.example.pavol.popularmovies.custom_objects.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pavol on 04/03/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final ArrayList<MovieModel> mListForAdapter;
    private Context context;
    private static final String BASIC_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE_CONSTANT_W185 = "w185/";

    public static final String INTERNET_MOVIE_VALUE = "internet movie";

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_thumbnail)
        ImageView moviePoster;
        @BindView(R.id.card_view)
        CardView clickableCard;
        @BindView(R.id.grid_layout_rating)
        TextView gridLayoutRating;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }
    }

    public MoviesAdapter(final ArrayList<MovieModel> listForAdapter) {
        this.mListForAdapter = listForAdapter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.grid_layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final MovieModel singleMovieData = mListForAdapter.get(position);

        String rating = String.valueOf(singleMovieData.getUserRating()) + "/10";
        String moviePosterUrl = BASIC_IMAGE_URL + SIZE_CONSTANT_W185 + singleMovieData.getMoviePoster();

        holder.gridLayoutRating.setText(rating);
        Picasso.with(context).load(moviePosterUrl).into(holder.moviePoster);
        holder.clickableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent movieDetailsIntent = new Intent(context, MovieDetailActivity.class);
                movieDetailsIntent.putExtra(MovieDetailActivity.DETAILS_KEY, singleMovieData);
                movieDetailsIntent.putExtra(MovieDetailActivity.MOVIE_SOURCE, INTERNET_MOVIE_VALUE);
                context.startActivity(movieDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListForAdapter.size();
    }
}
