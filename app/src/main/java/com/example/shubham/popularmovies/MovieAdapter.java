package com.example.shubham.popularmovies;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.zip.Inflater;

/**
 * Created by Shubham on 14-01-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String [] mMovieData;
    private String mDataFromCursor;
    private Context currentContext;
    private Cursor mCursor;

    private String BASE_URL_FOR_LOADING_IMAGE = "http://image.tmdb.org/t/p/w185//";
    public MovieOnClickHandler mClickHandler;

    public interface MovieOnClickHandler{
        void OnClick(String movieClicked);
    }



    public MovieAdapter(Context context, MovieOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        this.currentContext = context;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        public ImageView movieImageView;


        public MovieAdapterViewHolder (View view)
        {
            super(view);
            movieImageView = (ImageView)view.findViewById(R.id.iv_movie_view);
            movieImageView.setOnClickListener(this);
        }

        public void onClick(View view)
        {
            int adapterPosition = getAdapterPosition();

            if(mCursor !=null)
            {
                String movieImagePath;
                String movieOverView;
                String movieId;
                String movieTitle;
                String movieRating;
                String movieReleaseDate;

                mCursor.moveToPosition(adapterPosition);
                movieImagePath = mCursor.getString(1);
                movieOverView = mCursor.getString(2);
                movieId = mCursor.getString(3);
                movieTitle = mCursor.getString(4);
                movieRating = mCursor.getString(5);
                movieReleaseDate = mCursor.getString(6);
                mDataFromCursor = movieImagePath + "---" + movieOverView + "---" + movieId + "---" + movieTitle + "---" + movieRating + "---" + movieReleaseDate;

                mClickHandler.OnClick(mDataFromCursor);
            }

            else mClickHandler.OnClick(mMovieData[adapterPosition]);
        }
    }

    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();
        int layoutIDForMovie = R.layout.one_movie_layout;
        boolean shouldAttachToParrentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIDForMovie, viewGroup, shouldAttachToParrentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position)
    {

        String movieToDisplay;
        String movieImagePath;


        if(mCursor != null)
        {
            mCursor.moveToPosition(position);
            movieImagePath = mCursor.getString(1);
            movieImagePath = BASE_URL_FOR_LOADING_IMAGE + movieImagePath;
        }
        else
        {
            movieToDisplay = mMovieData[position];
            movieImagePath = movieToDisplay.substring(1, movieToDisplay.indexOf("---"));
            movieImagePath = BASE_URL_FOR_LOADING_IMAGE + movieImagePath;
        }

        Picasso.with(currentContext).load(movieImagePath).error(R.mipmap.error).into(movieAdapterViewHolder.movieImageView);
    }
    public int getItemCount()
    {
        if(mCursor !=null)
            return mCursor.getCount();
        else if(mMovieData !=null)
            return mMovieData.length;

        else return 0;
    }

    public void setMovieData(String [] parsedMovieData)
    {
        mMovieData = parsedMovieData;
        notifyDataSetChanged();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


}
