package com.example.milkandcookies;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.ArrayList;

import okhttp3.Headers;

public class SpoonacularClient  {

    private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    private final String TAG = "SpoonacularClient";

    public SpoonacularClient() {

    }


    public void getRecipe(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        String recipeURL = BASE_URL + "&url=" + url;
        Log.d("TAG", "URL is: " + recipeURL);
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "succesful in retrieval, data is: " + json);
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed in retrieval : (");
            }
        });
    }
}
