package com.alexandreolival.project1_popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexandreolival.project1_popularmovies.R;
import com.alexandreolival.project1_popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final static String TAG = "MovieAdapter";

    private final List<Movie> mMovieList;

    private final ListItemClickedListener mListItemClickedListener;

    public MovieAdapter(ListItemClickedListener listItemClickedListener) {
        this(new ArrayList<Movie>(), listItemClickedListener);
    }

    public MovieAdapter(List<Movie> movieList, ListItemClickedListener listItemClickedListener) {
        mMovieList = movieList;
        mListItemClickedListener = listItemClickedListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String posterUrl = mMovieList.get(position).getPosterUri();
        if(!TextUtils.isEmpty(posterUrl)) {
            Picasso.with(holder.listItem.getContext())
                    .load(posterUrl)
                    .into(holder.moviePoster);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        Log.d(TAG, "Movie list updated");
        mMovieList.clear();
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        View listItem;
        ImageView moviePoster;

        MovieViewHolder(final View itemView) {
            super(itemView);
            listItem = itemView;
            moviePoster = (ImageView) itemView.findViewById(R.id.image_view_movie_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        mListItemClickedListener.onListItemClicked(mMovieList.get(getAdapterPosition()), listItem);
                }
            });
        }
    }

    public interface ListItemClickedListener {
        void onListItemClicked(Movie clickedItem, View view);
    }

}
