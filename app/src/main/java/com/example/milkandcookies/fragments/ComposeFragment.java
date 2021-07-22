package com.example.milkandcookies.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.activities.DetailActivity;
import com.example.milkandcookies.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.Headers;

// fragment to compose a new recipe
public class ComposeFragment extends Fragment {

    private Button btnSubmit;
    private EditText etURL;
    private final String  TAG = "ComposeFragment";
    private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=";
    private ParseObject recipe;
    ProgressBar pb;



    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        etURL = view.findViewById(R.id.etURL);
        // listener for submit button to push new recipe to parse database
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(ProgressBar.VISIBLE);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(btnSubmit, "alpha", 0.2f);
                fadeAnim.start();
                getRecipe(etURL.getText().toString());
            }
        });
    }

    // sends get request for spoonacular API to retrieve recipe from website
    public void getRecipe(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append(getString(R.string.spoonacular_key)).append("&url=").append(url).append("&analyze=true");
        String recipeURL = urlBuilder.toString();
        Log.d(TAG, "URL is: " + recipeURL);
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "succesful in retrieval, data is: " + json);
                pushNewRecipe(json);
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed in retrieval : (" + s + throwable.toString());
            }
        });
    }

    // retrieves ingredients from json response of spoonacular api
    private JSONArray getIngredients(JsonHttpResponseHandler.JSON json) {
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

    // creates new ingredients by specifying required fields and pushing to parse
    private Ingredient createIngredient(JSONObject object) {
        // creates a new ingredient and adds it to the database
        Ingredient ingredient_parse = new Ingredient();
        try {
            String originalString = object.getString("originalString");
            ingredient_parse.setOriginal(originalString);
            ingredient_parse.put("display_original", originalString);
            ingredient_parse.put("display_modified", originalString);
            ingredient_parse.setName(object.getString("name").replaceAll("[^a-zA-Z0-9\\-\\s+]", ""));
            ingredient_parse.setUSAmount(object.getJSONObject("measures").getJSONObject("us").getDouble("amount"));
            ingredient_parse.setMetricAmount(object.getJSONObject("measures").getJSONObject("metric").getDouble("amount"));
            ingredient_parse.setUSUnit(object.getJSONObject("measures").getJSONObject("us").getString("unitShort") + "");
            ingredient_parse.put("recipe", recipe);
            ingredient_parse.setMetricUnit(object.getJSONObject("measures").getJSONObject("metric").getString("unitShort"));
            ingredient_parse.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ingredient_parse;
    }

    // pushes a new recipe to the parse website
    private void pushNewRecipe(JsonHttpResponseHandler.JSON json) {
        recipe =  ParseObject.create("Recipe");
        recipe.put("owner", ParseUser.getCurrentUser());
        recipe.put("instructions", getInstructions(json.jsonObject));
        try {
            recipe.put("vegan", json.jsonObject.getBoolean("vegan"));
            recipe.put("vegetarian", json.jsonObject.getBoolean("vegetarian"));
            recipe.put("gluten_free", json.jsonObject.getBoolean("glutenFree"));
            recipe.put("dairy_free", json.jsonObject.getBoolean("dairyFree"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            recipe.put("title", json.jsonObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully saved this new recipe");
                    getIngredients(json);
                    goDetailActivity();
                } else {
                    Log.d(TAG, "failed to save this new recipe " + e.toString());
                }
            }
        });
    }

    // retrieves ingredients by steps from json response of spoonacular
    private JSONArray getInstructions(JSONObject object) {
        JSONArray steps_cleaned = new JSONArray();
        try {
            JSONArray steps = object.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
            for (int i = 0; i < steps.length(); i++) {
                steps_cleaned.put(steps.getJSONObject(i).getString("step").replaceAll("[^a-zA-Z0-9\\s+]", ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return steps_cleaned;
    }

    // transitions to the detail activity specifying the recipe
    private void goDetailActivity() {
        pb.setVisibility(ProgressBar.INVISIBLE);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}