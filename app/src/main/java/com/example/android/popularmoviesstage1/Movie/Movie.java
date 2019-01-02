package com.example.android.popularmoviesstage1.Movie;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName ="movies")
public class Movie implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int tableId;
    @ColumnInfo(name = "Movie")
    private String movieName;

    private String image;

    private String description;

    private String releaseDate;

    private double userRating;

    private int id;



    /**
     * No args constructor for use in serialization
     */
    @Ignore
    public Movie() {
    }

    public Movie(String movieName, String image, String description,double userRating, String releaseDate,int id) {
        this.movieName = movieName;
        this.image = image;
        this.description = description;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.id = id;

    }
//    public Movie(String movieName, String image,double userRating, String releaseDate,int id) {
//        this.tableId = tableId;
//        this.movieName = movieName;
//        this.image = image;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.userRating = userRating;
//        this.id = id;
//    }

    @Ignore
    public Movie(int tableId, String movieName) {
        this.tableId = tableId;
        this.movieName = movieName;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(movieName);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(id);
    }

    //constructor used for parcel
    public Movie(Parcel parcel){
        movieName = parcel.readString();
        image = parcel.readString();
        description = parcel.readString();
        userRating = parcel.readDouble();
        releaseDate = parcel.readString();
        id = parcel.readInt();
    }
    //creator - used when un-parceling our parcle (creating the object)
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    public int describeContents() {
        return hashCode();
    }

    public double getUserRating() {
        return userRating;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getId() {return id;}

    public int getTableId() {return tableId;}

    public void setTableId(int id) {
        this.tableId = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public void setId (int id) {this.id = id;}
}
