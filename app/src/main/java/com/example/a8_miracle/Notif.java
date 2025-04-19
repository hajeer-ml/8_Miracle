package com.example.a8_miracle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notif extends AppCompatActivity {
    private TextView notificationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notif);

        notificationTextView = findViewById(R.id.notificationTextView);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        if (message != null && !message.isEmpty()) {
            notificationTextView.setText(message);
        } else {
            notificationTextView.setText("No Notices Right Now!");
        }

    }

}