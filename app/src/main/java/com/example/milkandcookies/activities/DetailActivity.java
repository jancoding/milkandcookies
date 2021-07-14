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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private JSONArray ingredients;
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        URL = getIntent().getStringExtra("URL");
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        ingredients = recipe.getOriginal();
        try {
            Log.d(TAG, ingredients.get(0).getClass() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpViewPager();
    }

    private void setUpViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DetailFragmentAdapter(getSupportFragmentManager(),
                DetailActivity.this, ingredients, recipe));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_detail);
        tabLayout.setupWithViewPager(viewPager);
    }

}