# Cinepedia-Mobile_App

NAMA : MAHENDRA KIRANA M.B
NIM : H071221058
KELAS : D

- Cinepedia-Mobile-App adalah sebuah aplikasi android yang dirancang untuk memudahkan 
- para user penikmat film dan tv show untuk mendapatkan akses info film dan tv show dengan mudah

| Fitur Fitur Aplikasi |

1. Login dan Register ✅
2. Show All Movies ✅
3. Show Trending Movie ✅
4. Show Top Rated Movie ✅
5. Show Coming Soon Movie ✅
6. Show New Release Movie ✅
7. Show Popular Tv Show ✅
8. Profile Account ✅
9. Add Watchlist ✅
10. Searching Movie ✅
11. Show Detail Movie ✅
12. Show Trailer Movie ✅
13. CRUD Profile Account Users ✅
14. CRUD Watchlist Users ✅

| Spesifikasi Teknis |

1. Activity ✅
2. Intent ✅
3. Recycler View ✅
4. Fragment & Navigation ✅
5. Background Thread ✅
6. Networking ✅
   - API TMDB ✅
7. Local Data Persistent : ✅
   - SQLite Database ->
     Table Users : - id - username - password - email - phone number - profile_photo
     Table Watchlist : - id - user_id references id (table Users) - movie_id - title - poster_path

   - Shared Preferences -> loginPrefs : - username - loggedIn
