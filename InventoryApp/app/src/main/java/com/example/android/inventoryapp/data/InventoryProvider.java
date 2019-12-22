package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * {@link ContentProvider} fot the Inventory app.
 */
public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log message.
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the inventory table.
     */
    private static final int INVENTORY = 100;

    /**
     * URI matcher code for the content URI fot a singe item in the inventory table.
     */
    private static final int INVENTORY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.inventoryapp/inventory"
        // will map to the integer code {@link #INVENTORY}.
        // This URI is used to provide access to MULTIPLE rows of the inventory table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY, INVENTORY);

        // The content URI of the form "content://com.example.android.inventoryapp/inventory/#"
        // will map to the integer code {@link #INVENTORY_ID}.
        // This URI is used to provide access to ONE single row of the inventory table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    /**
     * Database helper object.
     */
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        // Get the readable database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For the INVENTORY code, query the table directly with the given arguments.
                // The cursor could contain multiple rows of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                // For the INVENTORY_ID code, extract out the ID from the URI first.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the table where the _id equals {@link selectionArgs}
                // to return a Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // Only accept the INVENTORY code when insert a item to the database.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Call a helper method to perform the insert with the necessary arguments.
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Helper method that insert a item into the database with the given content values.
     *
     * @return the new content URI for that specific row in the database.
     */
    private Uri insertItem(Uri uri, ContentValues contentValues) {
        // Check that the image is not null.
        String image = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Item requires an image.");
        }

        // Check that the name is not null.
        String name = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name.");
        }

        // Check that the remaining number is valid.
        Long numberRemaining = contentValues.getAsLong(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
        if (numberRemaining == null || numberRemaining < 0) {
            throw new IllegalArgumentException("Item requires valid remaining number.");
        }

        // Check that the sold number is valid.
        Long numberSold = contentValues.getAsLong(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
        if (numberSold == null || numberSold < 0) {
            throw new IllegalArgumentException("Item requires valid sold number.");
        }

        // Check that the price is valid.
        String price = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
        if (price == null || Double.valueOf(price) < 0) {
            throw new IllegalArgumentException("Item requires valid price.");
        }

        // Get the writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new item with the given values.
        long id = database.insert(InventoryEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed and log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For the INVENTORY code, Call a helper method to perform the update directly
                // with the necessary arguments.
                return updateItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the INVENTORY_ID code, extract out the ID from the URI first.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Then call a helper method to perform the update with the necessary arguments.
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Helper method that update the item in the database with the given content values.
     *
     * @return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues contentValues,
                           String selection, String[] selectionArgs) {
        // If there are no values to update, return early with 0 which means no rows are updated.
        if (contentValues.size() == 0) {
            return 0;
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_IMAGE} key is present,
        // check that the image value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_ITEM_IMAGE)) {
            String image = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Item requires a name.");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_NAME} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name.");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_NUMBER_REMAINING} key is present,
        // check that the remaining number value is valid.
        if (contentValues.containsKey(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING)) {
            Long numberRemaining =
                    contentValues.getAsLong(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
            if (numberRemaining == null || numberRemaining < 0) {
                throw new IllegalArgumentException("Item requires valid remaining number.");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_NUMBER_SOLD} key is present,
        // check that the sold number value is valid.
        if (contentValues.containsKey(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD)) {
            Long numberSold = contentValues.getAsLong(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
            if (numberSold == null || numberSold < 0) {
                throw new IllegalArgumentException("Item requires valid sold number.");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_PRICE} key is present,
        // check that the price value is valid.
        if (contentValues.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            String price = contentValues.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null || Double.valueOf(price) < 0) {
                throw new IllegalArgumentException("Item requires valid price.");
            }
        }

        // Get the writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues,
                selection, selectionArgs);

        // If one or more rows were updated, then notify all listeners that
        // the data at the given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated.
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get the writable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection and selection args.
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If one or more rows were deleted, then notify all listeners that
        // the data at the given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted.
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
