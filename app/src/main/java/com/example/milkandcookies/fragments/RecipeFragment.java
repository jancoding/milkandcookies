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

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.adapters.IngredientAdapter;
import com.example.milkandcookies.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    private ArrayList<Ingredient> ingredients;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public RecipeFragment(int page, ArrayList<Ingredient> ingredients) {
        this.page = page;
        this.ingredients = ingredients;
    }

    // returns a new instance of the follower class
    public static RecipeFragment newInstance(int page, ArrayList<Ingredient> ingredients) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RecipeFragment fragment = new RecipeFragment(page, ingredients);
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
        rvIngredients.setAdapter(ingredientAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredients = (ArrayList<Ingredient>) getArguments().get("ingredients");

        setUpPage(view);
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