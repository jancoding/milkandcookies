package com.example.milkandcookies.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.milkandcookies.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private RadioGroup rgDietary;
    private Button btnSignup;
    private Button btnAdd;
    private List<EditText> allergens = new ArrayList<>();
    private final String TAG = "SignupActivity";
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rgDietary = findViewById(R.id.rgDietary);
        btnSignup = findViewById(R.id.btnSignup);
        btnAdd = findViewById(R.id.btnAdd);
        layout = (LinearLayout) findViewById(R.id.llAllergens);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText();
            }
        });
    }

    private void signupUser() {
        ParseUser user = new ParseUser();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.put("full_name", etFullName.getText().toString());
        RadioButton selected = findViewById(rgDietary.getCheckedRadioButtonId());
        user.put("dietary", selected.getText().toString());
        user.put("allergens", getAllergens());
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goFeedActivity();
                } else {
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
            }
        });
    }

    private JSONArray getAllergens() {
        JSONArray jsonArray = new JSONArray();
        for (EditText allergen: allergens) {
            jsonArray.put(allergen.getText().toString());
        }
        return jsonArray;
    }

    private void goFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    public void addEditText() {
        EditText etAllergen = new EditText(this);
        etAllergen.setId(View.generateViewId());
        allergens.add(etAllergen);
        layout.addView(etAllergen);
    }
}



