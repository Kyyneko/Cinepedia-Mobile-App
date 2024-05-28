package com.example.cinepedia.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinepedia.R;
import com.example.cinepedia.helper.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Inisialisasi view
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.button_login);

        // Set onClickListener untuk tombol login
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Cek apakah username dan password kosong
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username atau password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Memeriksa kecocokan username dan password di database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean userExists = databaseHelper.checkUser(username, password);

        if (userExists) {
            // Username dan password benar, simpan informasi login dan arahkan ke HomeActivity
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putBoolean("loggedIn", true);
            editor.apply();

            // Tampilkan pesan toast selamat datang
            Toast.makeText(this, "Selamat datang, " + username, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Username atau password salah, tampilkan pesan kesalahan
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
        }
    }
}
