package com.example.milkandcookies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.activities.ReplaceActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// adapter to display recipes in feed of recipes view
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    Context context;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        // wrap it inside a View Holder and return it
        return new ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipe = itemView.findViewById(android.R.id.text1);
        }

        // Update the view inside of the holder with this data
        public void bind(Recipe item) {
            tvRecipe.setText(item.getTitle());
        }

    }
}
