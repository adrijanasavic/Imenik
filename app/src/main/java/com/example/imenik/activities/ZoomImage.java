package com.example.imenik.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.imenik.R;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Kontakt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class ZoomImage extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ImageView imageView;
    private Kontakt kontakt;
    private int objekat_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_zoom_image );

        imageView = findViewById(R.id.zoom_image);

        uzimanjeIzIntenta();

        imageView.setImageBitmap( BitmapFactory.decodeFile( kontakt.getmSlika()));
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void uzimanjeIzIntenta(){
        objekat_id = getIntent().getIntExtra("objekat_id",1);
        try {
            kontakt = getDatabaseHelper().getKontaktDao().queryForId(objekat_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
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
