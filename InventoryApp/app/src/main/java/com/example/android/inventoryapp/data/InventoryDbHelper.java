package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Database helper for the Inventory app. Manage database creation and version management.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. Must increment it when the database schema changed.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InventoryDbHelper}.
     *
     * @param context of the app
     */
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table.
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_ITEM_IMAGE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_NUMBER_SOLD + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_ITEM_PRICE + " TEXT NOT NULL);";

        // Execute the SQL statement.
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create a String that contains the SQL statement to drop the old table.
        String SQL_DROP_INVENTORY_TABLE = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME + ";";

        // Execute the SQL statement.
        db.execSQL(SQL_DROP_INVENTORY_TABLE);

        // Create a new table.
        onCreate(db);
    }
}
