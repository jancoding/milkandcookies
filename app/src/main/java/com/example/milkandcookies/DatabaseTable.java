package com.example.milkandcookies;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseTable {

    private static final String TAG = "RecipeDatabase";
    public final DatabaseOpenHelper databaseOpenHelper;
    public static final String COL_TITLE = "TITLE";
    public static final String COL_IMAGEURL = "IMAGEURL";
    public static final String COL_SOURCEURL = "SOURCEURL";
    public static final String COL_GLUTEN = "GLUTEN_FREE";
    public static final String COL_DAIRY = "DAIRY_FREE";
    public static final String COL_VEGETARIAN = "VEGETARIAN";
    public static final String COL_VEGAN = "VEGAN";




    private static final String DATABASE_NAME = "RECIPE";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    public DatabaseTable(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public void loadMoreRecipes(JSONObject jsonObject) {
        databaseOpenHelper.loadDatabase(jsonObject);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        COL_TITLE + ", " +
                        COL_SOURCEURL + ", " +
                        COL_VEGAN + ", " +
                        COL_VEGETARIAN + ", " +
                        COL_GLUTEN + ", " +
                        COL_DAIRY + ", " +
                        COL_IMAGEURL + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
            mDatabase = getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        // starts a new thread to input items into a database
        public void loadDatabase(JSONObject jsonObject) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadRecipes(jsonObject);
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        // parses json from spoonacular to get information to add to database
        private void loadRecipes(JSONObject jsonObject) throws IOException, JSONException {
            JSONArray recipesArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject recipe = recipesArray.getJSONObject(i);
                addRecipe(recipe.getString("title"), recipe.getString("image"), recipe.getString("sourceUrl"), recipe.getString("vegetarian"), recipe.getString("vegan"), recipe.getString("glutenFree"), recipe.getString("dairyFree"));
            }
        }

        // adds a new recipe row to the database
        public long addRecipe(String title, String imageURL, String sourceURL, String vegetarian, String vegan, String glutenFree, String dairyFree) {
            ContentValues initialValues = new ContentValues();
            Log.d("vegan is ", vegan);
            initialValues.put(COL_TITLE, title);
            initialValues.put(COL_IMAGEURL, imageURL);
            initialValues.put(COL_SOURCEURL, sourceURL);
            initialValues.put(COL_VEGETARIAN, vegetarian);
            initialValues.put(COL_VEGAN, vegan);
            initialValues.put(COL_GLUTEN, glutenFree);
            initialValues.put(COL_DAIRY, dairyFree);
            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }
    }

    // gets matches of the query from SQL database
    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_TITLE + " LIKE ?";
        String[] selectionArgs = new String[] {"%" + query + "%"};
        return query(selection, selectionArgs, columns);
    }

    public boolean checkWordMatches(String query) {
        String[] selectionArgs = new String[] {"%" + query + "%"};
        // TODO: need to use StringBuilder for this
        String selection = "SELECT EXISTS (SELECT * FROM " + FTS_VIRTUAL_TABLE + " WHERE " + COL_TITLE + " LIKE ? LIMIT 1)";
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        Cursor cursor = builder.query(databaseOpenHelper.getReadableDatabase(),
                null, selection, selectionArgs, null, null, null);
        Log.d("DEBUG", "found this many matches " + cursor.getCount());
        return cursor.getCount() == 0;
    }

    // constructs and executes sql query and returns cursor with matching rows
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        Cursor cursor = builder.query(databaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

}
