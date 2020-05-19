package com.example.imenik.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "brojevi")
public class Broj {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "mTelefon")
    private String mTelefon;
    @DatabaseField(columnName = "mKategorijaTel")
    private String mKategorijaTel;
    @DatabaseField(foreign = true,foreignAutoRefresh = true,foreignAutoCreate = true)
    private Kontakt mKontakt;

    public Broj() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmTelefon() {
        return mTelefon;
    }

    public void setmTelefon(String mTelefon) {
        this.mTelefon = mTelefon;
    }

    public String getmKategorijaTel() {
        return mKategorijaTel;
    }

    public void setmKategorijaTel(String mKategorijaTel) {
        this.mKategorijaTel = mKategorijaTel;
    }

    public Kontakt getmKontakt() {
        return mKontakt;
    }

    public void setmKontakt(Kontakt mKontakt) {
        this.mKontakt = mKontakt;
    }

    @Override
    public String toString() {
        return mTelefon + " " + mKategorijaTel;
    }
}
