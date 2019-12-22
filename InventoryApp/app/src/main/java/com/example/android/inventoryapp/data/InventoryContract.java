package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class InventoryContract {

    /**
     * To prevent accidentally instantiating the contract class, so give it a empty constructor.
     */
    private InventoryContract() {
    }

    /**
     * Set the content authority string to the package name for the app,
     * which is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Use {@link #CONTENT_AUTHORITY} to create the base of all URI's,
     * which apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's).
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * The content URI to access the inventory data in the provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * URI for the internal drawable of no image provided.
         */
        public static final Uri NO_IMAGE_AVAILABLE_URI =
                Uri.withAppendedPath(Uri.parse("android.resource://" + CONTENT_AUTHORITY),
                        "drawable/no_image_available");

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of inventory.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for inventory.
         */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the item (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Image of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_IMAGE = "image";

        /**
         * Name of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME = "name";

        /**
         * Remaining number of the item.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_NUMBER_REMAINING = "number_remaining";

        /**
         * Sold number of the item.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_NUMBER_SOLD = "number_sold";

        /**
         * Price of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_PRICE = "price";
    }
}
