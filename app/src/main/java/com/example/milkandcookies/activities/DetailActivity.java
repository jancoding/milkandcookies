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
    private ParseObject recipe;


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
                pushNewRecipe(json);
                setUpViewPager();
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed in retrieval : (" + s + throwable.toString());

            }
        });
    }

    private void pushNewRecipe(JsonHttpResponseHandler.JSON json) {
        recipe =  ParseObject.create("Recipe");
        // put necessary recipe parameters
        recipe.put("ingredients_original", ingredients);
        Log.d(TAG, "after i put the ingredients list in the recipe this is what it is : " + ingredients.toString());
        recipe.put("owner", ParseUser.getCurrentUser());
        recipe.put("instructions", getInstructions(json.jsonObject));

        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully saved this new recipe");
                } else {
                    Log.d(TAG, "failed to save this new recipe " + e.toString());
                }
            }
        });
    }

    private JSONArray getInstructions(JSONObject object) {
        JSONArray steps_cleaned = new JSONArray();
        try {
            JSONArray steps = object.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
            for (int i = 0; i < steps.length(); i++) {
                steps_cleaned.put(steps.getJSONObject(i).getString("step"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return steps_cleaned;
    }


    public JSONArray getIngredients(JsonHttpResponseHandler.JSON json) {
        JSONArray ingredients = new JSONArray();
        try {
            JSONArray extendedIngredients = json.jsonObject.getJSONArray("extendedIngredients");
            for (int i = 0; i < extendedIngredients.length(); i++) {
                JSONObject ingredient = extendedIngredients.getJSONObject(i);
                ingredients.put(createIngredient(ingredient));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    private Ingredient createIngredient(JSONObject object) {
        // creates a new ingredient and adds it to the database
        Ingredient ingredient_parse = new Ingredient();
        try {
            ingredient_parse.setOriginal(object.getString("originalString"));
            ingredient_parse.setName(object.getString("name"));
            ingredient_parse.setUSAmount(object.getJSONObject("measures").getJSONObject("us").getInt("amount"));
            ingredient_parse.setMetricAmount(object.getJSONObject("measures").getJSONObject("metric").getInt("amount"));
            ingredient_parse.setUSUnit(object.getJSONObject("measures").getJSONObject("us").getString("unitShort") + "");
            ingredient_parse.setMetricUnit(object.getJSONObject("measures").getJSONObject("metric").getString("unitShort"));
            ingredient_parse.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ingredient_parse;
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