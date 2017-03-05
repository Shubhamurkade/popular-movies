package com.example.shubham.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubham.popularmovies.DatabaseUtilities.MoviesContract;
import com.example.shubham.popularmovies.Utillities.MovieJsonUtils;
import com.example.shubham.popularmovies.Utillities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by Shubham on 12-02-2017.
 */

public class SingleMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<String[]>, SingleMovieTrailersAdapter.TrailerClickHandler{

    private TextView mMovieTitleView;
    private ImageView mMovieImageView;
    private TextView mMovieDescription;
    private TextView mMovieRating;
    private TextView mMovieReleaseDate;
    private String BASE_URL_FOR_LOADING_IMAGE = "http://image.tmdb.org/t/p/w185//";
    private int SINGLE_MOVIE_VIDEOS_LOADER_ID = 1;
    private int SINGLE_MOVIE_REVIEWS_LOADER_ID = 2;
    private String MOVIE_ID_STRING = "movieId";
    public String SINGLE_MOVIE_VIDEOS_QUERY = "videos";
    private String SINGLE_MOVIE_REVIEWS_QUERY = "reviews";
    private String SINGLE_MOVIE_VIDEOS_OR_REVIEWS = "videosOrReviews";
    private RecyclerView mReclyclerViewForTrailers;
    private SingleMovieTrailersAdapter mTrailersAdapter;
    private String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";

    private RecyclerView mRecyclerViewForReviews;
    private SingleMovieReviewsAdapter mSingleMovieReviewsAdapter;
    private Button mFavouritesButton;

    private String mAddToFavourites = "Add To Favourites";
    private String mRemoveFromFavourites = "Remove From Favourites";


    private String mMovieDescString;
    private String mMovieIdString;
    private String mMovieTitleString;
    private String mMovieRatingString;
    private String mMovieReleaseDateString;
    private String mMovieImagePath;

    String[] mTrailersData = null;
    String[] mReviewsData = null;

    public static boolean mDualPane = false;
    private static String mMovieClicked;

    public static SingleMovieFragment newInstance(String movieClicked, boolean dualPane) {
        SingleMovieFragment f = new SingleMovieFragment();
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_single_movie_fragment, container, false);
        mMovieImageView = (ImageView) rootView.findViewById(R.id.iv_single_movie_view);

        mMovieTitleView = (TextView)rootView.findViewById(R.id.tv_movie_title);
        mMovieDescription = (TextView)rootView.findViewById(R.id.tv_movie_description);
        mMovieRating = (TextView)rootView.findViewById(R.id.tv_movie_rating);
        mMovieReleaseDate = (TextView)rootView.findViewById(R.id.tv_movie_release_date);

        //Recylcler views for trailers and reviews

        mReclyclerViewForTrailers = (RecyclerView)rootView.findViewById(R.id.recyclerview_trailers_movie);
        mRecyclerViewForReviews = (RecyclerView)rootView.findViewById(R.id.recyclerview_reviews_movie);

        String [] movieArray = new String[4];

        movieArray = mMovieClicked.split("---");
        String movieImagePath = movieArray[0];

        mMovieImagePath = movieImagePath;
        movieImagePath =  BASE_URL_FOR_LOADING_IMAGE + movieImagePath;

        mMovieDescString = movieArray[1];
        mMovieIdString = movieArray[2];
        mMovieTitleString = movieArray[3];
        mMovieRatingString = movieArray[4];
        mMovieReleaseDateString = movieArray[5];

        mMovieTitleView.setText(mMovieTitleString);
        mMovieDescription.setText(mMovieDescString);
        mMovieRating.append(mMovieRatingString);
        mMovieReleaseDate.append(mMovieReleaseDateString);


        mTrailersAdapter = new SingleMovieTrailersAdapter(getContext(), this, mMovieIdString);
        mReclyclerViewForTrailers.setAdapter(mTrailersAdapter);
        LinearLayoutManager linearLayoutManagerForTrailers = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mReclyclerViewForTrailers.setLayoutManager(linearLayoutManagerForTrailers);


        mSingleMovieReviewsAdapter = new SingleMovieReviewsAdapter(getContext());
        mRecyclerViewForReviews.setAdapter(mSingleMovieReviewsAdapter);
        LinearLayoutManager linearLayoutManagerForReviews = new LinearLayoutManager(getContext());
        mRecyclerViewForReviews.setLayoutManager(linearLayoutManagerForReviews);

        //favourites button
        mFavouritesButton = (Button)rootView.findViewById(R.id.bt_favourites_button);
        Picasso.with(mMovieImageView.getContext()).load(movieImagePath).into(mMovieImageView);
        loadTrailersAndVideosData(mMovieIdString);

