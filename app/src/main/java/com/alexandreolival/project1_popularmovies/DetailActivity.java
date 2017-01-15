package com.alexandreolival.project1_popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_OBJECT_EXTRA = "movie_object";
    private static final String TAG = "DetailActivity";

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Log.d(TAG, "Got movie from intent: " + getIntent().getParcelableExtra(MOVIE_OBJECT_EXTRA).toString());

        mTextView = (TextView) findViewById(R.id.text_view_movie_details);
        mTextView.setText(getIntent().getParcelableExtra(MOVIE_OBJECT_EXTRA).toString());
    }



    public static Intent getIntent(Context context, Parcelable movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(MOVIE_OBJECT_EXTRA, movie);
        return intent;
    }

}
