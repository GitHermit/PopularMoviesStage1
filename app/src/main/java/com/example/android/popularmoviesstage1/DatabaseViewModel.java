package com.example.android.popularmoviesstage1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesstage1.Movie.Movie;
import com.example.android.popularmoviesstage1.database.AppDatabase;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
