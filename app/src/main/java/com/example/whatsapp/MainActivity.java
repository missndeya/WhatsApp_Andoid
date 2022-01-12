package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private Button inscription,connexion;
    private SharedPreferences sp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences ( "connexion" , MODE_PRIVATE );


        String name = sp.getString("login", "");

        if(!name.equalsIgnoreCase(""))
        {

            goToUserActivity ();
            Log.d("ndeye","connecter "+name);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connexion = findViewById(R.id.connexion);
        inscription = findViewById(R.id.inscription);

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Connexion.class);
                startActivity(intent);
            }
        });

        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Inscription.class);
                startActivity(intent);
            }
        });
    }

    public void goToUserActivity() {
        Intent i = new Intent ( this , ActivityUser. class );
        startActivity (i);
    }
}
