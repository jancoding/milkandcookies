package com.example.milkandcookies.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int page;
    private RecyclerView rvIngredients;
    private IngredientAdapter ingredientAdapter;
    private TextView tvInstructions;
    private List<Ingredient> ingredients;
    private ParseObject recipe;

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
        ingredientAdapter = new IngredientAdapter(ingredients);
        tvInstructions = view.findViewById(R.id.tvInstructions);
        rvIngredients.setAdapter(ingredientAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvInstructions.setText(decomposeInstructions(((Recipe) recipe).getInstructions()));


        setUpPage(view);
    }

    private String decomposeInstructions(JSONArray jsonArray) {
        String text = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                text = text + jsonArray.getString(i) + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

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

    }
}