package com.gidi.movies;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    static final String TAG = "Details Activity";
    static final String ACTION_SNACKBAR = "snackbar";
    public static final String API_ADDRESS = "http://omdbapi.com/";
    public static final String SEARCH_FROM_DETAILS = "search_from_details";
    private SearchView searchView;

    private String imdbID;
    private String title;
    private String year;
    private String type;
    private String plot;
    private String director;
    private String actors;
    private String poster;
    private String imdbRating;
    private boolean seen;

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mYear;
    private ImageView mType;
    private TextView mPlot;
    private TextView mDirector;
    private TextView mActors;
    private TextView mImdbRating;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    long id;
    Movie movie = new Movie( id,  title,  poster,  year,  type,  plot,  director,  actors,  imdbID,  imdbRating,  seen);
    DbHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        dbHandler = new DbHandler(this);

        mPoster = (ImageView) findViewById(R.id.image);
        mTitle = (TextView) findViewById(R.id.title);
        mYear = (TextView) findViewById(R.id.year);
        mType = (ImageView) findViewById(R.id.type);
        mPlot = (TextView) findViewById(R.id.plot);
        mDirector = (TextView) findViewById(R.id.director);
        mActors = (TextView) findViewById(R.id.actors);
        mImdbRating = (TextView)findViewById(R.id.imdbRating);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.c_l);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        Intent callingIntent = getIntent();
        if (callingIntent.getAction().equals(MainActivity.IMDB_ID)){
            imdbID = callingIntent.getStringExtra("imdbID");
            new Search_by_imdbID().execute(API_ADDRESS);
        }else if (callingIntent.getAction().equals(FavoritesActivity.NO_FAB)){
            id = callingIntent.getLongExtra("id", 0);



            setData(id);
        }
    }

    private void setData(long id) {

        Movie movie = dbHandler.query(id);
        //Hide the FAB====================================
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        //================================================
        title = movie.getTitle();
        mTitle.setText(title);
        year = movie.getYear();
        mYear.setText("(" + year + ")");
        poster = movie.getPoster();
        Picasso.with(DetailsActivity.this).load(poster)
                .error(R.drawable.search)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.search)
                .into(mPoster);
        plot=movie.getPlot();
        mPlot.setText("Plot: "+ plot);
        director = movie.getDirector();
        mDirector.setText("Director: " + director);
        actors = movie.getActors();
        mActors.setText("Actors: " + actors);
        imdbRating = movie.getImdbRating();
        mImdbRating.setText(imdbRating);
        type = movie.getType();
        if (type.equals("movie")){
            mType.setImageResource(R.drawable.film);

        }else if(type.equals("series")){
            mType.setImageResource(R.drawable.tv);

        }else if(type.equals("game")){
            mType.setImageResource(R.drawable.game);

        }else{
            mType.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DetailsActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("query", query);
                editor.apply();
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                intent.setAction(SEARCH_FROM_DETAILS);
                intent.putExtra("query", query);
                startActivity(intent);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                movie.setId(0);
                movie = new Movie (id, title,poster,year,type,plot,director,actors,imdbID,imdbRating,false);

                Log.e(TAG, "director is " + director);

                dbHandler.insert(movie);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Added to favorites", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbHandler.delete(movie.getId());
                                finish();
                            }
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();

            break;
        }
    }

    class Search_by_imdbID extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(DetailsActivity.this);
            dialog.setMessage("Wait a minute...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String queryString = null;
            try {
                queryString = "" +
                        "i=" + URLEncoder.encode(imdbID, "utf-8");

            } catch (UnsupportedEncodingException e) {
            }
            return HttpHandler.get(params[0], queryString);
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result == null) {
                Toast.makeText(DetailsActivity.this, "Error getting results...", Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject json = new JSONObject(result);
                    title = json.getString("Title");
                    mTitle.setText(title);
                    plot = json.getString("Plot");
                    mPlot.setText("Plot: " + plot);
                    poster = json.getString("Poster");
                    Picasso.with(DetailsActivity.this).load(poster)
                            .error(R.drawable.search)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.search)
                            .into(mPoster);
                    year = json.getString("Year");
                    mYear.setText("(" + year + ")");
                    type = json.getString("Type");
                    if (type.equals("movie")){
                        mType.setImageResource(R.drawable.film);

                    }else if(type.equals("series")){
                        mType.setImageResource(R.drawable.tv);

                    }else if(type.equals("game")){
                        mType.setImageResource(R.drawable.game);

                    }else{
                        mType.setVisibility(View.INVISIBLE);

                    }

                    imdbID = json.getString("imdbID");
                    imdbRating = json.getString("imdbRating");
                    mImdbRating.setText(imdbRating);
                    director = json.getString("Director");
                    mDirector.setText("Director: " + director);
                    actors = json.getString("Actors");
                    mActors.setText("Actors: " + actors);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailsActivity.this, "Error parsing results...", Toast.LENGTH_LONG).show();
                }

            }
        }
    }


}
