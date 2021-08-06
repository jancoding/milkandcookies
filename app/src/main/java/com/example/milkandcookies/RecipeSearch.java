package com.example.milkandcookies;

public class RecipeSearch {

    private String imageUrl;
    private String sourceUrl;
    private String title;
    public Boolean vegetarian;
    public Boolean vegan;
    public Boolean gluten_free;
    public Boolean dairy_free;



    public RecipeSearch(String imageUrl, String sourceUrl, String title, Boolean vegan, Boolean vegetarian, Boolean gluten_free, Boolean dairy_free) {
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
        this.gluten_free = gluten_free;
        this.dairy_free = dairy_free;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
