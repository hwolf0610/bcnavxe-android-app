package com.crittermap.backcountrynavigator.xe.controller.database;

import android.database.sqlite.SQLiteDatabase;

import com.crittermap.backcountrynavigator.xe.share.Logger;

import java.io.File;


public abstract class BC_AbstractDatabaseHelper implements AutoCloseable {
    private String DATABASE_PATH = "";
    private String DATABASE_NAME = "";
    protected SQLiteDatabase sqLiteDatabase;

    protected BC_AbstractDatabaseHelper(String dbPath, String dbName, metadata metadata) {
        this.DATABASE_NAME = dbName;
        this.DATABASE_PATH = dbPath;
        File file = new File(dbPath);
        if (!file.exists()) {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            onCreate(sqLiteDatabase, metadata);
            //Logger.d("openOrCreateDatabase " + DATABASE_NAME);
        } else {
            sqLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            //Logger.d("openDatabase " + DATABASE_NAME);
        }
    }




    protected abstract void onCreate(SQLiteDatabase sqLiteDatabase, metadata metadata);

    public String getDATABASE_NAME() {
        return DATABASE_NAME;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
            Logger.d("getSqLiteDatabase", "open DB" + DATABASE_NAME);
        }

        return sqLiteDatabase;
    }

    public void open() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(this.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            //
            // Logger.d("getWritableDatabase", "open DB" + DATABASE_NAME);
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        File file = new File(DATABASE_PATH);
        if (!file.exists()) {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH, null);
        }

        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
            Logger.d("getWritableDatabase", "close exist DB: " + DATABASE_NAME);

        }
        sqLiteDatabase = SQLiteDatabase.openDatabase(this.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        Logger.d("getWritableDatabase", "open DB: " + DATABASE_NAME);
        return sqLiteDatabase;
    }


    public boolean dropDatabse() {
        boolean result = false;
        Logger.d("dropDatabse " + DATABASE_NAME);

        File file = new File(DATABASE_PATH);
        if (file.exists()) {
            result = file.delete();
        }

        File journal = new File(DATABASE_PATH + "-journal");
        if (journal.exists()) {
            result = journal.delete();
        }
        return result;
    }

    @Override
    public void close() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
            Logger.d("getWritableDatabase", "close exist DB" + DATABASE_NAME);
        }
    }
}
