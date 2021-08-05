package com.example.milkandcookies.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.adapters.RecipeAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private TextView tvNumRecipes;
    private TextView tvAllergens;
    private TextView tvDietary;
    private TextView tvFullName;
    private RecyclerView rvFavorites;
    private ArrayList<Recipe> favoriteRecipes = new ArrayList<>();
    private RecipeAdapter recipeAdapter;
    private static final String TAG = "ProfileFragment";


    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // connect to a view
        tvNumRecipes = view.findViewById(R.id.tvNumRecipes);
        tvAllergens = view.findViewById(R.id.tvAllergens);
        tvDietary = view.findViewById(R.id.tvDietary);
        tvFullName = view.findViewById(R.id.tvFullName);

        // set up recycler view
        rvFavorites = view.findViewById(R.id.rvFavorites);
        getFavoriteRecipes();
        recipeAdapter = new RecipeAdapter(getActivity(), favoriteRecipes);
        rvFavorites.setAdapter(recipeAdapter);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // retrieve profile information and load into view of user
        getNumRecipes();
        try {
            tvFullName.setText(ParseUser.getCurrentUser().getString("full_name"));
            tvDietary.setText(ParseUser.getCurrentUser().getString("dietary"));
            getAllergens();
        } catch (JSONException e) {
            Log.d(TAG, "could not successfully get allergens");
        }

    }

    // retrieves and formats user's allergens through a string builder
    private void getAllergens() throws JSONException {
        JSONArray allergens = ParseUser.getCurrentUser().getJSONArray("allergens");
        StringBuilder allergenBuilder = new StringBuilder("");
        for (int i = 0; i < allergens.length() - 1; i++) {
            allergenBuilder.append(allergens.getString(i)).append(", ");
        }
        allergenBuilder.append(allergens.getString(allergens.length() - 1));
        tvAllergens.setText(allergenBuilder.toString());
    }

    // retrieves the users favorited recipes
    private void getFavoriteRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_USER);
        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo("favorite", true);
        query.findInBackground(new FindCallback<Recipe>() {
            public void done(List<Recipe> itemList, ParseException e) {
                if (e == null) {
                    favoriteRecipes.addAll(itemList);
                    recipeAdapter.notifyDataSetChanged();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    // retrieves the number of recipes the user has created
    private void getNumRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_USER);
        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Recipe>() {
            public void done(List<Recipe> itemList, ParseException e) {
                if (e == null) {
                    tvNumRecipes.setText(itemList.size() + "");
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}