package com.example.cinepedia.activities;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.button_register);

        // Set onClickListener untuk tombol login
        loginButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validasi apakah password sesuai dengan persyaratan
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password harus terdiri dari minimal 6 karakter, " +
                    "minimal 1 huruf kapital, dan minimal 1 angka", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek apakah username sudah ada dalam database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.checkIfUsernameExists(username)) {
            // Jika username sudah ada, tampilkan pesan kesalahan
            Toast.makeText(this, "Username sudah terdaftar, silakan gunakan username lain", Toast.LENGTH_SHORT).show();
        } else {
            // Simpan data user ke database
            boolean success = databaseHelper.addUser(username, password);

            if (success) {
                // User berhasil didaftarkan, arahkan ke LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Gagal mendaftarkan user", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isPasswordValid(String password) {
        // Validasi panjang password
        if (password.length() < 6) {
            return false;
        }

        // Validasi minimal 1 huruf kapital
        boolean hasUppercase = !password.equals(password.toLowerCase());

        // Validasi minimal 1 angka
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasDigit;
    }
}
