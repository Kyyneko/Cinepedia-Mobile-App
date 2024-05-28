package com.example.cinepedia.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.cinepedia.R;
import com.example.cinepedia.fragment.TVShowFragment;
import com.example.cinepedia.fragment.HomeFragment;
import com.example.cinepedia.fragment.ProfileFragment;
import com.example.cinepedia.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set Toolbar sebagai ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            // Set fragment default saat aplikasi pertama kali dibuka
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, new HomeFragment())
                    .commit();
        }

        // Cek koneksi internet saat activity dibuat
        if (!isConnected()) {
            showNoInternetDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            // Tampilkan dialog konfirmasi logout
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to log out from your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Logout user dan arahkan ke MainActivity
                    SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("loggedIn", false);
                    editor.apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Tutup HomeActivity
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        // Cek koneksi internet sebelum mengganti fragment
        if (!isConnected()) {
            showNoInternetDialog();
            return false;
        }

        if (id == R.id.menu_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.menu_search) {
            fragment = new SearchFragment();
        } else if (id == R.id.menu_favorite) {
            fragment = new TVShowFragment();
        } else if (id == R.id.menu_profile) {
            fragment = new ProfileFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
            item.setChecked(true);
        }

        return true;
    }

    // Metode untuk memeriksa koneksi internet
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Method to show dialog when no internet connection
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tidak Ada Koneksi Internet");
        builder.setMessage("Aplikasi memerlukan koneksi internet untuk bekerja. Mohon aktifkan data seluler atau Wi-Fi Anda.");
        builder.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Directing user to Settings to enable internet connection
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (!isBackPressed) {
            isBackPressed = true;
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menutup aplikasi?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity(); // Menutup semua aktivitas yang terkait dengan aplikasi
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isBackPressed = false; // Set kembali ke false agar dialog dapat muncul kembali saat tombol kembali ditekan lagi
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
