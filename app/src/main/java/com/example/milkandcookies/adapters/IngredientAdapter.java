package com.example.milkandcookies.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.view.LayoutInflater;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.activities.ReplaceActivity;
import com.parse.ParseObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    JSONArray ingredients;

    public IngredientAdapter(JSONArray ingredients) {
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
        try {
            item = (Ingredient) ingredients.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Bind the item into the specified viewHolder
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return ingredients.length();
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
            tvIngredient.setText(item.getOriginal());
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), ReplaceActivity.class);
            try {
                intent.putExtra("ingredient", (Serializable) ingredients.get(getAdapterPosition()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            itemView.getContext().startActivity(intent);
        }
    }
}
