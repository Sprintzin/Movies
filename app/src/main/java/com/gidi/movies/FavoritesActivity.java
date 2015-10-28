package com.gidi.movies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

public class FavoritesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String TAG = "Favorites Activity";
    private SearchView searchView;
    DbHandler dbHandler;
    AdapterFavs adapterFavs;
    ListView listView;
    public static final String NO_FAB = "no_fab";
    public static final String SEARCH_FROM_FAVS = "search from favs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        setSupportActionBar(toolbar);

        dbHandler = new DbHandler(this);
        listView = (ListView) findViewById(R.id.cardList_fav);

        dbHandler = new DbHandler(this);
        Cursor cursor = dbHandler.queryAll();
        startManagingCursor(cursor);

        adapterFavs = new AdapterFavs(this, cursor);
        listView.setAdapter(adapterFavs);
        adapterFavs.notifyDataSetChanged();
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);


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
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FavoritesActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("query", query);
                editor.apply();
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.setAction(SEARCH_FROM_FAVS);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.setAction(NO_FAB);
        intent.putExtra("id", id);
        startActivity(intent);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_fav, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = menuInfo.id;
        Cursor newCursor;
        switch (item.getItemId()) {
            case R.id.delete:
                dbHandler.delete(id);
                newCursor = dbHandler.queryAll();
                startManagingCursor(newCursor);
                adapterFavs.changeCursor(newCursor);
                return true;
            case R.id.delete_all:
                dbHandler.deleteAll();
                newCursor = dbHandler.queryAll();
                startManagingCursor(newCursor);
                adapterFavs.changeCursor(newCursor);
                return true;
            default:
                super.onContextItemSelected(item);
        }
        return true;


    }


}
