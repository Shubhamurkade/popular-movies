package com.example.shubham.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

/**
 * Created by Shubham on 04-02-2017.
 */

public class SingleMovieTrailersAdapter extends RecyclerView.Adapter<SingleMovieTrailersAdapter.SingleMovieViewHolder>  {

    private TrailerClickHandler mTrailerClickHandler;
    private ImageView mTrailersVideosView;
    private String[] mTrailersData;
    private String mCurrentMovieId;
    private Context mCurrentContext;

    private String BASE_URL_FOR_THUMBNAIL = "http://img.youtube.com/vi/";

    public interface TrailerClickHandler {
        void OnClick(String trailerClicked);
    }

    public SingleMovieTrailersAdapter(Context context, TrailerClickHandler clickHandler, String currentMovieIdString) {
        mTrailerClickHandler = clickHandler;
        mCurrentMovieId = currentMovieIdString;
    }

    public class SingleMovieViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public SingleMovieViewHolder(View view) {
            super(view);
            mTrailersVideosView = (ImageView) view.findViewById(R.id.imageViewForTrailers);
            mTrailersVideosView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            mTrailerClickHandler.OnClick(mTrailersData[adapterPosition]);

        }
    }

    @Override
    public SingleMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        mCurrentContext = context;
        int layoutIDForTrailers = R.layout.activity_single_movie_videos_layout;
        boolean shouldAttachToParrentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIDForTrailers, parent, shouldAttachToParrentImmediately);
        return new SingleMovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SingleMovieViewHolder holder, int position) {

        //mTrailersDescView.append("" + position);

        Uri uri = Uri.parse(BASE_URL_FOR_THUMBNAIL).buildUpon()
                .appendPath(mTrailersData[position])
                .appendPath("0.jpg")
                .build();

        Picasso.with(mCurrentContext)
                .load(uri)
                .config(Bitmap.Config.RGB_565)
                .into(mTrailersVideosView);
        Log.d("thumbnail", uri.toString());
    }

    @Override
    public int getItemCount() {
        if (mTrailersData != null)
            return mTrailersData.length;
        else
            return 0;
    }

    public void setAdapterData(String[] trailersData) {
        mTrailersData = trailersData;
        notifyDataSetChanged();
    }



}
