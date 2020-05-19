package com.example.imenik.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imenik.R;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText ime;
    private EditText prezime;
    private EditText adresa;
    private EditText brojTelefona;
    private ImageButton imageButton;
    private String kategorijaBroja;
    private Button btn_add;
    private Button btn_addCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add );

        setSpinnerNumber();
        fillDataContact();
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
}
