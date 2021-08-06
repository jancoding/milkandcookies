package com.example.milkandcookies.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.adapters.IngredientAdapter;
import com.example.milkandcookies.R;
import com.parse.Parse;
import com.parse.ParseObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

// fragment for recipe view in detail activity TODO: configure for original vs modified recipe
public class RecipeFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int page;
    private RecyclerView rvIngredients;
    private IngredientAdapter ingredientAdapter;
    private TextView tvInstructions;
    private TextView tvTitle;
    private List<Ingredient> ingredients;
    private ParseObject recipe;
    private Switch switchUnits;
    private ImageView ivDairy;
    private ImageView ivGluten;
    private ImageView ivVegan;
    private ImageView ivVegetarian;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public RecipeFragment(int page, List<Ingredient> ingredients, ParseObject recipe) {
        this.page = page;
        this.ingredients = ingredients;
        this.recipe = recipe;
    }

    // returns a new instance of the follower class
    public static RecipeFragment newInstance(int page, List<Ingredient> ingredients, ParseObject recipe) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RecipeFragment fragment = new RecipeFragment(page, ingredients, recipe);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        switchUnits = view.findViewById(R.id.switchUnits);
        ingredientAdapter = new IngredientAdapter(ingredients, this.page == 1);
        tvInstructions = view.findViewById(R.id.tvInstructions);
        ivDairy = view.findViewById(R.id.ivDairy);
        ivGluten = view.findViewById(R.id.ivGluten);
        ivVegan = view.findViewById(R.id.ivVegan);
        ivVegetarian = view.findViewById(R.id.ivVegetarian);

        tvTitle = view.findViewById(R.id.tvTitle);
        rvIngredients.setAdapter(ingredientAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvInstructions.setText(decomposeInstructions(((Recipe) recipe).getInstructions()));
        tvTitle.setText(((Recipe) recipe).getTitle());
        tvInstructions.setMovementMethod(new ScrollingMovementMethod());

        setTags();

        // listener for switch to transition between metric and us measurements
        switchUnits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getMetric(ingredients);
                    ingredientAdapter.notifyDataSetChanged();
                } else {
                    getUS(ingredients);
                    ingredientAdapter.notifyDataSetChanged();
                }
            }
        });

        // determines whether the view should be original or modified
        setUpPage(view);
    }

    private void setTags() {
        if (recipe.getBoolean("vegan")) {
            ivVegan.setVisibility(View.VISIBLE);
        }
        if (recipe.getBoolean("vegetarian")) {
            ivVegetarian.setVisibility(View.VISIBLE);
        }
        if (recipe.getBoolean("gluten_free")) {
            ivGluten.setVisibility(View.VISIBLE);
        }
        if (recipe.getBoolean("dairy_free")) {
            ivDairy.setVisibility(View.VISIBLE);
        }
    }


    // retrieves metric amounts from the database
    private void getMetric(List<Ingredient> ingredients) {
        for (Ingredient ingredient: ingredients) {
            ingredient.put("display_original", ingredient.getMetricAmount() + " " + ingredient.getMetricUnit() + " " + ingredient.getName());
            ingredient.saveInBackground();
        }
    }

    // retrieves us amounts from the database
    private void getUS(List<Ingredient> ingredients) {
        for (Ingredient ingredient: ingredients) {
            ingredient.put("display_original", ingredient.getOriginal());
            ingredient.saveInBackground();
        }
    }

    // formats instructions for the recipe
    private String decomposeInstructions(JSONArray jsonArray) {
        String text = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                text = text + (i+1) + ". " + jsonArray.getString(i) + "\n\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    // BELOW: code to handle original vs modified view for recipe
    private void setUpPage(View view) {
        if (this.page == 1) {
            createOriginal(view);
        } else {
            createModified(view);
        }
    }

    private void createOriginal(View view) {

    }

    private void createModified(View view) {
        ingredientAdapter.notifyDataSetChanged();
        switchUnits.setVisibility(View.GONE);
    }
}