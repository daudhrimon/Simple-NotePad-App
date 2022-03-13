package com.daud.simplenotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MySp",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String SignIn = sharedPreferences.getString("SignIn","");

        if (SignIn.equals("true")){
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLay,new HomeFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLay,new SignInFragment()).commit();
        }
    }
}