package com.alexandreolival.project1_popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandreolival.project1_popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_OBJECT_EXTRA = "movie_object";
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(MOVIE_OBJECT_EXTRA)) {
            Log.d(TAG, "Got movie from intent: " + getIntent().getParcelableExtra(MOVIE_OBJECT_EXTRA).toString());
            Movie movie = getIntent().getParcelableExtra(MOVIE_OBJECT_EXTRA);

            TextView textViewTitle = (TextView) findViewById(R.id.text_view_movie_title);
            TextView textViewSynopsis = (TextView) findViewById(R.id.text_view_movie_synopsis);
            TextView textViewRating = (TextView) findViewById(R.id.text_view_movie_rating);
            TextView textViewReleaseDate = (TextView) findViewById(R.id.text_view_movie_release_date);
            ImageView imageViewPoster = (ImageView) findViewById(R.id.image_view_movie_detail_poster);

            Picasso.with(this).load(movie.getPosterUri()).into(imageViewPoster);
            textViewTitle.setText(movie.getTitle());
            textViewSynopsis.setText(movie.getSynopsis());
            String score = getString(R.string.vote_average_maximum_score, movie.getVoteAverage());
            textViewRating.setText(score);
            textViewReleaseDate.setText(movie.getReleaseDate());
        } else {
            Toast.makeText(this, getString(R.string.toast_movie_detail_error), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getIntent(Context context, Parcelable movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MOVIE_OBJECT_EXTRA, movie);
        return intent;
    }

}
