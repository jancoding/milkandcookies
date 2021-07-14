package com.example.milkandcookies.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.fragments.RecipeFragment;
import com.parse.ParseObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DetailFragmentAdapter extends FragmentPagerAdapter {

    // number of tabs in this PageAdapter
    int PAGE_COUNT;
    // titles of each tab
    private String tabTitles[];
    private Context context;
    private List<Ingredient> ingredients;
    private ParseObject recipe;

    public DetailFragmentAdapter(FragmentManager fm, Context context, List<Ingredient> ingredients, ParseObject recipe) {
        super(fm);
        this.context = context;
        PAGE_COUNT = 2;
        tabTitles = new String[] { "Original", "Modified" };
        this.ingredients = ingredients;
        this.recipe = recipe;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return RecipeFragment.newInstance(position + 1, ingredients, recipe);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    // retrieves the titles of each page
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
