package com.example.milkandcookies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.milkandcookies.R;
import com.example.milkandcookies.fragments.ComposeFragment;
import com.example.milkandcookies.fragments.HomeFragment;
import com.example.milkandcookies.fragments.IdentifyFragment;
import com.example.milkandcookies.fragments.ProfileFragment;
import com.example.milkandcookies.fragments.SearchFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

// Activity with the three tabs to view the feed, create a new recipe, and view profile
public class FeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // on first load, loads the home fragment (feed of recipes)
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flFragment, new HomeFragment()).commit();

        // loads the correct fragment based on bottom navigation view selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit();
                        break;
                    case R.id.new_recipe:
                        fragment = new ComposeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit();
                        break;
                    case R.id.search:
                        fragment = new SearchFragment();
                        fragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit();
                        break;
                    case R.id.profile:
                        fragment = new ProfileFragment();
                        fragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit();
                        break;
                    case R.id.scan:
                        fragment = new IdentifyFragment();
                        fragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    // inflates the special tool bar for this view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // controls the logout button click on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // logs a user out of its account and switches to LoginActivity
    private void logout() {
        ParseUser.logOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}