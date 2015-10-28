package com.gidi.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Gidi on 7/18/2015.
 */
public class DbHandler {

    private DbOpenHelper dbOpenHelper;

    public DbHandler(Context context) {
        dbOpenHelper = new DbOpenHelper(context);
    }

    //DATABASE Methods:

    public long insert(Movie movie) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        // the values to insert:
        ContentValues values = new ContentValues();
        values.put(DbConstants.MOVIE_TITLE, movie.getTitle());
        values.put(DbConstants.MOVIE_PLOT, movie.getPlot());
        values.put(DbConstants.MOVIE_TYPE, movie.getType());
        values.put(DbConstants.MOVIE_YEAR, movie.getYear());
        values.put(DbConstants.MOVIE_IMDBID, movie.getImdbID());
        values.put(DbConstants.MOVIE_POSTER_URL, movie.getPoster());
        values.put(DbConstants.MOVIE_DIRECTOR, movie.getDirector());
        values.put(DbConstants.MOVIE_ACTORS, movie.getActors());
        values.put(DbConstants.MOVIE_IMDB_RATING, movie.getImdbRating());
        values.put(DbConstants.MOVIE_SEEN, movie.isSeen());

        //insert
        long id = db.insertOrThrow(DbConstants.TABLE_MOVIES, null, values);
        db.close();

        return id;
    }

    public int update(Movie movie) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        // the values to update:
        ContentValues values = new ContentValues();
        values.put(DbConstants.MOVIE_TITLE, movie.getTitle());
        values.put(DbConstants.MOVIE_PLOT, movie.getPlot());
        values.put(DbConstants.MOVIE_TYPE, movie.getType());
        values.put(DbConstants.MOVIE_YEAR, movie.getYear());
        values.put(DbConstants.MOVIE_IMDBID, movie.getImdbID());
        values.put(DbConstants.MOVIE_POSTER_URL, movie.getPoster());
        values.put(DbConstants.MOVIE_DIRECTOR, movie.getDirector());
        values.put(DbConstants.MOVIE_ACTORS, movie.getActors());
        values.put(DbConstants.MOVIE_IMDB_RATING, movie.getImdbRating());
        values.put(DbConstants.MOVIE_SEEN, movie.isSeen());

        //update
        int count = db.updateWithOnConflict(
                DbConstants.TABLE_MOVIES,
                values,
                DbConstants.MOVIE_ID + " =?",
                new String[]{movie.getId() + ""},
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return count;
    }


    public int delete(long id) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        //delete
        int count = db.delete(
                DbConstants.TABLE_MOVIES,
                DbConstants.MOVIE_ID + "=?",
                new String[]{id + ""});
        db.close();
        return count;
    }


    //===========================Delete ALL Method======================
    public void deleteAll() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete(DbConstants.TABLE_MOVIES, null, null);
        db.close();
    }
    //============================================================

    public Cursor queryAll() {

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        //query all:
        Cursor cursor = db.query(
                DbConstants.TABLE_MOVIES,
                null,
                null,
                null,
                null,
                null, DbConstants.MOVIE_TITLE + " COLLATE LOCALIZED ASC");
        return cursor;
    }

    public Movie query(long id) {

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        //query one (where _id=id):
        Cursor cursor = db.query(
                DbConstants.TABLE_MOVIES,
                null,
                DbConstants.MOVIE_ID + "=?",
                new String[]{id + ""},
                null,
                null,
                null);

        Movie movie = null;
        //read the data:
        if (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_TITLE));
            String poster = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_POSTER_URL));
            String year = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_YEAR));
            String type = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_TYPE));

            String plot = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_PLOT));
            String director = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_DIRECTOR));
            String actors = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_ACTORS));

            String imdbID = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMDBID));
            String imdbRating = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMDB_RATING));
            boolean seen = (1 == cursor.getInt(cursor.getColumnIndex(DbConstants.MOVIE_SEEN)));


            // create an object:
            movie = new Movie(id, title,  poster,  year,  type,  plot,  director,  actors,  imdbID,  imdbRating,  seen);
        }

        //cursors must be closed!
        cursor.close();

        //return the product
        return movie;
    }

}