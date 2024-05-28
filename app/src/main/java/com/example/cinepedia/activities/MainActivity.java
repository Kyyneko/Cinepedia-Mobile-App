package com.example.cinepedia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinepedia.R;

public class MainActivity extends AppCompatActivity {

    private ImageView cinepediaImageView;
    private Button registerButton;
    private Button loginButton;
    private CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Pengecekan isLoggedIn
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("loggedIn", false);
        if (isLoggedIn) {
            // Jika isLoggedIn true, arahkan ke HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish(); // Tutup MainActivity agar tidak bisa kembali dengan tombol back
        }

        setContentView(R.layout.activity_main);

        cinepediaImageView = findViewById(R.id.cinepedia_image);
        registerButton = findViewById(R.id.button_register);
        loginButton = findViewById(R.id.button_login);
        card = findViewById(R.id.card);

        // Set visibilitas tombol register dan login menjadi gone
        card.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);

        // Menunda visibilitas cinepediaImageView selama 3 detik dengan animasi smooth
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animasi untuk menghilangkan cinepediaImageView
                AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(500); // Durasi animasi: 500 milidetik
                cinepediaImageView.startAnimation(animation);
                cinepediaImageView.setVisibility(View.GONE);

                // Animasi untuk menampilkan tombol register dan login
                AlphaAnimation buttonAnimation = new AlphaAnimation(0.0f, 1.0f);
                buttonAnimation.setDuration(500); // Durasi animasi: 500 milidetik
                registerButton.startAnimation(buttonAnimation);
                loginButton.startAnimation(buttonAnimation);
                registerButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
            }
        }, 3000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
