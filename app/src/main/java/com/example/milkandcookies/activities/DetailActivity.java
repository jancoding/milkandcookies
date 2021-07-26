package com.example.milkandcookies.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.adapters.DetailFragmentAdapter;
import com.example.milkandcookies.fragments.RecipeFragment;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
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
    // recipe that the detail view is displaying
    private Recipe recipe;
    private ViewPager viewPager;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            Log.d("testing", "in on activity result");
            recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        }
    }


    // sets up the view pager for the detail activity (will be used later for original vs modified ingredients)
    private void setUpViewPager(List<Ingredient> ingredients) {
        viewPager = findViewById(R.id.viewpagerDetail);
        viewPager.setAdapter(new DetailFragmentAdapter(getSupportFragmentManager(),
                DetailActivity.this, ingredients, recipe));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs_detail);
        tabLayout.setupWithViewPager(viewPager);

    }


}