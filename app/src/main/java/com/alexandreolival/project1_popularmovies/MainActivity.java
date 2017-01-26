package com.alexandreolival.project1_popularmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alexandreolival.project1_popularmovies.adapters.MovieAdapter;
import com.alexandreolival.project1_popularmovies.model.Movie;
import com.alexandreolival.project1_popularmovies.network.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ListItemClickedListener, LoaderManager.LoaderCallbacks<String>, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String MOVIE_DB_QUERY_URL_EXTRA = "MOVIE_DB_QUERY_URL";
    private static final int MOVIE_DB_LOADER_ID = 22;

    private MovieAdapter mMovieAdapter;

    private View mNoConnectionView;
    private ProgressBar mProgressBarLoadingMovies;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        // First load
        if (checkInternetConnectivity()) {
            showConnectedStateView(true);
            loadMoviesSortedByPopularity();
        } else {
            showConnectedStateView(false);
        }
    }

    private void initViews() {
        mProgressBarLoadingMovies = (ProgressBar) findViewById(R.id.progress_bar_loading_movies);
        mNoConnectionView = findViewById(R.id.no_connection_view);
        findViewById(R.id.button_retry_loading_movies).setOnClickListener(this);

        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movies);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMoviesSortedByPopularity() {
        Uri builtUri = Uri.parse(NetworkUtil.BASE_MOVIEDB_METADATA_URL).buildUpon()
                .appendPath(NetworkUtil.PATH_SORT_BY_POPULAR)
                .appendQueryParameter(NetworkUtil.PARAMETER_API_KEY, NetworkUtil.API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bundle queryBundle = new Bundle();
        if (url != null) {
            queryBundle.putString(MOVIE_DB_QUERY_URL_EXTRA, url.toString());
        }

        getSupportLoaderManager().restartLoader(MOVIE_DB_LOADER_ID, queryBundle, this);
    }

    private void loadMoviesSortedByRatings() {
        Uri builtUri = Uri.parse(NetworkUtil.BASE_MOVIEDB_METADATA_URL).buildUpon()
                .appendPath(NetworkUtil.PATH_SORT_BY_RATING)
                .appendQueryParameter(NetworkUtil.PARAMETER_API_KEY, NetworkUtil.API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bundle queryBundle = new Bundle();
        if (url != null) {
            queryBundle.putString(MOVIE_DB_QUERY_URL_EXTRA, url.toString());
        }

        getSupportLoaderManager().restartLoader(MOVIE_DB_LOADER_ID, queryBundle, this);
    }

    @Override
    public void onListItemClicked(Movie clickedMovieItem, View view) {
        Log.d(TAG, "Clicked movie with the following data: " + clickedMovieItem.toString());
        Log.d(TAG, "Starting DetailActivity");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, view,
                            getString(R.string.transition_movie_poster));
            startActivity(DetailActivity.getIntent(this, clickedMovieItem), options.toBundle());
        } else {
            startActivity(DetailActivity.getIntent(this, clickedMovieItem));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                if (checkInternetConnectivity()) {
                    showConnectedStateView(true);
                    loadMoviesSortedByPopularity();
                } else {
                    showConnectedStateView(false);
                }
                return true;

            case R.id.sort_highest_rated:
                if (checkInternetConnectivity()) {
                    showConnectedStateView(true);
                    loadMoviesSortedByRatings();
                } else {
                    showConnectedStateView(false);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String jsonResponse;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mProgressBarLoadingMovies.setVisibility(View.VISIBLE);
                if (args == null) {
                    return;
                }

                if (jsonResponse != null) {
                    deliverResult(jsonResponse);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(MOVIE_DB_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || searchQueryUrlString.isEmpty()) {
                    return null;
                }

                try {
                    URL url = new URL(searchQueryUrlString);
                    return NetworkUtil.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String jsonResponse) {
                this.jsonResponse = jsonResponse;
                super.deliverResult(jsonResponse);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mProgressBarLoadingMovies.setVisibility(View.GONE);
        List<Movie> movieArrayList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(data);
            JSONArray jsonMoviesArray = jsonResponse.getJSONArray("results");
            //Log.d(TAG, "Request result: " + jsonResponse);
            //Log.d(TAG, "Request array length: " + jsonMoviesArray.length());
            JSONObject movieJson;
            for (int i = 0, length = jsonMoviesArray.length(); i < length; i++) {
                movieJson = jsonMoviesArray.getJSONObject(i);
                //Log.d(TAG, "JSON Object: " + movieJson);
                movieArrayList.add(
                  new Movie(
                          movieJson.getString("poster_path"),
                          movieJson.getString("title"),
                          movieJson.getString("release_date"),
                          movieJson.getString("vote_average"),
                          movieJson.getString("overview")
                  )
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMovieAdapter.setMovieList(movieArrayList);
        if (!movieArrayList.isEmpty()) {
            showConnectedStateView(true);
        } else {
            showConnectedStateView(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {}

    private boolean checkInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry_loading_movies:
                if (checkInternetConnectivity()) {
                    showConnectedStateView(true);
                    loadMoviesSortedByPopularity();
                } else {
                    if (mProgressBarLoadingMovies.getVisibility() == View.VISIBLE) {
                        mProgressBarLoadingMovies.setVisibility(View.GONE);
                    }
                    Toast.makeText(this, getString(R.string.toast_internet_connectivity_error),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showConnectedStateView(boolean isConnected) {
        mRecyclerView.setVisibility(isConnected ? View.VISIBLE: View.GONE);
        mNoConnectionView.setVisibility(isConnected ? View.GONE : View.VISIBLE);
    }
}
