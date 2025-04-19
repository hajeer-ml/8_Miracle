package com.example.a8_miracle;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Settings extends Fragment {

    private TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUserName = myView.findViewById(R.id.tvUserName);


        fetchUserData();

        LinearLayout logoutLayout = myView.findViewById(R.id.logout);
        LinearLayout about =myView.findViewById(R.id.help);
        LinearLayout order =myView.findViewById(R.id.order);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();


                transaction.replace(R.id.container, new order_list());


                transaction.addToBackStack(null);


                transaction.commit();

            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AboutUs.class);
                startActivity(intent);

            }
        });




        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutUser();
            }
        });

        return myView;
    }
    private void updateUserName(String userName) {
        if (tvUserName != null) {
            tvUserName.setText(userName);
        }}

    private void fetchUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        if (!userID.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://8miracle.serv00.net/account/get_user_data.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");

                                if (status.equals("success")) {
                                    String userName = jsonObject.getString("name");
                                    updateUserName(userName);
                                } else {
                                    Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireActivity(), "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireActivity(), "Server connection error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userID);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(stringRequest);
        }
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