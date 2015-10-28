package com.gidi.movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbOpenHelper extends SQLiteOpenHelper {

    final static String TAG = "DbOpenHelper";

    // database file:
    public static String DB_NAME = "myMovies.db";

    // database version:
    public static int DB_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = ""
                + "CREATE TABLE "
                + DbConstants.TABLE_MOVIES +     " ("
                + DbConstants.MOVIE_ID +         " INTEGER PRIMARY KEY AUTOINCREMENT,"
                 +DbConstants.MOVIE_TITLE +      " TEXT,"
                + DbConstants.MOVIE_POSTER_URL+  " TEXT,"
                + DbConstants.MOVIE_YEAR +       " TEXT,"
                + DbConstants.MOVIE_TYPE+        " TEXT,"
                + DbConstants.MOVIE_PLOT +       " TEXT,"
                + DbConstants.MOVIE_DIRECTOR+    " TEXT,"
                + DbConstants.MOVIE_ACTORS+      " TEXT,"
                + DbConstants.MOVIE_IMDBID+      " TEXT,"
                + DbConstants.MOVIE_IMDB_RATING+ " TEXT,"
                + DbConstants.MOVIE_SEEN +       " INTEGER"
                +                                ")";
        Log.e(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DbConstants.TABLE_MOVIES;
        Log.d(TAG, sql);
        db.execSQL(sql);

        // recreate database:
        onCreate(db);
    }

}

