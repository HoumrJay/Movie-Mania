package com.example.pavol.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pavol.popularmovies.adapters.MoviesAdapter;
import com.example.pavol.popularmovies.adapters.MoviesCursorAdapter;
import com.example.pavol.popularmovies.adapters.ReviewsAdapter;
import com.example.pavol.popularmovies.adapters.VideosAdapter;
import com.example.pavol.popularmovies.content_provider.FavMoviesContract;
import com.example.pavol.popularmovies.custom_objects.MovieModel;
import com.example.pavol.popularmovies.custom_objects.ReviewModel;
import com.example.pavol.popularmovies.custom_objects.VideoModel;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pavol on 18/03/2018.
 */

public class MovieDetailActivity extends AppCompatActivity {

    public static final String DETAILS_KEY = "details";
    public static final String MOVIE_INDEX = "movie index";
    public static final String MOVIE_SOURCE = "movie source";

    private static final String BASIC_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE_CONSTANT_W342 = "w342/";

    private static final int MOVIE_INDEX_VALUE = -1;

    private static final String CLICKED_SECTION_KEY = "clicked_section_key";

    private String videoJsonUrlString = "http://api.themoviedb.org/3/movie/{id}/videos?api_key=";
    private String reviewsJsonUrlString = "http://api.themoviedb.org/3/movie/{id}/reviews?api_key=";
    private static final String POPULAR_MOVIES_API_KEY = "1e0364ae252be32db888c3490818f6ae";


    private byte[] imgByteArray;
    private Bitmap posterBitmapSource;

    private int clickCounter = 0;
    private String totalNumberOfReviews;
    private int sectionId = 0; // plot id = 1, reviews id = 2, trailers id = 3

    private Cursor getFavouriteMovieCursor = null;

    private String originalId;

    @BindView(R.id.movie_poster)
    ImageView moviePosterIv;
    @BindView(R.id.original_title)
    TextView movieTitleTv;
    @BindView(R.id.movie_plot)
    TextView moviePlotTv;
    @BindView(R.id.release_date)
    TextView movieReleaseDateTv;
    @BindView(R.id.movie_rating)
    TextView movieRatingTv;
    @BindView(R.id.rating_bar)
    RatingBar movieRatingBar;
    @BindView(R.id.favourite_button)
    ImageView favouriteButton;

    @BindView(R.id.plot_expandable_layout)
    ExpandableLayout plotExpandableLayout;
    @BindView(R.id.plot_label)
    TextView plotLabel;


    @BindView(R.id.trailer_list)
    RecyclerView listOfTrailers;
    @BindView(R.id.trailers_expandable_layout)
    ExpandableLayout trailersExpandableLayout;
    @BindView(R.id.show_trailers_tv)
    TextView showTrailersTv;

