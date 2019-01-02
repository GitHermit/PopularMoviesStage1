package com.example.android.popularmoviesstage1.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseUtility {

    public static ArrayList<String> parseMovieJson(String data){
        ArrayList<String> moviesArrayList = new ArrayList<String>();
        try {
            JSONObject movieParser = new JSONObject(data);
            JSONArray resultsArray = movieParser.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                moviesArrayList.add(resultsArray.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesArrayList;
    }


}
