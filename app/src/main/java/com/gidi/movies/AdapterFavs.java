package com.gidi.movies;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Sprintzin on 22/10/2015.
 */
public class AdapterFavs extends CursorAdapter {
    LruCache<String, Bitmap> bitmapCache;
    String a;


    class ViewHolder {
        long id;
        TextView title;
        TextView year;
        TextView imdbRating;
        ImageView poster;
        ImageView type;
        TextView director;
        TextView actors;
    }


    public AdapterFavs(Context context, Cursor c) {
        super(context, c, 0);

        // prepare a cache for the images.
        // key : the url. value : the bitmap.

        //max size : 4 MB
        int numImages = 4 * 1024 * 1024;
        this.bitmapCache = new LruCache<String, Bitmap>(numImages) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // this is how to calculate a bitmap size in bytes.
                // (bytes-in-a-row * height)
                return value.getRowBytes() * value.getHeight();
            }
        };
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_favs, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.txt_title_fav);
        holder.year = (TextView) view.findViewById(R.id.year_fav);
        holder.imdbRating = (TextView) view.findViewById(R.id.imdbRating_fav);
        holder.poster = (ImageView) view.findViewById(R.id.imageView_fav);
        holder.type = (ImageView) view.findViewById(R.id.type_fav);
        holder.director = (TextView) view.findViewById(R.id.fav_director);
        holder.actors = (TextView) view.findViewById(R.id.fav_actors);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbConstants.MOVIE_ID));

        String title_fav = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_TITLE));
        String year_fav = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_YEAR));

        String imdbRating_fav = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_IMDB_RATING));
        String director_fav = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_DIRECTOR));
        String actors_fav = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_ACTORS));

        String poster_favs = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_POSTER_URL));
        String type_favs = cursor.getString(cursor.getColumnIndex(DbConstants.MOVIE_TYPE));


        ViewHolder holder = (ViewHolder) view.getTag();
        holder.id = id;
        holder.title.setText(title_fav);
        holder.year.setText("(" + year_fav + ")");
        holder.imdbRating.setText(imdbRating_fav);
        holder.director.setText("Director: "+director_fav);
        holder.actors.setText("Actors: "+ actors_fav);
        if (type_favs.equals("movie")) {
            holder.type.setImageResource(R.drawable.film);

        } else if (type_favs.equals("series")) {
            holder.type.setImageResource(R.drawable.tv);

        } else if (type_favs.equals("game")) {
            holder.type.setImageResource(R.drawable.game);

        } else {
            holder.type.setVisibility(View.INVISIBLE);

        }


        Bitmap cachedBmp = bitmapCache.get(poster_favs);
        if (cachedBmp != null) {
            // we do - just use it!
            holder.poster.setVisibility(View.VISIBLE);
            holder.poster.setImageBitmap(cachedBmp);
        } else {
            //we don't... get it async
            //until the image is downloaded - hide the image view:
            holder.poster.setVisibility(View.INVISIBLE);

            GetImageTask task = new GetImageTask(id, holder);
            task.execute(poster_favs);
        }
    }


    class GetImageTask extends AsyncTask<String, Void, Bitmap> {


        private final long id;
        private final AdapterFavs.ViewHolder holder;

        public GetImageTask(long id, AdapterFavs.ViewHolder holder) {
            this.id = id;
            this.holder = holder;
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            //download:
            String poster = params[0];
            Bitmap bitmap = HttpHandler.getBitmap(poster, null);

            //save it in the cache for later:
            if (bitmap != null) {

                bitmapCache.put(poster, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            // check - is the view's holder still holding the same movie we started the download for?
            // or did the view get recycled and now displaying a different movie?
            if (id == holder.id) {
                // it's still the same movie !

                //restore the visibility and show the thumb
                holder.poster.setVisibility(View.VISIBLE);

                if (result != null) {
                    holder.poster.setImageBitmap(result);
                } else {
                    //error in download...
                    holder.poster.setImageResource(R.mipmap.ic_launcher);
                }
            }
        }
    }
}
