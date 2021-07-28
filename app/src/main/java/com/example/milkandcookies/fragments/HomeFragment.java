package com.example.milkandcookies.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.adapters.RecipeAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// fragment for home/feed view
public class HomeFragment extends Fragment {

    protected RecyclerView rvRecipes;
    private RecipeAdapter rAdapter;
    private List<Recipe> recipes = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        rAdapter = new RecipeAdapter(getContext(), recipes);
        rvRecipes.setAdapter(rAdapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        getRecipes();
    }

    // retrieves recipes from parse linked to the current user
    private void getRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Recipe>() {
            public void done(List<Recipe> itemList, ParseException e) {
                if (e == null) {
                    recipes.clear();
                    recipes.addAll(itemList);
                    rAdapter.notifyDataSetChanged();
                    rvRecipes.scheduleLayoutAnimation();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

}