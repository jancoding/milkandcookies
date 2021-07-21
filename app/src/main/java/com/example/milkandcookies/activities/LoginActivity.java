package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

// Activity for logging in a user or launching sign up
public class LoginActivity extends AppCompatActivity {

    private Button btnSignup;
    private Button btnLogin;
    private Button btnSearch;
    private Button btnScan;
    private EditText etUsername;
    private EditText etPassword;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // login persistence between app uses
        if (ParseUser.getCurrentUser() != null) {
            goFeedActivity();
        }

        // link view
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnSearch = findViewById(R.id.btnSearch);
        btnScan = findViewById(R.id.btnScan);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        // listener for scan
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goScanActivity();
            }
        });

        // listener for signup
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignUp();
            }
        });

        // listener for login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch();
            }
        });
    }

    private void goScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void goSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    // goes to sign up activity
    private void goSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    // goes to feed activity and removes view from view hierarchy
    private void goFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    // logs the user with specific username and password into parse
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    goFeedActivity();
                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Login failed " + e);
                }
            }
        });
    }

}