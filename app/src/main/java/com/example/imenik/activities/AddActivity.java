package com.example.imenik.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imenik.R;
import com.example.imenik.db.DatabaseHelper;
import com.example.imenik.db.model.Broj;
import com.example.imenik.db.model.Kontakt;
import com.example.imenik.tools.Tools;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity  {

    private Spinner spinner;
    private EditText ime;
    private EditText prezime;
    private EditText adresa;
    private EditText brojTelefona;
    private ImageButton imageButton;
    private String kategorijaBroja;
    private Button btn_add;
    private Button btn_addCancel;

    private Kontakt kontakt;
    private Broj broj;
    private String image_path;

    DatabaseHelper databaseHelper;
    private static final int SELECT_PICTURE = 1;


    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";
    private SharedPreferences prefs;

    private ImageView preview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add );

        setSpinnerNumber();
        fillDataContact();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        saveData();

        //        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isStoragePermissionGranted()) {
//                    Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(i, SELECT_PICTURE);
//                }            }
//        });
//
//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                add();
//
//            }
//        });
//
//        btn_addCancel.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        } );
//
    }

    public void saveData(){

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_PICTURE);
                }            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add();

            }
        });

        btn_addCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );


    }
    private void add(){
//        if (preview == null || image_path == null) {
//                Toast.makeText( AddActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT ).show();
//                return;
//            }
        if(Tools.validateInput( ime )
                && Tools.validateInput( prezime )
                && Tools.validateInput( adresa )
                && Tools.validateInput( brojTelefona )



        ) {
            kategorijaBroja = spinner.getSelectedItem().toString();
            Toast.makeText(this, "Selektovano je "+ kategorijaBroja, Toast.LENGTH_SHORT).show();


            kontakt = new Kontakt();
            kontakt.setmIme( ime.getText().toString().trim());
            kontakt.setmPrezime( prezime.getText().toString().trim());
            kontakt.setmAdresa( adresa.getText().toString().trim() );
            kontakt.setmSlika(image_path);

            broj = new Broj();
            broj.setKontakt(kontakt);
            broj.setmKategorijaTel( kategorijaBroja );
            broj.setmTelefon( brojTelefona.getText().toString().trim());

            try {
                getDatabaseHelper().getKontaktDao().create(kontakt);
                getDatabaseHelper().getBrojDao().create(broj);

                String tekstNotifikacije = "Unet je novi kontakt ";

                boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                if (toast) {
                    Toast.makeText( this, tekstNotifikacije + kontakt.getmIme() + kontakt.getmPrezime(), Toast.LENGTH_LONG ).show();

                }

                if (notif) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                    NotificationCompat.Builder builder = new NotificationCompat.Builder( AddActivity.this, NOTIF_CHANNEL_ID );
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


            startActivity( new Intent( this, MainActivity.class ) );
            finish();
        }
    }

    private void fillDataContact(){
        imageButton = findViewById(R.id.add_imageButton);
        ime = findViewById(R.id.add_ime);
        prezime = findViewById(R.id.add_prezime);
        adresa = findViewById( R.id.add_adresa );
        brojTelefona = findViewById(R.id.add_brojTelefona);
        btn_add = findViewById(R.id.btn_add);
        btn_addCancel = findViewById( R.id.btn_addCancel );


    }

    private List<String> fillSpinner(){
        List<String> list = new ArrayList<>();
        list.add("Kucni");
        list.add("Poslovni");
        list.add("Mobilni");
        return list;
    }

    private void setSpinnerNumber() {
        spinner = findViewById(R.id.spinner_new_contact);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,fillSpinner());
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategorijaBroja = parent.getItemAtPosition(position).toString();
                Toast.makeText( AddActivity.this, "Vi birate " + kategorijaBroja, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText( AddActivity.this, "Odabrana je podrazumevana vrednost " + parent.getItemAtPosition(0), Toast.LENGTH_SHORT).show();
            }
        });
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    image_path = picturePath;
                    cursor.close();

                    imageButton.setImageBitmap( BitmapFactory.decodeFile(image_path));
                }
            }
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
