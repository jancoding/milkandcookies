package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.io.InputStream;
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
    private final String BASE_URL = "https://api.spoonacular.com/food/ingredients/substitutes?apiKey=";
    private Ingredient ingredient;
    private final String TAG = "ReplaceActivity";
    private Recipe recipe;
    private JSONArray ingredients = new JSONArray();
    private Button btnSelect;
    private TextView tvNone;
    private TextView tvReplacementTitle;
    private JSONObject database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace);
        rgOptions = findViewById(R.id.rgOptions);
        ingredient = (Ingredient) getIntent().getSerializableExtra(
                "ingredient");
        btnSelect = findViewById(R.id.btnSelect);
        recipe = (Recipe) ingredient.getParseObject("recipe");
        tvNone = findViewById(R.id.tvNone);
        tvReplacementTitle = findViewById(R.id.tvReplacementTitle);
        getReplacements(ingredient);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("milk & cookies");


        // loading personal replacement database
        try {
            database = new JSONObject(loadJSONFromAsset(this));
        } catch (JSONException e) {
            Log.d("DEBUG load database", "could not load database");
            e.printStackTrace();
        }

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selected = findViewById(rgOptions.getCheckedRadioButtonId());
                StringBuilder modified = new StringBuilder(ingredient.getString("display_modified"));
                modified.append(" (").append(selected.getText()).append(")");
                ingredient.put("display_modified", modified.toString());
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
        // TODO: check database for ingredient replacements
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder recipeUrl = new StringBuilder(BASE_URL);
        recipeUrl.append(getString(R.string.spoonacular_key));
        recipeUrl.append("&ingredientName=").append(ingredient.getOriginal());
        client.get(recipeUrl.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                getReplacementsfromJSON(json, ingredient);
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
            }
        });
    }

    // parses JSON response from spoonacular api and creates buttons for every replacement option
    private void getReplacementsfromJSON(JsonHttpResponseHandler.JSON json, Ingredient ingredient) {
        JSONObject jsonObject = json.jsonObject;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("substitutes");
            addRadioButtons(jsonArray);
        } catch (JSONException e) {
            checkOwnDatabase(ingredient);
            e.printStackTrace();
        }
    }

    private void checkOwnDatabase(Ingredient ingredient) {
        String ingredientName = ingredient.getName();
        try {
            addRadioButtons(database.getJSONObject(ingredientName).getJSONArray("replacements"));
        } catch (JSONException e) {
            Log.d("DEBUG load database", "could not load replacements from database");
            tvNone.setVisibility(View.VISIBLE);
            btnSelect.setVisibility(View.INVISIBLE);
            tvReplacementTitle.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }
    }

    // adds radio buttons to view for every substitute in JSONArray input
    public void addRadioButtons(JSONArray substitutes) {
        rgOptions.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < substitutes.length(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(View.generateViewId());
            rdbtn.setButtonTintList(
                    ContextCompat.getColorStateList(this,
                            R.color.salmon));
            try {
                rdbtn.setText(substitutes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rgOptions.addView(rdbtn);
        }
    }


    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("database.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}