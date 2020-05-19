package com.example.imenik.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Broj;
import com.example.imenik.db.model.Kontakt;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView firstName;
    private TextView lastName;
    private TextView adress;
    private ImageView image;
    private Button btn_add_number;
    private Kontakt kontakt;
    private int objekat_id;
    private List<Broj> phone_list;

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details );

        setupToolbar();
        fillData();
        preuzmiPodatkeIzIntenta();


    }

    private void preuzmiPodatkeIzIntenta() {
        objekat_id = getIntent().getIntExtra( "objekat_id", -1 );
        if (objekat_id < 0) {
            Toast.makeText( this, "Greska! " + objekat_id + " ne pstoji", Toast.LENGTH_SHORT ).show();
            finish();
        }
        try {
            kontakt = getDatabaseHelper().getKontaktDao().queryForId( objekat_id );
            phone_list = getDatabaseHelper().getBrojDao().queryForEq( "kontakt_id", objekat_id );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        firstName.setText( kontakt.getmIme() );
        lastName.setText( kontakt.getmPrezime() );
        adress.setText( kontakt.getmAdresa() );
        image.setImageBitmap( BitmapFactory.decodeFile( kontakt.getmSlika() ) );

    }

    private void fillData() {
        firstName = findViewById( R.id.detail_tv_firstName );
        lastName = findViewById( R.id.detail_tv_lastName );
        adress = findViewById( R.id.detail_tv_adress );
        image = findViewById( R.id.detail_imageview );
        phone_list = new ArrayList<>();
        recyclerView = findViewById( R.id.detail_rv_container );
        btn_add_number = findViewById( R.id.detail_btn_add_number );

        image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( DetailsActivity.this, ZoomImage.class );
                intent.putExtra( "objekat_id", kontakt.getId() );
                startActivity( intent );
            }
        } );
    }

    public void setupToolbar() {
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbar.setTitleTextColor( Color.WHITE );
        toolbar.setSubtitle( "Detalji kontakata" );
        // toolbar.setLogo( R.drawable.heart );

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.drawer );
            actionBar.setHomeButtonEnabled( true );
            actionBar.show();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            kontakt = getDatabaseHelper().getKontaktDao().queryForId( objekat_id );
            phone_list.clear();
            phone_list.addAll( kontakt.getBroj() );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
