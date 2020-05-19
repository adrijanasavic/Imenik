package com.example.imenik.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Kontakt;
import com.example.imenik.dialog.AboutDialog;
import com.example.imenik.settings.SettingsActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> drawerItems;
    private ListView drawerList;

    private static final int SELECT_PICTURE = 1;
    private String imagePath;

    private EditText ime;
    private EditText prezime;
    private EditText adresa;
    private ImageButton imageButton;
    private Button btn_editSave;
    private Kontakt kontakt;
    private int kontakt_id;
    private DatabaseHelper databaseHelper;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit );

        fillDataDrawer();
        setupToolbar();
        setupDrawer();

        fillData();
        preuzmiPodatkeIzIntenta();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        } );
        btn_editSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();

            }
        } );

    }

    private void fillData() {
        ime = findViewById( R.id.edit_ime );
        prezime = findViewById( R.id.edit_prezime );
        adresa = findViewById( R.id.edit_adresa );
        btn_editSave = findViewById( R.id.edit_save );
        imageButton = findViewById( R.id.edit_image );
    }

    private void preuzmiPodatkeIzIntenta() {
        kontakt_id = getIntent().getIntExtra( "objekat_id", -1 );
        if (kontakt_id < 0) {
            Toast.makeText( this, "Greska! " + kontakt_id + " ne postoji", Toast.LENGTH_SHORT ).show();
            finish();
        }
        try {
            kontakt = getDatabaseHelper().getKontaktDao().queryForId( kontakt_id );
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ime.setText( kontakt.getmIme() );
        prezime.setText( kontakt.getmPrezime() );
        adresa.setText( kontakt.getmAdresa() );
        imageButton.setImageBitmap( BitmapFactory.decodeFile( kontakt.getmSlika() ) );
    }

    private void saveChanges() {
        kontakt.setmIme( ime.getText().toString() );
        kontakt.setmPrezime( prezime.getText().toString() );
        kontakt.setmAdresa( adresa.getText().toString() );

        kontakt.setmSlika( imagePath );

        try {
            getDatabaseHelper().getKontaktDao().createOrUpdate( kontakt );

            String tekstNotifikacije = "Kontakt je izmenjen ";

            boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
            boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

            if (toast) {
                Toast.makeText( this, tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime(), Toast.LENGTH_LONG ).show();

            }

            if (notif) {
                NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                NotificationCompat.Builder builder = new NotificationCompat.Builder( EditActivity.this, NOTIF_CHANNEL_ID );
                builder.setSmallIcon( android.R.drawable.ic_input_add );
                builder.setContentTitle( "Notifikacija" );
                builder.setContentText( tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime() );

                Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher_foreground );


                builder.setLargeIcon( bitmap );
                notificationManager.notify( 1, builder.build() );

            }
//            Toast.makeText(this, kontakt + " izmenjen", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent( EditActivity.this, DetailsActivity.class );
        intent.putExtra( "objekat_id", kontakt_id );
        startActivity( intent );
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
                        startActivity( new Intent( EditActivity.this, MainActivity.class ) );
                        break;
                    case 1:
                        Toast.makeText( getBaseContext(), "Prikaz podesavanja", Toast.LENGTH_SHORT );
                        title = "Podesavanja";
                        startActivity( new Intent( EditActivity.this, SettingsActivity.class ) );
                        break;
                    case 2:
                        title = "O aplikaciji";
                        AboutDialog dialog = new AboutDialog( EditActivity.this );
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE )
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1 );
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    private void selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( i, SELECT_PICTURE );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query( selectedImage, filePathColumn, null, null, null );
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex( filePathColumn[0] );
                    String picturePath = cursor.getString( columnIndex );
                    imagePath = picturePath;
                    cursor.close();

                    if (imageButton != null) {
                        imageButton.setImageBitmap( BitmapFactory.decodeFile( imagePath ) );
                    }

                    //  Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
                }
            }
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
