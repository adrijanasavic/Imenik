package com.example.imenik.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Broj;
import com.example.imenik.db.model.Kontakt;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddNumberActivity extends AppCompatActivity {

    private EditText broj;
    private Spinner spinner;
    private Button btn_addBroj;
    private Button btn_cancelBroj;
    private int objekat_id;
    private String kategorijaBroja;
    private Kontakt kontakt;

    private DatabaseHelper databaseHelper;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_number );

        addNumber();
        setSpinnerNewNumber();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );


        btn_addBroj.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNovogBrojaTelefona();
            }
        } );

        btn_cancelBroj.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

    }


    private void addNumber() {
        broj = findViewById( R.id.add_noviBroj );
        btn_addBroj = findViewById( R.id.btn_addNoviBroj );
        btn_cancelBroj = findViewById( R.id.btn_cancelNoviBroj );

    }

    private List<String> fillSpinner() {
        List<String> list = new ArrayList<>();
        list.add( "Kucni" );
        list.add( "Poslovni" );
        list.add( "Mobilni" );

        return list;
    }

    private void setSpinnerNewNumber() {
        spinner = findViewById( R.id.add_number_spinner );
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, fillSpinner() );
        spinner.setAdapter( spinnerAdapter );
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategorijaBroja = parent.getItemAtPosition( position ).toString();
                Toast.makeText( AddNumberActivity.this, "Vi birate " + kategorijaBroja, Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText( AddNumberActivity.this, "Odabrana je podrazumevana vrednost " + parent.getItemAtPosition( 0 ), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void addNovogBrojaTelefona() {
        objekat_id = getIntent().getIntExtra( "objekat_id", 1 );

        try {

            kontakt = getDatabaseHelper().getKontaktDao().queryForId( objekat_id );

            String tekstNotifikacije = "Unet je novi broj telefona ";

            boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
            boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

            if (toast) {
                Toast.makeText( this, tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime(), Toast.LENGTH_LONG ).show();

            }

            if (notif) {
                NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                NotificationCompat.Builder builder = new NotificationCompat.Builder( AddNumberActivity.this, NOTIF_CHANNEL_ID );
                builder.setSmallIcon( android.R.drawable.ic_input_add );
                builder.setContentTitle( "Notifikacija" );
                builder.setContentText( tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime() );

                Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher_foreground );


                builder.setLargeIcon( bitmap );
                notificationManager.notify( 1, builder.build() );

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Broj broj = new Broj();
        broj.setmTelefon( this.broj.getText().toString() );
        broj.setmKategorijaTel( kategorijaBroja );
        broj.setKontakt( kontakt );


        try {
            getDatabaseHelper().getBrojDao().createIfNotExists( broj );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent( this, DetailsActivity.class );
        intent.putExtra( "objekat_id", objekat_id );
        startActivity( intent );

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
