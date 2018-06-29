package me.jwill2385.flicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.jwill2385.flicks.models.Config;
import me.jwill2385.flicks.models.Movie;

public class MovieListActivity extends AppCompatActivity {

    //Constants
    //Base URL for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // Parameter name for API key
    public final static String API_KEY_PARAM = "api_key";
    //Tag for all logging from this activity
    public final static String TAG = "MovieListActivity2";
    // Instance Fields
    AsyncHttpClient client;
    // the list of currently playing movies
    ArrayList<Movie> movies;
    // Recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;
    // image config
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // initialize the client
        client = new AsyncHttpClient();
        // initialize list of movies
        movies = new ArrayList<>();
        // initialize the adapter -- movie array list cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and create a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);
        // get the configuration on app creation'
        getConfiguration();

    }

    // get list of currently playing movies from API
    private void getNowPlaying() {
        // create the url
        String url = API_BASE_URL + "/movie/now_playing";
        // set up the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API_key always required

        // execute GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load results to movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    // Iterate through result set and create movie object
                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (JSONException e) {
                    logError("Failed to parse now_playing_movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, errorResponse.toString());
            }
        });

    }

    //get the configuration from the API
    private void getConfiguration() {
        // create the url
        String url = API_BASE_URL + "/configuration";
        // set up the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API_key always required

        // execute GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    config = new Config(response);

                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                    // get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed Parsing Configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, throwable.toString());
                Log.i(TAG, errorResponse.toString());
            }
        });
    }

    /*
    This Function Handles errors, logs and alerts user

    Parameters:
    String Message: tells user where problem happened
    Throwable error: base class of errors in java
    Boolean alertUser: A flag that will let me decide if I want user to see message
     */
    private void logError(String message, Throwable error, Boolean alertUser) {
// always log the error
        Log.e(TAG, message, error);
        //alert user to error
        if (alertUser) {
            //show a long toast with the error
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }


    }
}
