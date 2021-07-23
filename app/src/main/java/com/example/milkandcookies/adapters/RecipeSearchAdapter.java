package com.example.milkandcookies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.RecipeSearch;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.ViewHolder> {

    private ArrayList<RecipeSearch> recipesToDisplay;
    private Context context;

    public RecipeSearchAdapter(Context context, ArrayList<RecipeSearch> recipes) {
        this.context = context;
        this.recipesToDisplay = recipes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipesearch, parent, false);
        // wrap it inside a View Holder and return it
        return new RecipeSearchAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(recipesToDisplay.get(position));
    }

    @Override
    public int getItemCount() {
        return recipesToDisplay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRecipeTitle;
        private ImageView ivRecipeImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
        }

        public void bind(RecipeSearch item) {
            tvRecipeTitle.setText(item.getTitle());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .into(ivRecipeImage);
        }
    }

}
