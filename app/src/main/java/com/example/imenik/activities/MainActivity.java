package com.example.imenik.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.example.imenik.dialog.AboutDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> drawerItems;
    private ListView drawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        fillDataDrawer();
        setupToolbar();
        setupDrawer();
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
                setTitle( "Novi kontakt" );
                break;
            case R.id.action_settings:
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
}
