package com.example.imenik.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.example.imenik.db.model.Broj;
import com.example.imenik.db.model.Kontakt;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "baza.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<Kontakt, Integer> kontaktDao = null;
    private Dao<Broj, Integer> brojDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Broj.class);
            TableUtils.createTable(connectionSource, Kontakt.class);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Broj.class, true);
            TableUtils.dropTable(connectionSource, Kontakt.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Kontakt, Integer> getKontaktDao() throws SQLException {
        if (kontaktDao == null) {
            kontaktDao = getDao(Kontakt.class);
        }

        return kontaktDao;
    }
    public Dao<Broj, Integer> getBrojDao() throws SQLException {
        if (brojDao == null) {
            brojDao = getDao(Broj.class);
        }

        return brojDao;
    }

    @Override
    public void close() {
        brojDao = null;
        kontaktDao = null;
        super.close();
    }
}
