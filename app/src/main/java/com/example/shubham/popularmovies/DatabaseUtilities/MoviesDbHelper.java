package com.example.shubham.popularmovies.DatabaseUtilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.shubham.popularmovies.DatabaseUtilities.MoviesContract.MoviesEntry;
/**
 * Created by Shubham on 07-02-2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    // The name of the database
    private static final String DATABASE_NAME = "moviesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 6;


    // Constructor
    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID                + " INTEGER PRIMARY KEY, " +
                MoviesEntry.MOVIE_IMAGE_PATH + " TEXT NOT NULL, "+
                MoviesEntry.MOVIE_DESCRIPTION + " TEXT NOT NULL, "+
                MoviesEntry.MOVIE_ID + " TEXT NOT NULL, "+
                MoviesEntry.MOVIE_TITLE + " TEXT NOT NULL, "+
                MoviesEntry.MOVIE_RATING + " TEXT NOT NULL, "+
                MoviesEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
