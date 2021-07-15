package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.milkandcookies.Ingredient;
import com.example.milkandcookies.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignup;
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goFeedActivity();
        }

        // link view
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

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
        BonAPITest();
    }

    private void BonAPITest() {
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, "{\"ingredients\": ['250gr white wheat flour', '50ml cow milk', '1 chicken breast', '0.5 cups of white rice']}");
        Request request = new Request.Builder()
                .header("Authorization", "Authorization: Token 50b313b1e22fa05eb8512f6f78845d8f5ec8f4b7")
                .header("Content-type", "application/json")
                .url("https://www.bon-api.com/api/v1/ingredient/alternatives")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    Log.d("bonapi", response.toString());
                }
            }
        });

    }

    private void goSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void goFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

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