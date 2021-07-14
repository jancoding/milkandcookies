package com.example.milkandcookies.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.milkandcookies.R;
import com.example.milkandcookies.fragments.ComposeFragment;
import com.example.milkandcookies.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.rlFeed, fragment).commit();
                        break;
                    case R.id.new_recipe:
                        fragment = new ComposeFragment();
                        fragmentManager.beginTransaction().replace(R.id.rlFeed, fragment).commit();
                        break;
                    case R.id.profile:

                    default:
                        break;
                }
                return true;
            }
        });


    }


}