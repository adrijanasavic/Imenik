package com.example.imenik.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.adapters.AdapterContact;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Kontakt;
import com.example.imenik.dialog.AboutDialog;
import com.example.imenik.settings.SettingsActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterContact.OnRecyclerItemClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> drawerItems;
    private ListView drawerList;

    private RecyclerView recyclerView;
    private AdapterContact adapterContact;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        fillDataDrawer();
        setupToolbar();
        setupDrawer();

        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                startActivity( new Intent( this, AddActivity.class ) );
                setTitle( "Novi kontakt" );
                break;
            case R.id.action_settings:
                startActivity( new Intent( this, SettingsActivity.class ) );
                setTitle( "Podesavanja" );
                break;
            case R.id.action_about:
                AboutDialog dialog = new AboutDialog( this );
                dialog.show();
                setTitle( "O aplikaciji" );
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    public void updateTitle(String text) {
        setTitle( text );
    }

    private void fillDataDrawer() {
        drawerItems = new ArrayList<>();
        drawerItems.add( "Lista kontakata" );
        drawerItems.add( "Podesavanja" );
        drawerItems.add( "O aplikaciji" );
    }

    private void setupDrawer() {
        drawerList = findViewById( R.id.left_drawer );
        drawerLayout = findViewById( R.id.drawer_layout );
        drawerList.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, drawerItems ) );
        drawerList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Lista kontakata";
                        startActivity( new Intent( MainActivity.this, MainActivity.class ) );
                        break;
                    case 1:
                        Toast.makeText( getBaseContext(), "Prikaz podesavanja", Toast.LENGTH_SHORT );
                        title = "Podesavanja";
                        startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
                        break;
                    case 2:
                        title = "O aplikaciji";
                        AboutDialog dialog = new AboutDialog( MainActivity.this );
                        dialog.show();
                        break;
                    default:
                        break;
                }
                setTitle( title );
                drawerLayout.closeDrawer( drawerList );
            }
        } );

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle( "" );
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle( "" );
                invalidateOptionsMenu();
            }
        };
    }

    public void setupToolbar() {
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbar.setTitleTextColor( Color.WHITE );
        toolbar.setSubtitle( "Lista kontakata" );
        // toolbar.setLogo( R.drawable.heart );

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.drawer );
            actionBar.setHomeButtonEnabled( true );
            actionBar.show();
        }
    }

    @Override
    public void onRVItemClick(Kontakt objekat) {

        Toast.makeText( this, "Detalji kontakta " + objekat.getmIme() + " " + objekat.getmPrezime(), Toast.LENGTH_SHORT ).show();
        Intent intent = new Intent( this, DetailsActivity.class );
        intent.putExtra( "objekat_id", objekat.getId() );

        startActivity( intent );
    }

    private void refresh() {
        recyclerView = findViewById( R.id.rvList );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        try {
            adapterContact = new AdapterContact( getDatabaseHelper().getKontaktDao().queryForAll(), this );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter( adapterContact );

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
