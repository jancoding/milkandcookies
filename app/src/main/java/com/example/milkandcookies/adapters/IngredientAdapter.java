package com.example.milkandcookies.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.view.LayoutInflater;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.example.milkandcookies.activities.ReplaceActivity;
import com.facebook.stetho.json.ObjectMapper;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.List;

// adapter for recycler view displaying ingredients in detail activity
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    List<Ingredient> ingredients;
    boolean original;

    public IngredientAdapter(List<Ingredient> ingredients, boolean original) {
        this.ingredients = ingredients;
        this.original = original;
    }

    @NonNull
    @NotNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Use layout inflator to inflate a view
        View ingredientView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        // wrap it inside a View Holder and return it
        return new ViewHolder(ingredientView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IngredientAdapter.ViewHolder holder, int position) {
        // Grab the item at the position
        Ingredient item = null;
        item = (Ingredient) ingredients.get(position);
        // Bind the item into the specified viewHolder
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        // Update the view inside of the holder with this data
        public void bind(Ingredient item) {
            String text = original ? item.getString("display_original") : item.getString("display_modified");
            tvIngredient.setText(text);
            checkAllergen();
        }

        // goes to replace activity to find alternatives for ingredient selected
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), ReplaceActivity.class);
            intent.putExtra("ingredient", (Serializable) ingredients.get(getAdapterPosition()));
            ((Activity) itemView.getContext()).startActivityForResult(intent, 200);
        }

        // checks allergens for user and highlights in red
        private void checkAllergen() {
//            Log.d("allergen", "checking allergens");
            JSONArray allergens = ParseUser.getCurrentUser().getJSONArray("allergens");
            for (int j = 0; j < allergens.length(); j++) {
                try {
//                    Log.d("allergen in", ingredients.get(getAdapterPosition()).getName());
//                    Log.d("allergen actual", allergens.get(j).toString());
                    if (ingredients.get(getAdapterPosition()).getName().equals(allergens.get(j).toString())){
                        tvIngredient.setTextColor(Color.rgb(255,0,0));
                        return;
                    } else {
                        tvIngredient.setTextColor(Color.rgb(255,255,255));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
