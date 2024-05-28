package com.example.cinepedia.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinepedia.R;
import com.example.cinepedia.helper.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextInputEditText editUsername, editEmail, editPhone;
    private Bitmap selectedProfileImage;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set layout XML
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Inisialisasi Views
        profileImageView = findViewById(R.id.image_profile);
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        Button buttonSave = findViewById(R.id.button_save);
        Button buttonDeleteAccount = findViewById(R.id.button_delete_account);

        // Dapatkan username dari SharedPreferences
        username = getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("username", null);
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        if (username != null) {
            // Ambil data pengguna dari database dan tampilkan di EditText
            loadUserData(username);
        }

        // Set padding untuk fitur EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listener untuk profileImageView
        profileImageView.setOnClickListener(v -> openGallery());

        // Set click listener untuk buttonSave
        buttonSave.setOnClickListener(v -> showConfirmationDialog());

        // Set click listener untuk buttonDeleteAccount
        buttonDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
    }

    @SuppressLint("Range")
    private void loadUserData(String username) {
        Cursor cursor = databaseHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            editUsername.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.getColumnUsername())));
            editEmail.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.getColumnEmail())));
            editPhone.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.getColumnPhoneNumber())));
            byte[] profilePhotoBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.getColumnProfilePhoto()));
            if (profilePhotoBytes != null) {
                Bitmap profilePhoto = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes.length);
                profileImageView.setImageBitmap(profilePhoto);
            }
            cursor.close();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        selectedProfileImage = BitmapFactory.decodeStream(inputStream);
                        profileImageView.setImageBitmap(selectedProfileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    // Tambahkan metode untuk menunjukkan dialog konfirmasi
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to update your profile?")
                .setPositiveButton("Yes", (dialog, which) -> saveUserDataConfirmed())
                .setNegativeButton("No", null)
                .show();
    }

    // Pindahkan kode dari saveUserData() ke metode baru saveUserDataConfirmed()
    private void saveUserDataConfirmed() {
        String newUsername = editUsername.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        byte[] profilePhotoBytes = null;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", newUsername);
        editor.apply();

        if (selectedProfileImage != null) {
            // Kompresi gambar ke format JPEG dengan kualitas 50%
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedProfileImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            profilePhotoBytes = byteArrayOutputStream.toByteArray();
        }

        boolean isUpdated = databaseHelper.updateUser(username, newUsername, newEmail, newPhone, profilePhotoBytes);
        if (isUpdated) {
            Toast.makeText(this, "Profile Is Updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("fragment", "ProfileFragment");
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setMessage("Are You Sure You Want To Delete Your Account?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        boolean isDeleted = databaseHelper.deleteUser(username);
        if (isDeleted) {
            Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
            getSharedPreferences("loginPrefs", MODE_PRIVATE).edit().putBoolean("loggedIn", false).apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to Delete Account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Yakin ingin kembali? Perubahan Anda akan terhapus.")
                .setPositiveButton("Ya", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
