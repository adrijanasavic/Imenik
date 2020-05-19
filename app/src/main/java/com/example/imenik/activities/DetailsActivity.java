package com.example.imenik.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.adapters.AdapterBrojeva;
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
    private AdapterBrojeva adapterBrojeva;

    private DatabaseHelper databaseHelper;

    private SharedPreferences prefs;
    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details );

        setupToolbar();
        fillData();
        preuzmiPodatkeIzIntenta();
        refresh();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        btn_add_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( DetailsActivity.this, AddNumberActivity.class);
                intent.putExtra("objekat_id",kontakt.getId());
                startActivity(intent);
            }
        });


    }

    private void preuzmiPodatkeIzIntenta() {
        objekat_id = getIntent().getIntExtra( "objekat_id", -1 );
        if (objekat_id < 0) {
            Toast.makeText( this, "Greska! " + objekat_id + " ne postoji", Toast.LENGTH_SHORT ).show();
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

    private void delete() {
        AlertDialog dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Brisanje kontakta")
                .setMessage("\n" + "Da li ste sigurni da zelite da izbrisete kontakt?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            List<Broj> slike = getDatabaseHelper().getBrojDao().queryForEq("kontakt_id", kontakt.getId());

                            getDatabaseHelper().getKontaktDao().delete(kontakt);
                            for (Broj slika : slike) {
                                getDatabaseHelper().getBrojDao().delete(slika);

                                String tekstNotifikacije = "Kontakt je obrisan ";

                                boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                                boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                                if (toast) {
                                    Toast.makeText( DetailsActivity.this, tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime(), Toast.LENGTH_LONG ).show();

                                }

                                if (notif) {
                                    NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder( DetailsActivity.this, NOTIF_CHANNEL_ID );
                                    builder.setSmallIcon( android.R.drawable.ic_menu_delete);
                                    builder.setContentTitle( "Notifikacija" );
                                    builder.setContentText( tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime() );

                                    Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher_foreground );


                                    builder.setLargeIcon( bitmap );
                                    notificationManager.notify( 1, builder.build() );

                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        startActivity( new Intent( DetailsActivity.this, MainActivity.class ) );

                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_create:
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                setTitle("Novi broj telefona");
                break;
            case R.id.action_edit:
                Intent intent1 = new Intent( DetailsActivity.this, EditActivity.class);
                intent1.putExtra("objekat_id",kontakt.getId());
                startActivity(intent1);
                break;
            case R.id.action_delete:
                delete();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startActivity( new Intent( DetailsActivity.this, AddActivity.class ) );
                    String tekstNotifikacije = "Kontakt uspesnoo unet!";

                    boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                    boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                    if (toast) {
                        Toast.makeText( DetailsActivity.this, tekstNotifikacije, Toast.LENGTH_LONG ).show();

                    }

                    if (notif) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                        NotificationCompat.Builder builder = new NotificationCompat.Builder( DetailsActivity.this, NOTIF_CHANNEL_ID );
                        builder.setSmallIcon( android.R.drawable.ic_input_add );
                        builder.setContentTitle( "Notifikacija" );
                        builder.setContentText( tekstNotifikacije );

                        Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher );


                        builder.setLargeIcon( bitmap );
                        notificationManager.notify( 1, builder.build() );

                    }


                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };
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

    private void refresh(){
        recyclerView = findViewById(R.id.detail_rv_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterBrojeva = new AdapterBrojeva(phone_list);

        recyclerView.setAdapter( adapterBrojeva );


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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel( NOTIF_CHANNEL_ID, name, importance );
            channel.setDescription( description );

            NotificationManager notificationManager = getSystemService( NotificationManager.class );
            notificationManager.createNotificationChannel( channel );
        }
    }
}
