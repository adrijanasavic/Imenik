package com.example.imenik.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "kontakti")
public class Kontakt {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "mIme")
    private String mIme;

    @DatabaseField(columnName = "mPrezime")
    private String mPrezime;

    @DatabaseField(columnName = "mAdresa")
    private String mAdresa;

    @DatabaseField(columnName = "mSlika")
    private String mSlika;

    @ForeignCollectionField(foreignFieldName = "kontakt",eager = true)
    private ForeignCollection<Broj> broj;

    public Kontakt() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmIme() {
        return mIme;
    }

    public void setmIme(String mIme) {
        this.mIme = mIme;
    }

    public String getmPrezime() {
        return mPrezime;
    }

    public void setmPrezime(String mPrezime) {
        this.mPrezime = mPrezime;
    }

    public String getmAdresa() {
        return mAdresa;
    }

    public void setmAdresa(String mAdresa) {
        this.mAdresa = mAdresa;
    }

    public String getmSlika() {
        return mSlika;
    }

    public void setmSlika(String mSlika) {
        this.mSlika = mSlika;
    }

    public ForeignCollection<Broj> getBroj() {
        return broj;
    }

    public void setBroj(ForeignCollection<Broj> broj) {
        this.broj = broj;
    }

    @Override
    public String toString() {
        return mIme + "" + mPrezime;
    }
}
