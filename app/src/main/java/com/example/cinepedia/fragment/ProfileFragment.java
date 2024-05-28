package com.example.cinepedia.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinepedia.R;
import com.example.cinepedia.activities.EditProfileActivity;
import com.example.cinepedia.activities.WatchlistActivity;
import com.example.cinepedia.helper.DatabaseHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView, phoneTextView;
    private CircleImageView profileImageView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        ImageButton editButton = view.findViewById(R.id.button_edit);
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);
        phoneTextView = view.findViewById(R.id.no_hp);
        profileImageView = view.findViewById(R.id.image_profile);
        Button watchlistButton = view.findViewById(R.id.button_watchlist);

        // Get username from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            // Fetch user data from database
            fetchUserData(username);
        } else {
            Toast.makeText(getContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
        }

        // Set click listener for edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start EditProfileActivity when edit button is clicked
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        watchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WatchlistActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get username from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            // Fetch user data from database
            fetchUserData(username);
        }
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void fetchUserData(String username) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Cursor cursor = databaseHelper.getUserData(username);

        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER));
            byte[] profilePhotoBytes = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROFILE_PHOTO));

            usernameTextView.setText(username != null ? username : "Belum di Set");
            emailTextView.setText((email != null ? email : "Belum di Set"));
            phoneTextView.setText((phoneNumber != null ? phoneNumber : "Belum di Set"));

            if (profilePhotoBytes != null) {
                // Set image profile from database
                Bitmap profilePhotoBitmap = BitmapFactory.decodeByteArray(profilePhotoBytes, 0, profilePhotoBytes.length);
                profileImageView.setImageBitmap(profilePhotoBitmap);
            } else {
                // Set default image if profile photo is not set in the database
                profileImageView.setImageResource(R.drawable.ic_launcher_background);
            }
            cursor.close();
        }
    }

}
