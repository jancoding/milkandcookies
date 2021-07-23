package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.DatabaseTable;
import com.example.milkandcookies.ParseApplication;
import com.example.milkandcookies.R;
import com.parse.Parse;

import org.json.JSONObject;

import okhttp3.Headers;

public class SearchActivity extends AppCompatActivity {

    private final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=";
    private final String TAG = "SearchActivity";
    private DatabaseTable db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbSearch);
        setSupportActionBar(toolbar);
        db = new DatabaseTable(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Expand the search view and request focus
        searchItem.expandActionView();
        searchView.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                checkAddedToDatabase(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // fetches recipes matching a query from spoonacular
    private void fetchRecipes(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append(getString(R.string.spoonacular_key)).append("&query=").append(query).append("&addRecipeInformation=true");
        String recipeURL = urlBuilder.toString();
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                addToDatabase(json.jsonObject, query);
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
            }
        });
    }

    // adds new recipes to the database
    private void addToDatabase(JSONObject jsonObject, String query) {
        db.loadMoreRecipes(jsonObject);
    }

    //  check to determine if database contains recipes or we need to retrieve ones
    private void checkAddedToDatabase(String query) {
        Cursor c = db.getWordMatches(query, null);
        if (c == null) {
            fetchRecipes(query);
        } else {
            Log.d(TAG, c.getCount() + "");
            while (c.moveToNext()) {
                Log.d(TAG, c.getString(c.getColumnIndex("TITLE")));
            }
        }
    }
}