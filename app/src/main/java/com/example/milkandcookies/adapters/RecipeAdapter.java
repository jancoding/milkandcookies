package com.example.milkandcookies.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.activities.DetailActivity;
import com.example.milkandcookies.activities.ReplaceActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipe = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteRecipe();
                    return true;
                }
            });
        }

        private void deleteRecipe() {
            ParseQuery<Recipe> query = ParseQuery.getQuery("Recipe");
            Recipe toRemove =  recipes.get(getAdapterPosition());
            removeIngredients(toRemove);
            recipes.get(getAdapterPosition()).deleteInBackground();
            recipes.remove(recipes.get(getAdapterPosition()));
            notifyDataSetChanged();
        }

        // requests the Parse database storing ingredients for the recipe
        private void removeIngredients(Recipe recipe) {
            JSONArray jsonArray = new JSONArray();
            ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
            query.whereEqualTo("recipe", recipe);
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Ingredient>() {
                public void done(List<Ingredient> itemList, ParseException e) {
                    if (e == null) {
                        // Access the array of results here
                        for (Ingredient item: itemList) {
                            item.deleteInBackground();
                        }
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
        }

        // Update the view inside of the holder with this data
        public void bind(Recipe item) {
            tvRecipe.setText(item.getTitle());
        }

        // transitions to detail view and passes recipe
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("recipe", (Serializable) recipes.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
