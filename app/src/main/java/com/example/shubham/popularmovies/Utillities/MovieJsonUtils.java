package com.example.shubham.popularmovies.Utillities;

/**
 * Created by Shubham on 14-01-2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public class MovieJsonUtils{
    public static String[] getSimpleMoviesStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        //variable for holding results array;

        final String RESULTS = "results";

        final String MOVIE_TITLE = "original_title";

        /* All temperatures are children of the "temp" object */
        final String MOVIE_OVERVIEW = "overview";

        final String MOVIE_POSTER = "poster_path";

        final String MOVIE_RELEASE_DATE = "release_date";

        final String MOVIE_RATING = "vote_average";

        /* Max temperature for the day */


        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedMovieData = null;

        JSONObject forecastJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray resultsArray = forecastJson.getJSONArray(RESULTS);

        parsedMovieData = new String[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {


            /* Get the JSON object representing the day */
            JSONObject jsonObject = resultsArray.getJSONObject(i);

            String posterPath = jsonObject.getString(MOVIE_POSTER);
            posterPath = posterPath.replaceAll("(\\r|\\n)", " ");
            //posterPath = posterPath.substring(1, posterPath.length());

            String overView = jsonObject.getString(MOVIE_OVERVIEW);
            String id = jsonObject.getString("id");
            String title = jsonObject.getString(MOVIE_TITLE);
            String rating = jsonObject.getString(MOVIE_RATING);
            String release_date = jsonObject.getString(MOVIE_RELEASE_DATE);



            parsedMovieData[i] = posterPath + "---" + overView + "---" + id + "---" + title + "---" + rating + "---" + release_date;

        }

        return parsedMovieData;
    }

    public static String[] getSimpleTrailersStringFromJson(Context context, String movieJsonStr)
            throws JSONException {


        final String RESULTS = "results";


        final String TRAILER_KEY = "key";


        final String OWM_MESSAGE_CODE = "cod";


        String[] parsedTrailersData = null;
        Log.d("json", movieJsonStr);

        JSONObject forecastJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray resultsArray = forecastJson.getJSONArray(RESULTS);

        parsedTrailersData = new String[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {


            /* Get the JSON object representing the day */
            JSONObject jsonObject = resultsArray.getJSONObject(i);

            String trailersKey = jsonObject.getString(TRAILER_KEY);





            parsedTrailersData[i] = trailersKey;

        }

        return parsedTrailersData;
    }

    public static String[] getSimpleReviewsStringFromJson(Context context, String movieJsonStr)
            throws JSONException {


        final String RESULTS = "results";


        final String REVIEW_AUTHOR = "author";


        final String REVIEW_CONTENT = "content";
        final String OWM_MESSAGE_CODE = "cod";

        String[] parsedReviewsData = null;

        JSONObject forecastJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray resultsArray = forecastJson.getJSONArray(RESULTS);

        parsedReviewsData = new String[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {


            /* Get the JSON object representing the day */
            JSONObject jsonObject = resultsArray.getJSONObject(i);

            String reviewAuthor = jsonObject.getString(REVIEW_AUTHOR);
            String reviewContent = jsonObject.getString(REVIEW_CONTENT);

            reviewContent.replaceAll("(\\r|\\n)", " ");


            parsedReviewsData[i] = reviewAuthor + "---" + reviewContent;

        }

        return parsedReviewsData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}