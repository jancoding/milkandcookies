package com.example.milkandcookies;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import junit.framework.TestCase;

import java.util.List;

public class IngredientTest extends TestCase {

    // tests that we got the ingredient unit successfully
    public void testGetUSUnit() {
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.findInBackground(new FindCallback<Ingredient>() {
            public void done(List<Ingredient> itemList, ParseException e) {
                if (e == null) {
                    assertEquals(itemList.get(0).getUSUnit().getClass(), String.class);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}