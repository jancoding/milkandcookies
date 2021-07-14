package com.example.milkandcookies.fragments;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    private Button btnSubmit;
    private EditText etURL;
    private final String  TAG = "ComposeFragment";
    private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    private JSONArray ingredients;
    private ParseObject recipe;


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
        etURL = view.findViewById(R.id.etURL);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecipe(etURL.getText().toString());
            }
        });
    }

    public void getRecipe(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        String recipeURL = BASE_URL + "&url=" + url;
        Log.d("TAG", "URL is: " + recipeURL);
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

    private Ingredient createIngredient(JSONObject object) {
        // creates a new ingredient and adds it to the database
        Ingredient ingredient_parse = new Ingredient();
        try {
            ingredient_parse.setOriginal(object.getString("originalString"));
            ingredient_parse.setName(object.getString("name"));
            ingredient_parse.setUSAmount(object.getJSONObject("measures").getJSONObject("us").getInt("amount"));
            ingredient_parse.setMetricAmount(object.getJSONObject("measures").getJSONObject("metric").getInt("amount"));
            ingredient_parse.setUSUnit(object.getJSONObject("measures").getJSONObject("us").getString("unitShort") + "");
            ingredient_parse.put("recipe", recipe);
            ingredient_parse.setMetricUnit(object.getJSONObject("measures").getJSONObject("metric").getString("unitShort"));
            ingredient_parse.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ingredient_parse;
    }

    private void pushNewRecipe(JsonHttpResponseHandler.JSON json) {
        recipe =  ParseObject.create("Recipe");
        recipe.put("owner", ParseUser.getCurrentUser());
        recipe.put("instructions", getInstructions(json.jsonObject));
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

    private void goDetailActivity() {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}