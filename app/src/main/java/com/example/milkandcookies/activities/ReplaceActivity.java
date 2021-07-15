package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.net.Proxy.Type.HTTP;

public class ReplaceActivity extends AppCompatActivity {

    private RadioGroup rgOptions;
    private final String BASE_URL = "https://api.spoonacular.com/food/ingredients/substitutes?apiKey=79e84e817f6144358ae1a9057f0bb87a";
    private Ingredient ingredient;
    private final String TAG = "ReplaceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace);
        rgOptions = findViewById(R.id.rgOptions);
        ingredient = (Ingredient) getIntent().getSerializableExtra("ingredient");
        getReplacements(ingredient);

    }



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

    private void getReplacementsfromJSON(JsonHttpResponseHandler.JSON json) {
        JSONObject jsonObject = json.jsonObject;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("substitutes");
            addRadioButtons(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



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
}