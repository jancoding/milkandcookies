package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.example.milkandcookies.adapters.DetailFragmentAdapter;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

import static android.view.View.GONE;

public class DetailActivity extends AppCompatActivity {

    private String URL;
    private final String TAG = "DetailActivity";
    private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    private ArrayList<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        URL = getIntent().getStringExtra("URL");
        getRecipe(URL);

    }


    public void getRecipe(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        String recipeURL = BASE_URL + "&url=" + url;
        Log.d("TAG", "URL is: " + recipeURL);
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "succesful in retrieval, data is: " + json);
                ingredients = getIngredients(json);
                Log.d(TAG, ingredients.toString());
                setUpViewPager();
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed in retrieval : (" + s + throwable.toString());

            }
        });
    }

    public ArrayList<Ingredient> getIngredients(JsonHttpResponseHandler.JSON json) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            JSONArray extendedIngredients = json.jsonObject.getJSONArray("extendedIngredients");
            for (int i = 0; i < extendedIngredients.length(); i++) {
                JSONObject ingredient = extendedIngredients.getJSONObject(i);
                ingredients.add(new Ingredient(ingredient.getString("originalString"), ingredient.getString("name")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    private void setUpViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DetailFragmentAdapter(getSupportFragmentManager(),
                DetailActivity.this, ingredients));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_detail);
        tabLayout.setupWithViewPager(viewPager);


    }

}