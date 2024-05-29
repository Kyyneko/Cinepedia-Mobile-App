package com.example.cinepedia.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cinepedia.db";
    private static final int DATABASE_VERSION = 1;

    // Tabel Users
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_PROFILE_PHOTO = "profile_photo"; // Kolom baru

    // Tabel Watchlist
    private static final String TABLE_WATCHLIST = "Watchlist";
    private static final String COLUMN_WATCHLIST_ID = "id";
    private static final String COLUMN_USERS_ID = "id_users";
    public static final String COLUMN_MOVIE_ID = "id_movie"; // Ubah atribute menjadi public agar bisa diakses di luar kelas

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " +
            TABLE_USERS + "(" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_EMAIL + " TEXT," +
            COLUMN_PHONE_NUMBER + " TEXT," +
            COLUMN_PROFILE_PHOTO + " TEXT" +
            ")";

    private static final String CREATE_TABLE_WATCHLIST = "CREATE TABLE " +
            TABLE_WATCHLIST + "(" +
            COLUMN_WATCHLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERS_ID + " INTEGER," +
            COLUMN_MOVIE_ID + " INTEGER," +
            "title TEXT," +
            "poster_path TEXT," +
            "FOREIGN KEY (" + COLUMN_USERS_ID + ") REFERENCES " +
            TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
            ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_WATCHLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_WATCHLIST + " ADD COLUMN title TEXT");
            db.execSQL("ALTER TABLE " + TABLE_WATCHLIST + " ADD COLUMN poster_path TEXT");
        }
        // Jika versi lebih dari 2, tambahkan perubahan schema lainnya di sini.
    }


    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                null,
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );
    }

    // Method to update user data
    public boolean updateUser(String oldUsername, String newUsername, String email, String phoneNumber, byte[] profilePhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        if (profilePhoto != null) {
            values.put(COLUMN_PROFILE_PHOTO, profilePhoto);
        }

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{oldUsername});
        return rowsAffected > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean checkIfUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, // Tabel yang dipilih
                new String[]{COLUMN_USERNAME}, // Kolom yang dipilih
                COLUMN_USERNAME + "=?", // Klausa WHERE
                new String[]{username}, // Argumen klausa WHERE
                null, // GROUP BY
                null, // HAVING
                null); // ORDER BY

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public boolean addWatchlist(int userId, int movieId, String title, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERS_ID, userId);
        values.put(COLUMN_MOVIE_ID, movieId);
        values.put("title", title);
        values.put("poster_path", posterPath);
        long result = db.insert(TABLE_WATCHLIST, null, values);
        db.close();
        return result != -1;
    }



    @SuppressLint("Range")
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Inisialisasi user_id dengan nilai default -1

        // Query untuk mencari user_id berdasarkan username
        String selectQuery = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});

        // Jika cursor tidak kosong, ambil user_id
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
        }

        return userId;
    }

    public Cursor getWatchlistData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_WATCHLIST,
                null,
                COLUMN_USERS_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );
    }

    public boolean isMovieInWatchlist(int userId, int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean result = false;

        try {
            // Query untuk memeriksa apakah film ada di watchlist pengguna
            String query = "SELECT * FROM " + TABLE_WATCHLIST +
                    " WHERE " + COLUMN_USERS_ID + " = ?" +
                    " AND " + COLUMN_MOVIE_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(movieId)});

            // Jika cursor memiliki data, maka film ada di watchlist
            if (cursor != null && cursor.getCount() > 0) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while checking if movie is in watchlist: " + e.getMessage());
        } finally {
            // Menutup cursor
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public boolean deleteMovieFromWatchlist(int userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USERS_ID + " = ?" + " AND " + COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(movieId)};
        int deletedRows = db.delete(TABLE_WATCHLIST, selection, selectionArgs);
        db.close();
        return deletedRows > 0;
    }



    public static String getColumnMovieId() {
        return COLUMN_MOVIE_ID;
    }

    // Static getter methods for column names
    public static String getColumnUsername() {
        return COLUMN_USERNAME;
    }

    public static String getColumnEmail() {
        return COLUMN_EMAIL;
    }

    public static String getColumnPhoneNumber() {
        return COLUMN_PHONE_NUMBER;
    }

    public static String getColumnProfilePhoto() {
        return COLUMN_PROFILE_PHOTO;
    }

    public static String getColumnUserId() {
        return COLUMN_USER_ID;
    }
}