    @BindView(R.id.reviews_list)
    RecyclerView mListOfReviews;
    @BindView(R.id.show_reviews_tv)
    TextView showReviewsTv;
    @BindView(R.id.reviews_expandable_layout)
    ExpandableLayout reviewsExpandableLayout;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);


        Intent inheritedData = getIntent();
        /* the code differentiates between movie data passed as a parcelable object from fragments "FragmentPopularity"
         * or "FragmentRating" - i.e. directly from the Internet and data loaded from SQL database triggered
         * from "FragmentFavourite" */

        if (inheritedData.getStringExtra(MOVIE_SOURCE).
                equals(MoviesAdapter.INTERNET_MOVIE_VALUE)) {

            /* after the rotation, it is necessary to check which from the three tabs
            (Plot, Reviews, Trailers) was active before the rotation so I can mark
            the correct one as active again .*/

            if (savedInstanceState != null) {
                sectionId = savedInstanceState.getInt(CLICKED_SECTION_KEY);

                switch (sectionId) {
                    case 0:
                    case 1:
                        plotLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        break;

                    case 2:
                        showReviewsTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        break;

                    case 3:
                        showTrailersTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        break;
                }
            }


            final MovieModel specificMovie = inheritedData.getParcelableExtra(DETAILS_KEY);

            listOfTrailers.setLayoutManager(new LinearLayoutManager
                    (this, LinearLayoutManager.HORIZONTAL, false));

            mListOfReviews.setLayoutManager(new LinearLayoutManager
                    (this, LinearLayoutManager.VERTICAL, false));

            /* Every time onCreate() is called in this activity, app needs to check if the movie
            already is in the database of favourite movies or not - otherwise the user could add one
               * movie multiple times. Variable clickCounter is not helpful in this case as it is
               * re-created whenever onCreate() is called. */

            String movieTitle = specificMovie.getOriginalTitle();
            final boolean isAmongFavourite = isFavouriteCheck(movieTitle);

            if (isAmongFavourite) {
                favouriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
                favouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getBaseContext(), R.string.is_among_favourite,
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {

                favouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /* the "if" statement checks if the movie has already been marked as favourite.
                         * It prevents the user from marking the same movie as favourite more than once -
                         * without this check it would be possible mark one movie as favourite multiple
                         * times util onCreate() is called again.
                         */

                        clickCounter += 1;
                        if (clickCounter == 1) {
                            favouriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
                            moviePosterIv.buildDrawingCache();
                            Bitmap imgBitmapFormat = moviePosterIv.getDrawingCache();
                            bitmapToByteArray(imgBitmapFormat);
                            addToFavourite(specificMovie);
                        } else {
                            Toast.makeText(getBaseContext(), R.string.is_among_favourite,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }


            originalId = specificMovie.getMovieId();

            String moviePosterUrl = BASIC_IMAGE_URL + SIZE_CONSTANT_W342 + specificMovie.getMoviePoster();
            Picasso.with(this).load(moviePosterUrl).into(moviePosterIv);

            movieTitleTv.setText(specificMovie.getOriginalTitle());
            moviePlotTv.setText(specificMovie.getPlotSynopsis());
            movieReleaseDateTv.setText(dateConverter(specificMovie.getReleaseDate()));
            movieRatingTv.setText(String.valueOf(specificMovie.getUserRating()));

            Float starsRating = Float.valueOf(String.valueOf(specificMovie.getUserRating()));

            movieRatingBar.setRating(starsRating / 2);


            if (savedInstanceState == null) {
                plotLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            plotExpandableLayout.setInterpolator(Utils.createInterpolator
                    (Utils.ACCELERATE_DECELERATE_INTERPOLATOR));
            plotLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!plotExpandableLayout.isExpanded() || (savedInstanceState != null && (sectionId == 2 || sectionId == 3))) {
                        plotLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        reviewsExpandableLayout.collapse();
                        trailersExpandableLayout.collapse();

                        showReviewsTv.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));
                        showTrailersTv.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));

                        sectionId = 1;
                        toggleExpandableLayout(plotExpandableLayout);
                    }
                }
            });

            setTitle(specificMovie.getOriginalTitle());

            VideosAsyncTask getVideoKeys = new VideosAsyncTask();
            getVideoKeys.execute();

            ReviewsAsyncTask getReviews = new ReviewsAsyncTask();
            getReviews.execute();


        } else if (inheritedData.getStringExtra(MOVIE_SOURCE). // This part deals with the movies stored in the database.
                equals(MoviesCursorAdapter.DATABASE_MOVIE_VALUE)) {


            /* reviews and trailers are not showed for movies from database so I hid them.*/

            showReviewsTv.setVisibility(View.GONE);
            showTrailersTv.setVisibility(View.GONE);

            int movieIndex = inheritedData.getIntExtra(MOVIE_INDEX, MOVIE_INDEX_VALUE);
            String movieIndexString = Integer.toString(movieIndex);

            Uri uri = FavMoviesContract.FavouriteMoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieIndexString).build();

            final Uri finalUri = uri;

            getFavouriteMovieCursor = getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);


            getFavouriteMovieCursor.moveToFirst();
            getMovieFromDatabase(getFavouriteMovieCursor);

            plotLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            /*in this case, the favourite button serves only to remove the movies from favourite movies*/

            favouriteButton.setImageResource(R.drawable.ic_favorite_white_36dp);
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFromFavourite(finalUri);
                }
            });

        }
    }

    // method the chcecks if the movie already is among the favourite movies or not
    private boolean isFavouriteCheck(String movieTitle) {
        Cursor helperCursor;
        boolean isFavourite = false;
        String returnedTitle = " ";
        String[] mProjection = new String[]{FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE};
        String mSelection = FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE + "=?";
        String[] mSelectionArgs = new String[]{movieTitle};

        try {
            helperCursor = this.getContentResolver().query(FavMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                    mProjection,
                    mSelection,
                    mSelectionArgs,
                    null
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        helperCursor.moveToFirst();

        try {
            int index = helperCursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE);
            returnedTitle = helperCursor.getString(index);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!returnedTitle.equals(" ")) {
            isFavourite = true;
        }

        helperCursor.close();

        return isFavourite;
    }

    /*The two following methods help me to store movie posters in the database in the form of
     * a byteArray and then convert it back to a bitmap image when I need to show it */
    private void bitmapToByteArray(Bitmap bitmapImg) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImg.compress(Bitmap.CompressFormat.PNG, 0, stream);
        imgByteArray = stream.toByteArray();
    }

    private void byteArrayToBitmap(byte[] imgByteArray) {
        posterBitmapSource = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
    }

    /* a simple method that helps me to convert the release date to a different format*/
    private String dateConverter(String originalDate) {
        String convertedDate;
        String monthString;
        String[] monthsArray = getResources().getStringArray(R.array.months);
        String[] separatedString = originalDate.split("-");
        switch (separatedString[1]) {
            case "01":
                monthString = monthsArray[0];
                break;
            case "02":
                monthString = monthsArray[1];
                break;
            case "03":
                monthString = monthsArray[2];
                break;
            case "04":
                monthString = monthsArray[3];
                break;
            case "05":
                monthString = monthsArray[4];
                break;
            case "06":
                monthString = monthsArray[5];
                break;
            case "07":
                monthString = monthsArray[6];
                break;
            case "08":
                monthString = monthsArray[7];
                break;
            case "09":
                monthString = monthsArray[8];
                break;
            case "10":
                monthString = monthsArray[9];
                break;
            case "11":
                monthString = monthsArray[10];
                break;
            case "12":
                monthString = monthsArray[11];
                break;
            default:
                monthString = "Error";
        }

        convertedDate = monthString + " " + separatedString[0];

        if (monthString.equals("Error")) {
            return originalDate;
        } else {
            return convertedDate;
        }
    }

    public void addToFavourite(MovieModel movieData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE, movieData.getOriginalTitle());
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.PLOT_SYNOPSIS, movieData.getPlotSynopsis());
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.MOVIE_RATING, movieData.getUserRating());
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.RELEASE_DATE, movieData.getReleaseDate());
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER, imgByteArray);
        contentValues.put(FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER_URL, movieData.getMoviePoster());

        Uri uri = getContentResolver().insert(FavMoviesContract.FavouriteMoviesEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(), R.string.add_to_favourite, Toast.LENGTH_LONG).show();
        }

    }

    private void removeFromFavourite(Uri uriToRemove) {
        getContentResolver().delete(uriToRemove,
                null,
                null);

        Toast.makeText(getBaseContext(), R.string.remove_from_favourite,
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void getMovieFromDatabase(Cursor cursor) {

        int titleIndex = cursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.ORIGINAL_TITLE);
        int plotIndex = cursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.PLOT_SYNOPSIS);
        int ratingIndex = cursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.MOVIE_RATING);
        int dateIndex = cursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.RELEASE_DATE);
        int moviePosterIndex = cursor.getColumnIndex(FavMoviesContract.FavouriteMoviesEntry.MOVIE_POSTER);


        //get values
        String title = cursor.getString(titleIndex); //TITLE
        String plot = cursor.getString(plotIndex); //PLOT_SYNOPSIS
        double rating = cursor.getDouble(ratingIndex); //RATING
        String date = cursor.getString(dateIndex); //DATE
        byte[] moviePosterByteArray = cursor.getBlob(moviePosterIndex); //MOVIE POSTER BYTE ARRAY
        byteArrayToBitmap(moviePosterByteArray);

        //set values
        moviePosterIv.setImageBitmap(posterBitmapSource);
        movieTitleTv.setText(title);
        moviePlotTv.setText(plot);
        movieReleaseDateTv.setText(dateConverter(date));
        movieRatingTv.setText(String.valueOf(rating));
        Float starsRating = Float.valueOf(String.valueOf(rating));
        movieRatingBar.setRating(starsRating / 2);

        setTitle(title);
    }

    private class VideosAsyncTask extends AsyncTask<URL, Void, ArrayList<VideoModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<VideoModel> doInBackground(URL... urls) {
            URL videoJsonUrl;
            String jsonResponse = "";

            videoJsonUrlString = videoJsonUrlString.replace("{id}", originalId);
            videoJsonUrl = createUrl(videoJsonUrlString + POPULAR_MOVIES_API_KEY);

            try {
                jsonResponse = httpRequestMethod(videoJsonUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return videoKeyArray(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<VideoModel> listOfKeys) {
            super.onPostExecute(listOfKeys);

            listOfTrailers.setAdapter(new VideosAdapter(listOfKeys));
            trailersExpandableLayout.setInterpolator(Utils.createInterpolator
                    (Utils.ACCELERATE_DECELERATE_INTERPOLATOR));
            showTrailersTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!trailersExpandableLayout.isExpanded()) {
                        showTrailersTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        reviewsExpandableLayout.collapse();
                        plotExpandableLayout.collapse();

                        showReviewsTv.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));
                        plotLabel.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));

                        sectionId = 3;
                        toggleExpandableLayout(trailersExpandableLayout);

                    }
                }
            });


        }

        private URL createUrl(String urlString) {
            URL url;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e("Runtime error:", "Error with creating URL", e);
                return null;
            }
            return url;
        }

        private String httpRequestMethod(URL mMovieUrl) throws IOException {
            String mJsonResponse = "";
            if (mMovieUrl == null) {
                return mJsonResponse;
            }

            HttpURLConnection newHttpConnection = null;
            InputStream newInputStream = null;

            try {
                newHttpConnection = (HttpURLConnection) mMovieUrl.openConnection();
                newHttpConnection.setRequestMethod("GET");
                newHttpConnection.setReadTimeout(10000);
                newHttpConnection.setConnectTimeout(15000);
                newHttpConnection.connect();
                if (newHttpConnection.getResponseCode() == 200) {
                    newInputStream = newHttpConnection.getInputStream();
                    mJsonResponse = inputStreamTranslation(newInputStream);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                if (newHttpConnection != null) {
                    newHttpConnection.disconnect();
                }

                if (newInputStream != null) {
                    newInputStream.close();
                }
            }

            return mJsonResponse;
        }

        private String inputStreamTranslation(InputStream mNewInputStream) throws IOException {
            StringBuilder finalInputStreamTranslation = new StringBuilder();
            if (mNewInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(mNewInputStream,
                        Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String responseInLine = bufferedReader.readLine();
                while (responseInLine != null) {
                    finalInputStreamTranslation.append(responseInLine);
                    responseInLine = bufferedReader.readLine();
                }
            }
            return finalInputStreamTranslation.toString();
        }

        private ArrayList<VideoModel> videoKeyArray(String jsonData) {

            ArrayList<VideoModel> videoKey = new ArrayList<VideoModel>();

            if (TextUtils.isEmpty(jsonData)) {
                return null;
            }

            try {
                JSONObject mainJsonObject = new JSONObject(jsonData);
                JSONArray resultsArray = mainJsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject videoObject = resultsArray.getJSONObject(i);
                    String videoUrlId = videoObject.optString("key");
                    String videoName = videoObject.optString("name");
                    videoKey.add(new VideoModel(videoUrlId, videoName));
                }

            } catch (JSONException e) {
                Log.e("Error:", "Problem parsing data!");
            }

            return videoKey;
        }

    }

    private class ReviewsAsyncTask extends AsyncTask<URL, Void, ArrayList<ReviewModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ReviewModel> doInBackground(URL... urls) {
            URL reviewsJsonUrl;
            String jsonResponse = "";

            reviewsJsonUrlString = reviewsJsonUrlString.replace("{id}", originalId);
            reviewsJsonUrl = createUrl(reviewsJsonUrlString + POPULAR_MOVIES_API_KEY);

            try {
                jsonResponse = httpRequestMethod(reviewsJsonUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return reviewsArray(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewModel> listOfReviews) {
            super.onPostExecute(listOfReviews);

            if (totalNumberOfReviews.equals("0")) {
                listOfReviews.add(new ReviewModel("", getResources().getString(R.string.no_reviews)));
            }

            mListOfReviews.setAdapter(new ReviewsAdapter(listOfReviews));
            reviewsExpandableLayout.setInterpolator(Utils.createInterpolator
                    (Utils.ACCELERATE_DECELERATE_INTERPOLATOR));
            showReviewsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!reviewsExpandableLayout.isExpanded()) {
                        showReviewsTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        plotExpandableLayout.collapse();
                        trailersExpandableLayout.collapse();

                        plotLabel.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));
                        showTrailersTv.setBackgroundColor(getResources().getColor(R.color.mainBackgroundColor));

                        sectionId = 2;
                        toggleExpandableLayout(reviewsExpandableLayout);
                    }
                }
            });


        }

        private URL createUrl(String urlString) {
            URL url;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e("Runtime error:", "Error with creating URL", e);
                return null;
            }
            return url;
        }

        private String httpRequestMethod(URL mMovieUrl) throws IOException {
            String mJsonResponse = "";
            if (mMovieUrl == null) {
                return mJsonResponse;
            }

            HttpURLConnection newHttpConnection = null;
            InputStream newInputStream = null;

            try {
                newHttpConnection = (HttpURLConnection) mMovieUrl.openConnection();
                newHttpConnection.setRequestMethod("GET");
                newHttpConnection.setReadTimeout(10000);
                newHttpConnection.setConnectTimeout(15000);
                newHttpConnection.connect();
                if (newHttpConnection.getResponseCode() == 200) {
                    newInputStream = newHttpConnection.getInputStream();
                    mJsonResponse = inputStreamTranslation(newInputStream);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                if (newHttpConnection != null) {
                    newHttpConnection.disconnect();
                }

                if (newInputStream != null) {
                    newInputStream.close();
                }
            }

            return mJsonResponse;
        }

        private String inputStreamTranslation(InputStream mNewInputStream) throws IOException {
            StringBuilder finalInputStreamTranslation = new StringBuilder();
            if (mNewInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(mNewInputStream,
                        Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String responseInLine = bufferedReader.readLine();
                while (responseInLine != null) {
                    finalInputStreamTranslation.append(responseInLine);
                    responseInLine = bufferedReader.readLine();
                }
            }
            return finalInputStreamTranslation.toString();
        }

        private ArrayList<ReviewModel> reviewsArray(String jsonData) {

            ArrayList<ReviewModel> reviews = new ArrayList<ReviewModel>();

            if (TextUtils.isEmpty(jsonData)) {
                return null;
            }

            try {
                JSONObject mainJsonObject = new JSONObject(jsonData);
                totalNumberOfReviews = mainJsonObject.optString("total_results");
                JSONArray resultsArray = mainJsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject reviewObject = resultsArray.getJSONObject(i);
                    String userName = reviewObject.optString("author");
                    String reviewContent = reviewObject.optString("content");
                    reviews.add(new ReviewModel(userName, reviewContent));
                }

            } catch (JSONException e) {
                Log.e("Error:", "Problem parsing data!");
            }
            return reviews;
        }

    }

    private void toggleExpandableLayout(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    /*I'm using onSaveInstanceState to store id of a section tab and restore it during onCreate()*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CLICKED_SECTION_KEY, sectionId);
    }
}
