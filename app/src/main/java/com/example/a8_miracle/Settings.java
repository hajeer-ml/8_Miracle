package com.example.a8_miracle;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class Settings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_settings, container, false);


        LinearLayout logoutLayout = myView.findViewById(R.id.logout);


        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // تسجيل الخروج
                logoutUser();
            }
        });

        return myView;
    }

    private void logoutUser() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userID");
        editor.remove("isLoggedIn");
        editor.apply();


        Intent intent = new Intent(requireActivity(), Login.class);
        startActivity(intent);


        requireActivity().finish();
    }
}