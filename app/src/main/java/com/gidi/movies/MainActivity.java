package com.gidi.movies;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SearchView searchView;
    private RecyclerView recList;
    List<Movie> movieList;
    private Adapter adapter;
    private String mQuery;
    static final String TAG = "Main Activity";
    public static final String IMDB_ID = "imdb id";
    public static final String API_ADDRESS = "http://omdbapi.com/";
    private FloatingActionButton fab_fav;
    private String json_result = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (movieList == null){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String q = sp.getString("query", "a");
            if (q!=null && !q.equals("a")){
                mQuery=q;
                new Send_API().execute(API_ADDRESS);

            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        fab_fav = (FloatingActionButton) findViewById(R.id.fab_fav);
        fab_fav.setOnClickListener(this);
        movieList = new ArrayList<Movie>();
        adapter = new Adapter(this, movieList);
        recList.setAdapter(adapter);


        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.setAction(IMDB_ID);
                String imdbID = movieList.get(position).getImdbID();
                intent.putExtra("imdbID", imdbID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        super.onBackPressed();

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

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("query", query);
                editor.apply();
                new Send_API().execute(API_ADDRESS);
                searchView.setQuery("", false);
                searchView.setIconified(true);


                return true;

            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_fav:
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                break;

        }
    }


    //=======================================Send class===============================
    class Send_API extends AsyncTask<String, Void, String> {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String txt = sp.getString("query", "a");

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (movieList != null) {
                movieList.clear();
                adapter.notifyDataSetChanged();
            }

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Searching...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String queryString = null;
            try {
                queryString = "" +
                        "s=" + URLEncoder.encode(txt, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            return HttpHandler.get(params[0], queryString);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            json_result = result;
            dialog.dismiss();
            if (result == null) {
                Toast.makeText(MainActivity.this, "error getting results...", Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject json = new JSONObject(result);

                    JSONArray searchArray = json.getJSONArray("Search");

                    for (int i = 0; i < searchArray.length(); i++) {
                        JSONObject searchObject = searchArray.getJSONObject(i);
                        String title = searchObject.getString("Title");
                        String type = searchObject.getString("Type");
                        String year = searchObject.getString("Year");
                        String imdbID = searchObject.getString("imdbID");
                        String poster_url = searchObject.getString("Poster");
                        movieList.add(new Movie(title, type, year, imdbID, poster_url));

                        Log.e(TAG, "MovieList is " + movieList);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "error parsing results...", Toast.LENGTH_LONG).show();
                }

                adapter.notifyDataSetChanged();

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("json", json_result);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        json_result = savedInstanceState.getString("json");
        if (json_result == null) {
            movieList.clear();
        } else {
            try {
                JSONObject json = new JSONObject(json_result);

                JSONArray searchArray = json.getJSONArray("Search");

                for (int i = 0; i < searchArray.length(); i++) {
                    JSONObject searchObject = searchArray.getJSONObject(i);
                    String title = searchObject.getString("Title");
                    String type = searchObject.getString("Type");
                    String year = searchObject.getString("Year");
                    String imdbID = searchObject.getString("imdbID");
                    String poster_url = searchObject.getString("Poster");
                    movieList.add(new Movie(title, type, year, imdbID, poster_url));

                    Log.e(TAG, "MovieList is " + movieList);


                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "error parsing results...", Toast.LENGTH_LONG).show();
            }

            adapter.notifyDataSetChanged();
        }
    }
}
