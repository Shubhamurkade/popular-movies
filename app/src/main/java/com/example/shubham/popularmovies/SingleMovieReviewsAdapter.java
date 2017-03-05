package com.example.shubham.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shubham on 04-02-2017.
 */

public class SingleMovieReviewsAdapter extends RecyclerView.Adapter<SingleMovieReviewsAdapter.SingleMovieReviewViewHolder>{

    private TextView mTrailersDescView;
    private String[] mReviewsData;
    private Context mCurrentContext;



    public SingleMovieReviewsAdapter(Context context){
         mCurrentContext = context;
    }

    public class SingleMovieReviewViewHolder extends RecyclerView.ViewHolder
    {

        public SingleMovieReviewViewHolder(View view)
        {
            super(view);
            mTrailersDescView = (TextView)view.findViewById(R.id.tv_single_movie_reviews);
        }

    }
    @Override
    public SingleMovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIDForReviews = R.layout.activity_single_movie_reviews_layout;
        boolean shouldAttachToParrentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIDForReviews, parent, shouldAttachToParrentImmediately);
        return new SingleMovieReviewViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SingleMovieReviewsAdapter.SingleMovieReviewViewHolder holder, int position) {

        String reviewToDisplay = mReviewsData[position];
        mTrailersDescView.setText(reviewToDisplay);
    }

    @Override
    public int getItemCount() {

        if(mReviewsData != null)
            return mReviewsData.length;
        else return 0;
    }

    public void setAdapterData(String[] reviewsData)
    {

        mReviewsData = reviewsData;
        notifyDataSetChanged();
    }
}
