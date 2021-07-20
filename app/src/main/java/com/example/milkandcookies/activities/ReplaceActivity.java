package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.net.Proxy.Type.HTTP;

// Activity for users selecting replacements for ingredients
public class ReplaceActivity extends AppCompatActivity {

    private RadioGroup rgOptions;
    // base url for spponacular api substitutes TODO: put API key in secrets.xml file
    private final String BASE_URL = "https://api.spoonacular.com/food/ingredients/substitutes?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    private Ingredient ingredient;
    private final String TAG = "ReplaceActivity";
    private Recipe recipe;
    private JSONArray ingredients = new JSONArray();
    private Button btnSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace);
        rgOptions = findViewById(R.id.rgOptions);
        ingredient = (Ingredient) getIntent().getSerializableExtra(
                "ingredient");
        btnSelect = findViewById(R.id.btnSelect);
        recipe = (Recipe) ingredient.getParseObject("recipe");
        getReplacements(ingredient);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selected = findViewById(rgOptions.getCheckedRadioButtonId());
                Log.d("in here", selected.getText().toString());
                ingredient.put("display_modified", ingredient.getString("display_modified") + " (" + selected.getText() + ")");
                ingredient.saveInBackground();
                goRecipeActivity();
            }
        });
    }

    private void goRecipeActivity() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("recipe", (Serializable) recipe);
        setResult(200, intent);
        finish();
    }

    // retrieves replacements from spoonacular API
    public void getReplacements(Ingredient ingredient) {
        AsyncHttpClient client = new AsyncHttpClient();
        String recipeURL = BASE_URL + "&ingredientName=" + ingredient.getOriginal();
        Log.d("TAG", "URL is: " + recipeURL);
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "successfully grabbed replacements " + json.toString());
                getReplacementsfromJSON(json);
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed : ( " + s + throwable.toString());
            }
        });
    }

    // parses JSON response from spoonacular api and creates buttons for every replacement option
    private void getReplacementsfromJSON(JsonHttpResponseHandler.JSON json) {
        JSONObject jsonObject = json.jsonObject;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("substitutes");
            addRadioButtons(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // adds radio buttons to view for every substitute in JSONArray input
    public void addRadioButtons(JSONArray substitutes) {
        rgOptions.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < substitutes.length(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(View.generateViewId());
            try {
                rdbtn.setText(substitutes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rgOptions.addView(rdbtn);
        }
    }

    private void BonAPITest() {
        final MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        Log.d("random", "{\"ingredients\":" + ingredients.toString() + "}");
        RequestBody body = RequestBody.create(JSON, "{\"ingredients\":" + ingredients.toString() + "}");
        Request request = new Request.Builder()
                .addHeader("Authorization", "Token 50b313b1e22fa05eb8512f6f78845d8f5ec8f4b7")
                .addHeader("Content-type", "application/json")
                .url("https://www.bon-api.com/api/v1/ingredient/alternatives/?diet=" + ParseUser.getCurrentUser().get("dietary") + "&language=en")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    processResponse(response.body().string());
                }
            }
        });
    }

    private void processResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1).replace("\\", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getIngredients() {
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.setLimit(30);
        query.whereEqualTo("recipe", recipe);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Ingredient>() {
            public void done(List<Ingredient> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    for (Ingredient ingredient: itemList) {
                        ingredients.put(ingredient.getString("display_original"));
                    }
                    BonAPITest();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

}