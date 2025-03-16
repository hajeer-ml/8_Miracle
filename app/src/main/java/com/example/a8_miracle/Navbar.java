package com.example.a8_miracle;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Navbar extends AppCompatActivity {

    NafisBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navbar);

        bottomNavigation = findViewById(R.id.navbar);



        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.hom));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.favorite));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.cart));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.sett));
        bottomNavigation.show(1, true);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,new Home());
        fragmentTransaction.commit();

        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                if(model.getId()==1){
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new Home());
                    fragmentTransaction.commit();
                } else if (model.getId()==2) {
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new Favorite());
                    fragmentTransaction.commit();
                } else if (model.getId()==3) {
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new Cart());
                    fragmentTransaction.commit();
                }else if (model.getId()==4) {
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container,new Settings());
                    fragmentTransaction.commit();}
                return null;
            }
        });
    }
}