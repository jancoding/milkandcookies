package com.example.milkandcookies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.OnSwipeTouchListener;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.activities.DetailActivity;
import com.example.milkandcookies.activities.ReplaceActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
        View recipeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
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
        ImageView ivHeart;
        Button btnSend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipe = itemView.findViewById(R.id.tvTitle);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            btnSend = itemView.findViewById(R.id.btnSend);
            ivHeart.setColorFilter(ContextCompat.getColor(context, R.color.medium_red), android.graphics.PorterDuff.Mode.SRC_IN);

            itemView.setOnTouchListener(new OnSwipeTouchListener(context) {
                public void onSwipeLeft() {
                    deleteRecipe();
                }
            });


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteRecipe();
                    return true;
                }
            });

            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFavorited = recipes.get(getAdapterPosition()).getBoolean("favorite");
                    int imageID = isFavorited ? R.drawable.ufi_heart : R.drawable.ufi_heart_active;
                    ivHeart.setImageResource(imageID);
                    favoriteInDatabase(recipes.get(getAdapterPosition()), isFavorited);
                }
            });

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handle sending an email to user
                    Recipe recipe = recipes.get(getAdapterPosition());
                    getIngredients(recipe);
                }
            });
        }

        private String getIngredients(Recipe recipe) {
            ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
            query.whereEqualTo("recipe", recipe);
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Ingredient>() {
                public void done(List<Ingredient> itemList, ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, ParseUser.getCurrentUser().getEmail().toString());
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Your recipe from milk and cookies: " + recipe.getTitle().toString());
                        Log.d("DEBUG", getFormattedIngredients(itemList)+ " \n\n\n" + getFormattedInstructions(recipe.getInstructions()));
                        intent.putExtra(Intent.EXTRA_TEXT, getFormattedIngredients(itemList)+ " \n\n\n" + getFormattedInstructions(recipe.getInstructions()));
                        context.startActivity(intent);
                    } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        private String getFormattedIngredients(List<Ingredient> itemList) {
            StringBuilder formattedIngredients = new StringBuilder("");
            for (Ingredient ingredient: itemList) {
                formattedIngredients.append(ingredient.get("display_modified")).append("\n");
            }
            return formattedIngredients.toString();
        }

        // TODO: change to StringBuilder
        private String getFormattedInstructions(JSONArray instructions) {
            String text = "";
            for (int i = 0; i < instructions.length(); i++) {
                try {
                    text = text + (i+1) + ". " + instructions.getString(i) + "\n\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return text;
        }


        private void favoriteInDatabase(Recipe recipe, boolean isFavorited) {
            recipe.put("favorite", !isFavorited);
            recipe.saveInBackground();
        }

        private void deleteRecipe() {
            Recipe toRemove =  recipes.get(getAdapterPosition());
            removeIngredients(toRemove);
            recipes.get(getAdapterPosition()).deleteInBackground();
            recipes.remove(recipes.get(getAdapterPosition()));
            notifyDataSetChanged();
        }

        // requests the Parse database storing ingredients for the recipe
        private void removeIngredients(Recipe recipe) {
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
            setHeart();
        }

        private void setHeart() {
            boolean isFavorited = recipes.get(getAdapterPosition()).getBoolean("favorite");
            int imageID = isFavorited ? R.drawable.ufi_heart_active : R.drawable.ufi_heart;
            ivHeart.setImageResource(imageID);
        }

        // transitions to detail view, passes recipe, and performs transition
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("recipe", (Serializable) recipes.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
