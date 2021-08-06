package com.example.milkandcookies.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.milkandcookies.R;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreferencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferencesFragment extends DialogFragment {


    private RadioGroup rgDietary;
    private Button btnConfirm;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    public static PreferencesFragment newInstance() {
        PreferencesFragment fragment = new PreferencesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rgDietary = view.findViewById(R.id.rgDietary);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selection = view.findViewById(rgDietary.getCheckedRadioButtonId());
                goSearchActivity(selection.getText().toString());
            }
        });
    }

    private void goSearchActivity(String selected) {
        SearchFragment.selection = selected;
        dismiss();
    }
}