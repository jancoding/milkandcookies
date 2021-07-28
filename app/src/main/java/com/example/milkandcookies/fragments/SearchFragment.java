package com.example.milkandcookies.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.DatabaseTable;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.RecipeSearch;
import com.example.milkandcookies.adapters.RecipeAdapter;
import com.example.milkandcookies.adapters.RecipeSearchAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private DatabaseTable db;
    private final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=";
    private final String TAG = "SearchFragment";
    private ArrayList<RecipeSearch> recipesToDisplay;
    protected RecyclerView rvSearchRecipes;
    private RecipeSearchAdapter rAdapter;
    private Button btnAdvanced;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseTable(getContext());
        recipesToDisplay = new ArrayList<>();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        rvSearchRecipes = view.findViewById(R.id.rvSearchRecipe);
        btnAdvanced = view.findViewById(R.id.btnAdvanced);
        rAdapter = new RecipeSearchAdapter(getContext(), recipesToDisplay);
        rvSearchRecipes.setAdapter(rAdapter);
        rvSearchRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferences();
            }
        });
    }

    private void showPreferences() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PreferencesFragment preferencesFragment = PreferencesFragment.newInstance();
        preferencesFragment.show(fm, "fragment_edit_tweet");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
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
                JSONObject response = json.jsonObject;
                addToDatabase(response, query);
                try {
                    recipesToDisplay.addAll(createRecipeSearchFromJSON(response));
                    rAdapter.notifyDataSetChanged();
                    rvSearchRecipes.scheduleLayoutAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed to fetch recipes");
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
            recipesToDisplay.addAll(createRecipeSearchFromCursor(c));
            rAdapter.notifyDataSetChanged();
            rvSearchRecipes.scheduleLayoutAnimation();
        }
    }

    // creates RecipeSearch objects from Cursor results
    private ArrayList<RecipeSearch> createRecipeSearchFromCursor(Cursor c) {
        ArrayList<RecipeSearch> toReturn = new ArrayList<RecipeSearch>();
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("TITLE"));
            String sourceUrl = c.getString(c.getColumnIndex("SOURCEURL"));
            String imageUrl = c.getString(c.getColumnIndex("IMAGEURL"));
            RecipeSearch recipeSearch = new RecipeSearch(imageUrl, sourceUrl, title);
            toReturn.add(recipeSearch);
        }
        return toReturn;
    }

    // creates RecipeSearch objects from JSON results
    private ArrayList<RecipeSearch> createRecipeSearchFromJSON(JSONObject jsonObject) throws JSONException {
        ArrayList<RecipeSearch> toReturn = new ArrayList<RecipeSearch>();
        JSONArray results = jsonObject.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            String title = results.getJSONObject(i).getString("title");
            String sourceUrl = results.getJSONObject(i).getString("sourceUrl");
            String imageUrl = results.getJSONObject(i).getString("image");
            RecipeSearch recipeSearch = new RecipeSearch(imageUrl, sourceUrl, title);
            toReturn.add(recipeSearch);
        }
        return toReturn;
    }



}