package com.example.milkandcookies.adapters;

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

    public IngredientAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
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
            tvIngredient.setText(item.getModified());
            Log.d("here", "hereeeee");
            checkAllergen();
        }

        // goes to replace activity to find alternatives for ingredient selected
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), ReplaceActivity.class);
            intent.putExtra("ingredient", (Serializable) ingredients.get(getAdapterPosition()));
            itemView.getContext().startActivity(intent);
        }

        private void checkAllergen() {
            JSONArray allergens = ParseUser.getCurrentUser().getJSONArray("allergens");
            Log.d("allergens", allergens.toString());
            for (int j = 0; j < allergens.length(); j++) {
                try {
                    Log.d("allergens", (ingredients.get(getAdapterPosition()).getName()));
                    Log.d("allergens", allergens.get(j).toString());
                    if (ingredients.get(getAdapterPosition()).getName().equals(allergens.get(j).toString())){
                        tvIngredient.setTextColor(Color.rgb(255,0,0));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
