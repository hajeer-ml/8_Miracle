package com.example.a8_miracle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class spltwo extends AppCompatActivity {
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spltwo);
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                            Intent intent= new Intent(spltwo.this , Login.class);
                            startActivities(new Intent[]{intent});
                            finish();
            }
        });
    }
}