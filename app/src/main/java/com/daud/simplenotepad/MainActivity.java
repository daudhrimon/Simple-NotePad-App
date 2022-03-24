package com.daud.simplenotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MySp", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String SignIn = sharedPreferences.getString("SignIn", "");

        if (SignIn.equals("true")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLay, new HomeFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLay, new SignInFragment()).commit();
        }
    }

    // For Close Keyboard //
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Doing Shared preference Empty //
    public static void setSharedPreferencesEmpty() {
        editor.putString("Title", "");
        editor.putString("Note", "");
        editor.putString("Key", "");
        editor.putString("State", "");
        editor.commit();
    }
    // Bitmap to Uri Converter
    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title", null);
        return Uri.parse(path);
    }
}