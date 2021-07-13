package com.example.milkandcookies;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String fullIngredient;
    private String simpleIngredient;

    public Ingredient(String fullIngredient, String simpleIngredient) {
        this.fullIngredient = fullIngredient;
        this.simpleIngredient = simpleIngredient;
    }

    public String getFullIngredient() {
        return fullIngredient;
    }

    public void setFullIngredient(String fullIngredient) {
        this.fullIngredient = fullIngredient;
    }

    public String getSimpleIngredient() {
        return simpleIngredient;
    }

    public void setSimpleIngredient(String simpleIngredient) {
        this.simpleIngredient = simpleIngredient;
    }
}
