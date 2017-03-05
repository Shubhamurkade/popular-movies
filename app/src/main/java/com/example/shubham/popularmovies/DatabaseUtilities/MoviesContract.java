package com.example.shubham.popularmovies.DatabaseUtilities;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Shubham on 07-02-2017.
 */

public class MoviesContract {

    //authority for this database
    public static final String AUTHORITY = "com.example.shubham.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns
    {
        // MoviesEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES).build();



        // Task table and column names
        public static final String TABLE_NAME = "movies";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below

        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_DESCRIPTION = "movie_desc";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_IMAGE_PATH = "image_path";
    }
}


