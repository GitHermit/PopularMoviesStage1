package com.example.android.popularmoviesstage1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.Movie.Movie;
import com.example.android.popularmoviesstage1.Utils.NetworkUtilities;
import com.example.android.popularmoviesstage1.Utils.ParseUtility;
import com.example.android.popularmoviesstage1.database.AppDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmoviesstage1.MainActivity.LOG_TAG;
import static com.example.android.popularmoviesstage1.MainActivity.apiKey;
import static com.example.android.popularmoviesstage1.MainActivity.baseImageUrl;
import static com.example.android.popularmoviesstage1.MainActivity.baseMovieUrl;
import static com.example.android.popularmoviesstage1.MainActivity.baseYoutube;
import static com.example.android.popularmoviesstage1.MainActivity.movie;
import static com.example.android.popularmoviesstage1.MainActivity.reviews;
import static com.example.android.popularmoviesstage1.MainActivity.videos;


public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    private static final String LIFECYCLE_PARSED_DATA = "results";

    public static String keyOne = "";
    public static String keyTwo = "";
    public static String keyThree = "";
    public static String reviewString = "";
    public static Movie currentMovie;
    public static boolean duplicateFound;
    public static String movieName;



    private Button trailer2;
    private Button trailer3;
    private Button trailer1;
    private Button favorite;
    private TextView reviewOne;
    private TextView reviewTwo;
    private ArrayList<String> parsedTrailers;
    private ArrayList<String> parsedReviews;

    private AppDatabase mDb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        TextView title = findViewById(R.id.titletv);
        ImageView poster = findViewById(R.id.posteriv);
        TextView overview = findViewById(R.id.descriptiontv);
        TextView rating = findViewById(R.id.ratingstv);
        TextView date = findViewById(R.id.releasetv);
        TextView attribution = findViewById(R.id.attribute);
        trailer1 = findViewById(R.id.trailer1);
        favorite = findViewById(R.id.favorite);
        trailer2 = findViewById(R.id.trailer2);
        trailer3 = findViewById(R.id.trailer3);
        reviewOne = findViewById(R.id.reviewOne);
        reviewTwo = findViewById(R.id.reviewTwo);
        double review;
        int id;
        String releaseDate = "Release date: ";
        String reviewTotal = "/10";


        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
        }
        if (savedInstanceState != null){
            parsedReviews = savedInstanceState.getStringArrayList("Review");
            parsedTrailers = savedInstanceState.getStringArrayList("Trailer");

            trailer1.setVisibility(View.GONE);
            trailer2.setVisibility(View.GONE);
            trailer3.setVisibility(View.GONE);

            if (parsedReviews.size() == 0) {
                reviewOne.setText("No one has reviewed this film on themoviedb.org yet!");
            }

            reviewInfo(parsedReviews);
            youtubeInfo(parsedTrailers);
        }

        final Movie movie = intent.getParcelableExtra("Movie");
        currentMovie = movie;
        movieName = movie.getMovieName();
        title.setText(movieName);
        overview.setText(movie.getDescription());
        review = movie.getUserRating();
        reviewTotal = String.valueOf(review) + reviewTotal;
        rating.setText(reviewTotal);
        releaseDate = releaseDate + movie.getReleaseDate();
        date.setText(releaseDate);
        attribution.setText(R.string.attribute);
        id = movie.getId();
        duplicateFound = false;

        if (savedInstanceState == null) {
            trailer1.setVisibility(View.GONE);
            trailer2.setVisibility(View.GONE);
            trailer3.setVisibility(View.GONE);
            reviewOne.setText("No one has reviewed this film on themoviedb.org yet!");

            String videoUrl = baseMovieUrl + id + videos + apiKey;
            String reviewUrl = baseMovieUrl + id + reviews + apiKey;
            URL newUrl = createUrl(videoUrl);
            new MediaTask().execute(newUrl);
            URL reviewrl = createUrl(reviewUrl);
            new ReviewTask().execute(reviewrl);

        }

        Picasso.with(this)
                .load(baseImageUrl + movie.getImage())
                .into(poster);



        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onFavoriteButtonClicked(currentMovie);
            }
        });

        trailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTrailer(keyOne);
            }
        });
        trailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTrailer(keyTwo);
            }
        });
        trailer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTrailer(keyThree);
            }
        });
        setupFavoriteText();
    }


    public class MediaTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try{
                searchResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);

            }catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null && !results.equals("")) {
                ArrayList<String> trailers = ParseUtility.parseMovieJson(results);
                parsedTrailers = trailers;
                youtubeInfo(trailers);
                }

            }
        }

        public void onFavoriteButtonClicked(Movie name) {

        final String movieName = name.getMovieName();
        String description = name.getDescription();
        String image = name.getImage();
        Double rating = name.getUserRating();
        String release = name.getReleaseDate();
        int id = name.getId();
        final Movie movie = new Movie(movieName,image,description,rating,release,id);
        mDb = AppDatabase.getsInstance(getApplicationContext());


            AppExecutors.getInstance().diskIo().execute(new Runnable() {
            @Override
            public void run() {

                if (!duplicateFound){
                    mDb.movieDao().insertMovie(movie);
                    runOnUiThread(new Runnable() {
                        public void run() {
                                Toast.makeText(DetailActivity.this, "Added to favorites successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {

                    int id = mDb.movieDao().getId(movieName);
                    mDb.movieDao().loadAllMovies();
                    Movie deleteMovie = new Movie(id,movieName);
                    mDb.movieDao().deleteMovie(deleteMovie);
                    duplicateFound = false;
                    runOnUiThread(new Runnable() {
                        public void run() {
                                Toast.makeText(DetailActivity.this, "Removed from favorites successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });

        }

        private void showThreeTrailers(){

            trailer1.setVisibility(View.VISIBLE);

            trailer2.setVisibility(View.VISIBLE);

            trailer3.setVisibility(View.VISIBLE);
        }

        private void showTwoTrailers() {
            trailer1.setVisibility(View.VISIBLE);

            trailer2.setVisibility(View.VISIBLE);
        }
        private void showOneTrailer() {

            trailer1.setVisibility(View.VISIBLE);
    }


    public class ReviewTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try{
                searchResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);

            }catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }


        @Override
        protected void onPostExecute(String results) {
            if (results != null && !results.equals("")) {
                ArrayList<String> reviews = ParseUtility.parseMovieJson(results);
                parsedReviews = reviews;
                reviewInfo(reviews);
            }

        }
    }


    private void youtubeInfo (ArrayList<String> trailers) {
        try {
            if (trailers.size() == 1) {
                String firstTrailer = trailers.get(0);
                JSONObject firstMovieTrailer = new JSONObject(firstTrailer);
                keyOne = firstMovieTrailer.getString("key");
                showOneTrailer();
            }
            if (trailers.size() >= 2) {
                showTwoTrailers();
                String firstTrailer = trailers.get(0);
                JSONObject firstMovieTrailer = new JSONObject(firstTrailer);
                keyOne = firstMovieTrailer.getString("key");
                String secondTrailer = trailers.get(1);
                JSONObject secondMovieTrailer = new JSONObject(secondTrailer);
                keyTwo = secondMovieTrailer.getString("key");
                if (trailers.size() >= 3) {
                    showThreeTrailers();
                    String thirdTrailer = trailers.get(2);
                    JSONObject thirdMovieTrailer = new JSONObject(thirdTrailer);
                    keyThree = thirdMovieTrailer.getString("key");
                }

            }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void reviewInfo (ArrayList<String> review) {
        try {
            if (review.size() > 0) {
                String firstReviewData = review.get(0);
                JSONObject firstReview = new JSONObject(firstReviewData);
                reviewString = firstReview.getString("content");
                String author = firstReview.getString("author");
                reviewString = reviewString + " \n\nReviewed by " + author +
                        "\n______________________";
                reviewOne.setText(reviewString);
                if (review.size() > 1) {
                    String secondReviewData = review.get(1);
                    JSONObject secondReview = new JSONObject(secondReviewData);
                    reviewString = secondReview.getString("content");
                    author = secondReview.getString("author");
                    reviewString = reviewString + " \n\nReviewed by " + author;
                    reviewTwo.setText(reviewString);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }


    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void openTrailer (String url) {
        Uri link = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, link);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void onClickTrailer(String key) {
        String url = baseYoutube + key;
        openTrailer(url);
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("Trailer",parsedTrailers);
        outState.putStringArrayList("Review",parsedReviews);


    }
    public void setupFavoriteText() {


        DatabaseViewModel viewModel = ViewModelProviders.of(this).get(DatabaseViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> savedMovies) {
                if (!savedMovies.isEmpty()) {
                    for (int i = 0; i < savedMovies.size(); i++) {
                        if (currentMovie.getMovieName().equals(savedMovies.get(i).getMovieName())){
                            duplicateFound = true;
                            favorite.setText(R.string.un_favorite);
                            break;
                        }
                        else {
                            favorite.setText(R.string.add_favorite);
                        }
                    }
                }else {
                    favorite.setText(R.string.add_favorite);
                }
                }
        });
    }

}


