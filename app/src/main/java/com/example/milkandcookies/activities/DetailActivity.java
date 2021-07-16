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
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.adapters.DetailFragmentAdapter;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static android.view.View.GONE;

// Detail view of a specific recipe
public class DetailActivity extends AppCompatActivity {

    // tag for log calls
    private final String TAG = "DetailActivity";
    // url for api calls TODO: replace ID in secrets.xml file
    private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    // recipe that the detail view is displaying
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // retrieves recipe and ingredients associated with the recipe
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        getIngredients();
    }

    // requests the Parse database storing ingredients for the recipe
    private void getIngredients() {
        JSONArray jsonArray = new JSONArray();
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.setLimit(30);
        query.whereEqualTo("recipe", recipe);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Ingredient>() {
            public void done(List<Ingredient> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    setUpViewPager(itemList);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    // sets up the view pager for the detail activity (will be used later for original vs modified ingredients)
    private void setUpViewPager(List<Ingredient> ingredients) {
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DetailFragmentAdapter(getSupportFragmentManager(),
                DetailActivity.this, ingredients, recipe));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_detail);
        tabLayout.setupWithViewPager(viewPager);
    }

}