package com.example.milkandcookies.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AbsCallback;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.milkandcookies.R;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IdentifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdentifyFragment extends Fragment {


    private Button btnPicture;
    private ImageView ivPicture;
    private TextView tvIdentification;
    private TextView tvConfidence;
    private File photoFile;
    private File binaryPhotoFile;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public final static int PICK_PHOTO_CODE = 1046;
    private String photoFileName = "photo.jpg";
    public static final String TAG = "IdentifyFragment";
    private String BASE_URL = "https://api.spoonacular.com/food/images/classify/?apiKey=";

    public IdentifyFragment() {
        // Required empty public constructor

    }


    public static IdentifyFragment newInstance() {
        IdentifyFragment fragment = new IdentifyFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPicture = view.findViewById(R.id.btnPicture);
        ivPicture = view.findViewById(R.id.ivPicture);
        tvIdentification = view.findViewById(R.id.tvIdentification);
        tvConfidence = view.findViewById(R.id.tvConfidence);


        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked take picture button");
                launchCamera();
            }
        });


    }


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identify, container, false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ComposeFragment", "return from activity result " + requestCode);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPicture.setImageBitmap(takenImage);
                getFoodIdentification(takenImage);
            }
        }
    }

    private void getFoodIdentification(Bitmap takenImage) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
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
                    tvIdentification.setText(jsonObject.getString("category"));
                    double probability = jsonObject.getDouble("probability") * 100;
                    tvConfidence.setText(getConfidenceType(probability));
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

    private String getConfidenceType(double probability) {
        if (probability > 80) {
            return "Very Confident";
        } else if (probability > 50) {
            return "Kind of Confident";
        } else {
            return "Not Confident";
        }
    }
}