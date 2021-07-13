package com.example.milkandcookies;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;

@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Serializable {

    public static final String KEY_ORIGINAL = "ingredients_original";
    public static final String KEY_MODIFIED = "ingredients_modified";
    public static final String KEY_USER = "owner";
    public static final String KEY_INSTRUCTIONS = "instructions";

    public JSONArray getInstructions() {
        return getJSONArray(KEY_INSTRUCTIONS);
    }

    public void setInstructions(JSONArray instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }

    public JSONArray getOriginal() {
        return getJSONArray(KEY_ORIGINAL);
    }

    public void setOriginal(JSONArray original) {
        put(KEY_ORIGINAL, original);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public JSONArray getModified() {
        return getJSONArray(KEY_MODIFIED);
    }

    public void setModified(JSONArray modified) {
        put(KEY_MODIFIED, modified);
    }

}




































































