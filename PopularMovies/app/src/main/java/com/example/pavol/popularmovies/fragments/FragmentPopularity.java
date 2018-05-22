package com.example.pavol.popularmovies.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pavol.popularmovies.custom_objects.MovieModel;
import com.example.pavol.popularmovies.adapters.MoviesAdapter;
import com.example.pavol.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
 * A simple {@link Fragment} subclass.
 */
public class FragmentPopularity extends Fragment {

    private static final String POPULAR_MOVIES_API_KEY = "{replace with an API key}";
    private static final String ORDER_BY_POPULARITY_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    public FragmentPopularity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.movie_list, container, false);
        ButterKnife.bind(this, mainView);

        PopularMoviesAsyncTask onCreateAsyncTask = new PopularMoviesAsyncTask();
        onCreateAsyncTask.execute();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return mainView;
    }

    private class PopularMoviesAsyncTask extends AsyncTask<URL, Void, ArrayList<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieModel> doInBackground(URL... urls) {
            URL movieUrl;
            String jsonResponse = "";

            movieUrl = createMovieUrl(ORDER_BY_POPULARITY_URL + POPULAR_MOVIES_API_KEY);

            try {
                jsonResponse = httpRequestMethod(movieUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return movieObjectsList(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> movieModelList) {
            if (movieModelList == null) {
                return;
            }
            recyclerView.setAdapter(new MoviesAdapter(movieModelList));
            progressBar.setVisibility(View.GONE);

        }

        private URL createMovieUrl(String urlString) {
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

        private ArrayList<MovieModel> movieObjectsList(String jsonData) {

            ArrayList<MovieModel> listOfMovies = new ArrayList<>();

            if (TextUtils.isEmpty(jsonData)) {
                return null;
            }

            try {
                JSONObject mainJsonObject = new JSONObject(jsonData);
                JSONArray resultsArray = mainJsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movieDetails = resultsArray.getJSONObject(i);
                    String movieId = movieDetails.optString("id");
                    String originalTitle = movieDetails.optString("original_title");
                    String moviePoster = movieDetails.optString("poster_path");
                    String plotSynopsis = movieDetails.optString("overview");
                    double userRating = movieDetails.getDouble("vote_average");
                    String releaseDate = movieDetails.optString("release_date");

                    listOfMovies.add(new MovieModel(movieId, originalTitle, moviePoster, plotSynopsis,
                            userRating, releaseDate));
                }
            } catch (JSONException e) {
                Log.e("Error:", "Problem parsing data!");
            }

            return listOfMovies;
        }
    }

}
