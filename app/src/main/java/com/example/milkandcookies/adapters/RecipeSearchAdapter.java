package com.example.milkandcookies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.RecipeSearch;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.ViewHolder> {

    private ArrayList<RecipeSearch> recipesToDisplay;
    private Context context;
    private final String  TAG = "RecipeSearchAdapter";


    public RecipeSearchAdapter(Context context, ArrayList<RecipeSearch> recipes) {
        this.context = context;
        this.recipesToDisplay = recipes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipesearch, parent, false);
        // wrap it inside a View Holder and return it
        return new RecipeSearchAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(recipesToDisplay.get(position));
    }

    @Override
    public int getItemCount() {
        return recipesToDisplay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRecipeTitle;
        private ImageView ivRecipeImage;
        private ImageView ivAddRecipe;
        private final String BASE_URL = "https://api.spoonacular.com/recipes/extract?apiKey=";
        private ParseObject recipe;
        private String apiKey;



        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            ivAddRecipe = itemView.findViewById(R.id.ivAddRecipe);
            apiKey = itemView.getContext().getString(R.string.spoonacular_key);
        }

        public void bind(RecipeSearch item) {
            tvRecipeTitle.setText(item.getTitle());
            ivAddRecipe.setColorFilter(R.color.salmon);
            Glide.with(context)
                    .load(item.getImageUrl())
                    .into(ivRecipeImage);
            ivAddRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToUsersRecipes(recipesToDisplay.get(getAdapterPosition()));
                }
            });
        }


        // TODO: Restructure code so that we remove repeated code to add a new recipe
        /*
        ------------------------------------------------------------------------------
        NOTE: BELOW IS A LOT OF REPEATED CODE FROM THE COMPOSE FRAGMENT TO ADD A NEW RECIPE
        FROM A URL
        ------------------------------------------------------------------------------
         */
        private void addToUsersRecipes(RecipeSearch recipe) {
            getRecipe(recipe.getSourceUrl());
        }

        // sends get request for spoonacular API to retrieve recipe from website
        public void getRecipe(String url) {
            AsyncHttpClient client = new AsyncHttpClient();
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append(apiKey).append("&url=").append(url).append("&analyze=true");
            String recipeURL = urlBuilder.toString();
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
    }

}
