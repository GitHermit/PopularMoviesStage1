package com.example.android.popularmoviesstage1.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.popularmoviesstage1.Movie.Movie;

import java.util.List;


@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY tableId")
    LiveData<List<Movie>> loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movies WHERE movie = :name")
    Movie checkForDuplicates(String name);

    @Query("SELECT tableId FROM movies WHERE movie = :name")
    int getId(String name);
}
