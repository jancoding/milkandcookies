package com.example.milkandcookies;

public class RecipeSearch {

    private String imageUrl;
    private String sourceUrl;
    private  String title;

    public RecipeSearch(String imageUrl, String sourceUrl, String title) {
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
        this.title = title;
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
