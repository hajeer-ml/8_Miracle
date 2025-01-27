package com.example.a8_miracle;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.nafis.bottomnavigation.NafisBottomNavigation;

public class Navbar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        NafisBottomNavigation bottomNavigation = findViewById(R.id.navbar);
        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.hom));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.favorite));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.cart));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.sett));
        bottomNavigation.show(1, true);
    }
}