package com.example.android.popularmoviesstage1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.example.android.popularmoviesstage1.Movie.Movie;
import com.example.android.popularmoviesstage1.Utils.NetworkUtilities;
import com.example.android.popularmoviesstage1.Utils.ParseUtility;
import com.example.android.popularmoviesstage1.database.AppDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String JSONDATA = "results";
    private static final String BOOLEANRESULTS = "boolean results";
    private static int URL;
    private static final String URLKEY = "url results";
    /**
     * Tag for the log messages
     */
    static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * initial query upon loading the app.
     */
    public final static String mostPopularUrl = "https://api.themoviedb.org/3/movie/popular?api_key=cb265f74561b7516d002533b4b647700";


    public final static String highestRatedUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=cb265f74561b7516d002533b4b647700";

    public final static String baseImageUrl = "http://image.tmdb.org/t/p/w185/";

    public final static String baseMovieUrl = "https://api.themoviedb.org/3/movie/";

    public final static String videos = "/videos?";

    public final static String apiKey = "api_key=cb265f74561b7516d002533b4b647700";

    public final static String reviews = "/reviews?";

    public final static String baseYoutube = "https://www.youtube.com/watch?v=";

    public boolean favorites = false;

    private static ArrayList<String> parsedMovies = new ArrayList<>();
    public static ArrayList<String> urls = new ArrayList<>();
    public static ArrayList<Movie> movie = new ArrayList<>();


    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            findMovieData(mostPopularUrl);
        }


        gridView = findViewById(R.id.movies_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                launchDetailActivity(position);
            }
        });


        if (savedInstanceState != null) {
            favorites = savedInstanceState.getBoolean(BOOLEANRESULTS);
            if (!favorites) {
                ArrayList<String> moviesList = savedInstanceState.getStringArrayList(JSONDATA);
                movieInfo(moviesList);
            }
            else {
                setupViewModel();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.most_popular) {
            favorites = false;
            findMovieData(mostPopularUrl);
        } else if (menuItemThatWasSelected == R.id.high_ratings) {
            favorites = false;
            findMovieData(highestRatedUrl);
        } else if (menuItemThatWasSelected == R.id.favorites) {
            favorites = true;
            setupViewModel();
        }
        return true;
    }

    public class MoviesTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null && !results.equals("")) {
                ArrayList<String> movies = ParseUtility.parseMovieJson(results);
                parsedMovies = movies;
                movieInfo(movies);
            }
        }
    }

    public void movieInfo(ArrayList<String> movies) {

        movie.clear();
        urls.clear();
        for (int i = 0; i < movies.size(); i++) {
            try {
                String movieDetails = movies.get(i);
                JSONObject currentMovie = new JSONObject(movieDetails);
                String title = currentMovie.getString("original_title");
                int id = currentMovie.getInt("id");
                String image = currentMovie.getString("poster_path");
                urls.add(image);
                String overview = currentMovie.getString("overview");
                double popularity = currentMovie.getDouble("vote_average");
                String release = currentMovie.getString("release_date");
                Movie movieData = new Movie(title, image, overview, popularity, release, id);
                movie.add(i, movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        loadImages();
    }

    public void loadImages() {
        gridView.setAdapter(new ImageAdapter(this, urls));
    }

    public void findMovieData(String pickedUrl) {
        URL url = createUrl(pickedUrl);
        new MoviesTask().execute(url);
    }


    public URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "URL error", exception);
            return null;
        }
        return url;
    }


    private void launchDetailActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra("Movie", movie.get(position));
        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BOOLEANRESULTS,favorites);
        outState.putStringArrayList(JSONDATA, parsedMovies);
    }

    public void setupViewModel() {


        DatabaseViewModel viewModel = ViewModelProviders.of(this).get(DatabaseViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> savedMovies) {

                if (favorites){
                movie.clear();
                urls.clear();

                for (int i = 0; i < savedMovies.size(); i++){
                    movie.add(i, savedMovies.get(i));
                    urls.add(savedMovies.get(i).getImage());
                }

                gridView.setAdapter(new ImageAdapter(MainActivity.this, urls ));

            }}
        });
    }
}