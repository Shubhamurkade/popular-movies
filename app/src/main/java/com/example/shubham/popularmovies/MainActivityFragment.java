package com.example.shubham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;


import com.example.shubham.popularmovies.DatabaseUtilities.MoviesContract;
import com.example.shubham.popularmovies.Utillities.MovieJsonUtils;
import com.example.shubham.popularmovies.Utillities.NetworkUtils;


import java.net.URL;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Shubham on 12-02-2017.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<String[]>, MovieAdapter.MovieOnClickHandler {

    public RecyclerView mRecyclerViewForMovie;
    private ImageView movieImageView;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private MovieAdapter movieAdapter;
    private String POPULAR_STRING="popular";
    private String TOP_RATED_STRING="top_rated";
    private String FAVOURITES_STRING="favourites";
    private String SORT_DATA_BY = "sort_by";
    private String mCurrentSortBy = null;
    private String[] movieData = null;
    private static final int POPULAR_LOADER_ID = 0;
    private static final int TOP_RATED_LOADER_ID =1;
    private static final int FAVOURITES_LOADER_ID = 2;

    private boolean mDualPane;
    private OnArticleSelectedListener mListener;

    public void setListener(OnArticleSelectedListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.sortmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);



        mErrorMessage = (TextView)view.findViewById(R.id.tv_error_message_display);
        mRecyclerViewForMovie = (RecyclerView) view.findViewById(R.id.recyclerview_movie);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        movieAdapter = new MovieAdapter(view.getContext(), this);
        mRecyclerViewForMovie.setAdapter(movieAdapter);

        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();


        GridLayoutManager layoutManager;
        switch(orientation) {
            case Surface.ROTATION_90:

                layoutManager = new GridLayoutManager(view.getContext(), 4);
                mRecyclerViewForMovie.setLayoutManager(layoutManager);
                break;
            default:
                layoutManager = new GridLayoutManager(view.getContext(), 2);
                mRecyclerViewForMovie.setLayoutManager(layoutManager);
                break;
        }

        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;

        if(dpWidth >= 600 && orientation == Surface.ROTATION_90)
        {
            layoutManager = new GridLayoutManager(view.getContext(), 3);
            mRecyclerViewForMovie.setLayoutManager(layoutManager);
        }

        if(savedInstanceState != null)
        {
            String sortBy = savedInstanceState.getString(SORT_DATA_BY);
            loadMovieData(sortBy);
        }
        else loadMovieData(POPULAR_STRING);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    public class LoaderForFavourites implements LoaderManager.LoaderCallbacks<Cursor>
    {
        Context mContext;

        public LoaderForFavourites(Context context)
        {
            mContext = context;
        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext,
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            movieAdapter.swapCursor(data);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {

        mLoadingIndicator.setVisibility(View.VISIBLE);

        return new AsyncTaskLoader<String[]>(getContext()) {

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    deliverResult(movieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {

                String sortBy = loaderArgs.getString(SORT_DATA_BY);
                URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    String[] simpleJsonWeatherData = MovieJsonUtils.getSimpleMoviesStringsFromJson(getContext(), jsonMovieResponse) ;
                    return simpleJsonWeatherData;

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
                movieData = data;
                super.deliverResult(movieData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        movieAdapter.setMovieData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String[]> loader) {

    }

    @Override
    public void OnClick(String movieClicked)
    {
        mListener = (OnArticleSelectedListener)getActivity();
        mListener.onArticleSelected(movieClicked, mDualPane);

    }
    private void showErrorMessage()
    {
        /* First, hide the currently visible data */
        mRecyclerViewForMovie.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loadMovieData(String sortBy) {
        showMovieDataView();
        Bundle popularString = new Bundle();
        if(sortBy == POPULAR_STRING)
        {
            popularString.putString(SORT_DATA_BY, sortBy);
            LoaderManager.LoaderCallbacks<String[]> callbackPopular = this;
            getLoaderManager().initLoader(POPULAR_LOADER_ID, popularString, callbackPopular);
            movieAdapter.swapCursor(null);
        }
        else if(sortBy == TOP_RATED_STRING)
        {
            popularString.putString(SORT_DATA_BY, sortBy);
            LoaderManager.LoaderCallbacks<String[]> callbackTopRated = this;
            getLoaderManager().initLoader(TOP_RATED_LOADER_ID, popularString, callbackTopRated);
            movieAdapter.swapCursor(null);
        }
        else
        {
            popularString.putString(SORT_DATA_BY, sortBy);
            LoaderForFavourites loaderForFavourites = new LoaderForFavourites(getContext());
            LoaderManager.LoaderCallbacks<Cursor> callbacksForFavourites = loaderForFavourites;
            getLoaderManager().initLoader(FAVOURITES_LOADER_ID, null, callbacksForFavourites);
            movieAdapter.setMovieData(null);

        }

        mCurrentSortBy = sortBy;

    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessage.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerViewForMovie.setVisibility(View.VISIBLE);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popularity) {
            movieData = null;
            movieAdapter.setMovieData(null);
            loadMovieData(POPULAR_STRING);
            return true;
        }

        else if(id == R.id.top_rated)
        {
            movieData = null;
            movieAdapter.setMovieData(null);
            loadMovieData(TOP_RATED_STRING);
            return true;
        }
        else if(id == R.id.favourites)
        {
            movieData = null;
            movieAdapter.setMovieData(null);
            loadMovieData(FAVOURITES_STRING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SORT_DATA_BY, mCurrentSortBy);
        super.onSaveInstanceState(savedInstanceState);
    }


    public interface OnArticleSelectedListener {
        public void onArticleSelected(String movieClicked, boolean dualPane);
    }


}
