package itse560.znn.dailyverse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import itse560.znn.dailyverse.data.VersesContract.VerseEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class VersesDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = VersesDBHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelter.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link VersesDBHelper}.
     *
     * @param context of the app
     */
    public VersesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + VerseEntry.TABLE_NAME + " ("
                + VerseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VerseEntry.COLUMN_VERSE_TEXT + " TEXT NOT NULL, "
                + VerseEntry.COLUMN_VERSE_REF + " TEXT, "
                + VerseEntry.COLUMN_VERSE_VERSION + " TEXT NOT NULL, "
                + VerseEntry.COLUMN_VERSE_TIME + " TEXT NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
