package com.example.milkandcookies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.DatabaseTable;
import com.example.milkandcookies.R;
import com.example.milkandcookies.Recipe;
import com.example.milkandcookies.RecipeSearch;
import com.example.milkandcookies.adapters.RecipeAdapter;
import com.example.milkandcookies.adapters.RecipeSearchAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private DatabaseTable db;
    private final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=";
    private final String TAG = "SearchFragment";
    private ArrayList<RecipeSearch> recipesToDisplay;
    protected RecyclerView rvSearchRecipes;
    private RecipeSearchAdapter rAdapter;
    private Button btnSearchByImage;
    private SearchView searchView;
    private String photoFileName = "photo.jpg";
    private Button btnAdvanced;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    public static String selection = null;


    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseTable(getContext());
        recipesToDisplay = new ArrayList<>();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        rvSearchRecipes = view.findViewById(R.id.rvSearchRecipe);
        btnAdvanced = view.findViewById(R.id.btnAdvanced);
        btnSearchByImage = view.findViewById(R.id.btnSearchByPicture);
        rAdapter = new RecipeSearchAdapter(getContext(), recipesToDisplay);
        rvSearchRecipes.setAdapter(rAdapter);
        rvSearchRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferences();
            }
        });

        btnSearchByImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked take picture button");
                launchCamera();
            }
        });
    }

    // REPEATED: COPY PASTED CODE FROM IDENTIFY FRAGMENT
    // TODO: Determine how to remove repeated code

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.janvi.fileprovider.milkandcookies", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            Log.i(TAG, "Starting activity for result");
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void showPreferences() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PreferencesFragment preferencesFragment = PreferencesFragment.newInstance();
        preferencesFragment.show(fm, "fragment_edit_tweet");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                getFoodIdentification(takenImage);
            }
        }
    }


    private void getFoodIdentification(Bitmap takenImage) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder urlBuilder = new StringBuilder("https://api.spoonacular.com/food/images/classify/?apiKey=");
        urlBuilder.append(getString(R.string.spoonacular_key));
        String recipeURL = urlBuilder.toString();

        RequestBody body = null;
        try {
            body = RequestBody.create(Files.readAllBytes(photoFile.toPath()), MediaType.get("image/jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "photo.jpg", body)
                .build();

        client.post(recipeURL, null, null, requestBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("DEBUG success", json.toString());
                JSONObject jsonObject = json.jsonObject;
                try {
                    searchView.setQuery(jsonObject.getString("category"), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG fail", response);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search for New Recipes");
        // Expand the search view and request focus
        searchItem.expandActionView();
        searchView.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                checkAddedToDatabase(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // fetches recipes matching a query from spoonacular
    private void fetchRecipes(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append(getString(R.string.spoonacular_key)).append("&query=").append(query).append("&addRecipeInformation=true");
        String recipeURL = urlBuilder.toString();
        client.get(recipeURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject response = json.jsonObject;
                Log.d("query response", response.toString());
                addToDatabase(response, query);
                try {
                    recipesToDisplay.clear();
                    rAdapter.notifyDataSetChanged();
                    Log.d("DEBUG", "Clearing recipes");
                    recipesToDisplay.addAll(createRecipeSearchFromJSON(response));
                    rAdapter.notifyDataSetChanged();
                    rvSearchRecipes.scheduleLayoutAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "failed to fetch recipes");
            }
        });
    }

    // adds new recipes to the database
    private void addToDatabase(JSONObject jsonObject, String query) {
        db.loadMoreRecipes(jsonObject);
    }

    //  check to determine if database contains recipes or we need to retrieve ones
    private void checkAddedToDatabase(String query) {

        if (db.checkWordMatches(query)) {
            Log.d("DEBUG", "Could not find matches in database");
            fetchRecipes(query);
        } else {
            Log.d("DEBUG", "Could  find matches in database");
            Cursor c = db.getWordMatches(query, null);
            recipesToDisplay.clear();
            recipesToDisplay.addAll(createRecipeSearchFromCursor(c));
            rAdapter.notifyDataSetChanged();
            rvSearchRecipes.scheduleLayoutAnimation();
        }
    }

    // creates RecipeSearch objects from Cursor results
    private ArrayList<RecipeSearch> createRecipeSearchFromCursor(Cursor c) {
        ArrayList<RecipeSearch> toReturn = new ArrayList<RecipeSearch>();
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("TITLE"));
            String sourceUrl = c.getString(c.getColumnIndex("SOURCEURL"));
            String imageUrl = c.getString(c.getColumnIndex("IMAGEURL"));
            Boolean vegetarian = Boolean.valueOf(c.getString(c.getColumnIndex("VEGETARIAN")));
            Boolean vegan = Boolean.valueOf(c.getString(c.getColumnIndex("VEGAN")));
            Boolean dairy_free = Boolean.valueOf(c.getString(c.getColumnIndex("DAIRY_FREE")));
            Boolean gluten_free = Boolean.valueOf(c.getString(c.getColumnIndex("GLUTEN_FREE")));
            RecipeSearch recipeSearch = new RecipeSearch(imageUrl, sourceUrl, title, vegan, vegetarian, gluten_free, dairy_free);
            if (canAdd(recipeSearch)) {
                toReturn.add(recipeSearch);
            }

        }
        return toReturn;
    }

    // creates RecipeSearch objects from JSON results
    private ArrayList<RecipeSearch> createRecipeSearchFromJSON(JSONObject jsonObject) throws JSONException {
        ArrayList<RecipeSearch> toReturn = new ArrayList<RecipeSearch>();
        JSONArray results = jsonObject.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            String title = results.getJSONObject(i).getString("title");
            String sourceUrl = results.getJSONObject(i).getString("sourceUrl");
            String imageUrl = results.getJSONObject(i).getString("image");
            Boolean vegetarian = results.getJSONObject(i).getBoolean("vegetarian");
            Boolean vegan = results.getJSONObject(i).getBoolean("vegan");
            Boolean glutenFree = results.getJSONObject(i).getBoolean("glutenFree");
            Boolean dairyFree = results.getJSONObject(i).getBoolean("dairyFree");
            RecipeSearch recipeSearch = new RecipeSearch(imageUrl, sourceUrl, title, vegan, vegetarian, glutenFree, dairyFree);

            if (canAdd(recipeSearch)) {
                toReturn.add(recipeSearch);
            }

        }
        return toReturn;
    }

    private boolean canAdd(RecipeSearch recipeSearch) {
        if (selection == null) {
            return true;
        } else if (selection.equals("Vegan") && recipeSearch.vegan) {
            return true;
        } else if (selection.equals("Vegetarian") && recipeSearch.vegetarian) {
            return true;
        } else if (selection.equals("Dairy Free") && recipeSearch.dairy_free) {
            return true;
        } else if (selection.equals("Gluten Free") && recipeSearch.gluten_free) {
            return true;
        }
        return false;
    }



}