        Button bt=(Button)rootView.findViewById(R.id.bt_favourites_button);
        bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ChangeMovieInDb(v);
            }

        });

        WrapperCheckIfMovieInDbAndChangeButtonText();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDualPane = getArguments().getBoolean("isDual");
        mMovieClicked = getArguments().getString("movieClicked");
        Log.d("movieClickedFragment", mMovieClicked);

    }

    public void loadTrailersAndVideosData(String movieId)
    {
        LoaderManager.LoaderCallbacks<String[]> callbackForVideos = this;
        Bundle movieBundleForVideos = new Bundle();
        movieBundleForVideos.putString(MOVIE_ID_STRING, movieId);
        movieBundleForVideos.putString(SINGLE_MOVIE_VIDEOS_OR_REVIEWS, SINGLE_MOVIE_VIDEOS_QUERY);
        getLoaderManager().initLoader(SINGLE_MOVIE_VIDEOS_LOADER_ID, movieBundleForVideos, callbackForVideos);


        LoaderManager.LoaderCallbacks<String[]> callbackForReviews = this;
        Bundle movieBundleForReviews = new Bundle();
        movieBundleForReviews.putString(MOVIE_ID_STRING, movieId);
        movieBundleForReviews.putString(SINGLE_MOVIE_VIDEOS_OR_REVIEWS, SINGLE_MOVIE_REVIEWS_QUERY);
        getLoaderManager().initLoader(SINGLE_MOVIE_REVIEWS_LOADER_ID, movieBundleForReviews, callbackForReviews);

    }

    @Override
    public Loader<String[]> onCreateLoader(final int id, final Bundle movieIdBundle) {

        return new AsyncTaskLoader<String[]>(getContext()) {

            @Override
            protected void onStartLoading() {
                if(id == SINGLE_MOVIE_VIDEOS_LOADER_ID)
                {
                    if(mTrailersData != null)
                        deliverResult(mTrailersData);
                    else
                        forceLoad();
                }
                else
                {
                    if(mReviewsData != null)
                        deliverResult(mReviewsData);
                    else
                        forceLoad();
                }

            }


            @Override
            public String[] loadInBackground() {

                String movieIdString = movieIdBundle.getString(MOVIE_ID_STRING);
                String reviewOrVideo = movieIdBundle.getString(SINGLE_MOVIE_VIDEOS_OR_REVIEWS);
                URL trailersOrReviewsDataUrl = NetworkUtils.buildUrlForTrailersAndReviews(movieIdString, reviewOrVideo);


                try {
                    String jsonTrailersOrVideosResponse = NetworkUtils
                            .getResponseFromHttpUrl(trailersOrReviewsDataUrl);
                    Log.v("data", jsonTrailersOrVideosResponse);
                    String[] simpleJsonReviewsOrVideosData;

                    if(id == SINGLE_MOVIE_VIDEOS_LOADER_ID)
                    {
                        simpleJsonReviewsOrVideosData = MovieJsonUtils.getSimpleTrailersStringFromJson(getContext(), jsonTrailersOrVideosResponse);
                        Log.d("json response", jsonTrailersOrVideosResponse);
                    }
                    else
                    {
                        simpleJsonReviewsOrVideosData = MovieJsonUtils.getSimpleReviewsStringFromJson(getContext(), jsonTrailersOrVideosResponse);
                        Log.d("json response", jsonTrailersOrVideosResponse);
                    }



                    return simpleJsonReviewsOrVideosData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {

                super.deliverResult(data);
            }
        };




    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        if(loader.getId() == SINGLE_MOVIE_VIDEOS_LOADER_ID)
            mTrailersAdapter.setAdapterData(data);
        else
            mSingleMovieReviewsAdapter.setAdapterData(data);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    public void OnClick(String trailerClicked) {

        String trailerKey = trailerClicked;
        Uri youtubleLink = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter("v", trailerKey).build();
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, youtubleLink);
        startActivity(videoIntent);
    }


    public void ChangeMovieInDb(View view)
    {

        String [] selectionArgs = {mMovieIdString};

        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(mMovieIdString).build();

        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MoviesEntry.MOVIE_IMAGE_PATH, mMovieImagePath);
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_DESCRIPTION, mMovieDescString);
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_ID, mMovieIdString);
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_TITLE, mMovieTitleString);
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_RATING, mMovieRatingString);
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, mMovieReleaseDateString);


        if(CheckIfMovieInDbAndChangeButtonText(uri, selectionArgs) == false)
        {

            getActivity().getContentResolver().insert(uri, contentValues);

        }
        else
        {
            getActivity().getContentResolver().delete(uri, MoviesContract.MoviesEntry.MOVIE_ID+"=?", selectionArgs);

        }


    }

    private boolean CheckIfMovieInDbAndChangeButtonText(Uri uri, String[] selectionArgs)
    {

        Cursor queryResult = getActivity().getContentResolver().query(uri, null, MoviesContract.MoviesEntry.MOVIE_ID+"=?",
                selectionArgs, null);

        if(queryResult.getCount()>0)
        {
            mFavouritesButton.setText(mAddToFavourites);
            return true;
        }
        else
        {
            mFavouritesButton.setText(mRemoveFromFavourites);
            return false;
        }

    }

    private void WrapperCheckIfMovieInDbAndChangeButtonText()
    {
        String [] selectionArgs = {mMovieIdString};

        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(mMovieIdString).build();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.MOVIE_ID, mMovieIdString);

        Cursor queryResult = getActivity().getContentResolver().query(uri, null, MoviesContract.MoviesEntry.MOVIE_ID+" = ?",
                selectionArgs, null);

        if(queryResult.getCount()>0)
        {
            mFavouritesButton.setText(mRemoveFromFavourites);
        }
        else
        {
            mFavouritesButton.setText(mAddToFavourites);
        }
    }
}
